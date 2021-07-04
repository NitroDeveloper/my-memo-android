package com.nitroex.my_memo.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import com.nitroex.my_memo.BaseActivity
import com.nitroex.my_memo.utils.attachFile.adapter.AttachFileAdapter
import com.nitroex.my_memo.utils.attachFile.model.AttachFile
import com.nitroex.my_memo.utils.listener.OnResizeImageListener
import pl.aprilapps.easyphotopicker.MediaFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URLDecoder
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

object FileUtils {

    fun resizeImage(context: Context, returnedPhotos: Array<MediaFile>, sizeRequest: Int, listener: OnResizeImageListener) {
        val baseAc = (context as BaseActivity)
        var bitmap: Bitmap
        val fileName = getFileNameFromUrl(returnedPhotos[0].file.path)
        val file = returnedPhotos[0].file

        try {
            bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source: ImageDecoder.Source = ImageDecoder.createSource(context.contentResolver, file.toUri())
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, file.toUri())
            }
            // เช็คขนาดรูปภาพถ้าเล็กกว่าขนาดที่ต้องการต้อง resize
            val sizePicMinimum = if (bitmap.width < bitmap.height) bitmap.width else bitmap.height
            baseAc.isLogDebug("resizeImage", "original bitmap --->> width " + bitmap.width + " height " + bitmap.height)

            if (sizePicMinimum > sizeRequest) {

                ///////////////// Resize /////////////////

                val widthRatio = (bitmap.width / bitmap.width).toFloat()
                val heightRatio: Float = sizeRequest.toFloat() / bitmap.height

                val newWidth: Int
                val newHeight: Int
                if (widthRatio > heightRatio) {
                    newWidth = (bitmap.width * heightRatio).roundToInt()
                    newHeight = (bitmap.height * heightRatio).roundToInt()
                } else {
                    newWidth = (bitmap.width * widthRatio).roundToInt()
                    newHeight = (bitmap.height * widthRatio).roundToInt()
                }
                bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    bitmap = pictureTurn(bitmap, file.toUri())
                }

                val outStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream)
                val fileCacheDir = File(context.cacheDir.toString(), Configs.EasyImageCache)
                val filePic = File(fileCacheDir.path + File.separator + fileName + ".jpg")

                if (!fileCacheDir.exists()) {
                    if (!fileCacheDir.mkdirs()) return
                }
                filePic.createNewFile()

                val stream = FileOutputStream(filePic)
                stream.write(outStream.toByteArray())
                stream.close()
                baseAc.isLogDebug("resizeImage", "new create bitmap ->> width " + bitmap.width + " height " + bitmap.height)

                listener.onSuccessResult(filePic,"")
                baseAc.isLogDebug("resizeImage", filePic.path.toString())

                ///////////////// Resize /////////////////
            }else{
                listener.onSuccessResult(file,"")
                baseAc.isLogDebug("resizeImage", file.path.toString())
            }

        } catch (e: Exception) {
            baseAc.showDialogAlert(false, "resize image : error")
            listener.onErrorResult(e.message.toString())
        }
    }

    private fun pictureTurn(img : Bitmap, uri : Uri) : Bitmap {
        val matrix = Matrix()
        try {
            val exif = ExifInterface(uri.path!!)
            val attribute = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val rotation = exifToDegrees(attribute)
            if (rotation != 0) {
                matrix.postRotate(rotation.toFloat())
            }
        } catch (e: Exception) { }
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }

    private fun exifToDegrees(exifOrientation: Int): Int {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    fun clearImageCache(context: Context) {
        clearImageCache(context, Configs.EasyImage)
        clearImageCache(context, Configs.EasyImageCache)
    }

    fun clearImageCache(context: Context, strFolder: String) {
        val fileDir = File(context.cacheDir.toString(), strFolder)
        clearFolderImageCache(fileDir)
    }

    private fun clearFolderImageCache(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory)
            for (child in fileOrDirectory.listFiles()!!)
                clearFolderImageCache(child)
        try {
            fileOrDirectory.delete()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun isButtonAddFile(model: ArrayList<AttachFile>, numMaxFile: Int): Boolean {
        if (model.size < numMaxFile){
            for (i in model.indices){
                if (model[i].type_id == AttachFileAdapter.ADD) {
                    return true
                }
            }
        }
        return false
    }

    fun isListSize(model: ArrayList<AttachFile>): Int {
        val sizeList = model.size
        for (i in model.indices){
            if (model[i].type_id == AttachFileAdapter.ADD) {
                return sizeList-1
            }
        }
        return sizeList
    }

    /////////////////////////////////////////////////// setPathToModel ///////////////////////////////////////////

    fun setPathToModel(path: String, attachFile: ArrayList<AttachFile>, numMaxFile:Int): ArrayList<AttachFile> {
        var isPass = true

        if (path.isNotEmpty()) {
            for (i in attachFile.indices) {
                if (attachFile[i].path.toString() == path) {
                    isPass = false
                }
            }
            if (isPass) {
                val list = ArrayList<AttachFile>()
                if (attachFile.size > 1) {
                    for (i in 0 until attachFile.size-1) { list.add(attachFile[i]) }
                    list.add(AttachFile(0, AttachFileAdapter.IMAGE, path,"","","",0,0,""))
                } else {
                    list.add(AttachFile(0, AttachFileAdapter.IMAGE, path,"","","",0,0,""))
                }
                if (list.size < numMaxFile) {
                    list.add(AttachFile(0, AttachFileAdapter.ADD, "","","","",0,0,""))
                }
                return list
            }
        }
        return attachFile
    }
    fun setPathToModel(model: AttachFile, attachFile: ArrayList<AttachFile>, numMaxFile:Int): ArrayList<AttachFile> {
        var isPass = true

        if (model.path.isNotEmpty()) {
            for (i in attachFile.indices) {
                if (attachFile[i].path == model.path) {
                    isPass = false
                }
            }
            if (isPass) {
                val list = ArrayList<AttachFile>()
                if (attachFile.size > 1) {
                    for (i in 0 until attachFile.size-1) { list.add(attachFile[i]) }
                    list.add(AttachFile(model.id, AttachFileAdapter.IMAGE, model.path, model.file_name, model.file_type, model.thumb_path, model.employee_id, model.company_id, model.attach_info))
                } else {
                    list.add(AttachFile(model.id, AttachFileAdapter.IMAGE, model.path, model.file_name, model.file_type, model.thumb_path, model.employee_id, model.company_id, model.attach_info))
                }
                if (list.size < numMaxFile) {
                    list.add(AttachFile(0, AttachFileAdapter.ADD, "","","","",0,0,""))
                }
                return list
            }
        }
        return attachFile
    }
    /////////////////////////////////////////////////// setPathToModel ///////////////////////////////////////////

    private fun getFileNameFromUrl(fullPath: String): String {
        val fileName = fullPath.substringBeforeLast(".")
        val txt: String = URLDecoder.decode(fileName, "UTF-8")
        return txt.substringAfterLast("/")
    }

}