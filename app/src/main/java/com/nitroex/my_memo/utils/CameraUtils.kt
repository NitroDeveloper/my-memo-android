package com.nitroex.my_memo.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.nitroex.my_memo.BaseActivity
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.utils.FileUtils.resizeImage
import com.nitroex.my_memo.utils.listener.OnCameraCallbackListener
import com.nitroex.my_memo.utils.listener.OnResizeImageListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import pl.aprilapps.easyphotopicker.*
import java.io.File

// Dialog Select Picture And Check Permission
class CameraUtils : BaseActivity(){
    private lateinit var context: Context
    private lateinit var listener : OnCameraCallbackListener
    private var instance: CameraUtils? = null
    private var easyImage: EasyImage? = null

    fun newInstance(): CameraUtils {
        if (instance == null) instance = CameraUtils()
        return instance as CameraUtils
    }

    fun setEasyImage(context: Context) {
        this.context = context
        easyImage = EasyImage.Builder(context)
            .setChooserTitle("Pick media")
            .setCopyImagesToPublicGalleryFolder(false)
            //.setChooserType(ChooserType.CAMERA_AND_DOCUMENTS)
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .setFolderName("Picture")
            .allowMultiple(false)
            .build()
    }

    fun showDialogSelectPic(listener: OnCameraCallbackListener) {
        this.listener = listener
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.please_select_camera))
        val animals = arrayOf(context.getString(R.string.choose_camera), context.getString(R.string.chose_album))
        builder.setItems(animals) { _, which ->
            when (which) {
                0 -> checkPermissionCamera()
                1 -> checkPermissionWriteStorage()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun checkPermissionCamera() {
        Dexter.withActivity(context as Activity?)
            .withPermissions(Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener {

                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        easyImage!!.openCameraForImage(context as Activity)
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .check()
    }

    private fun checkPermissionWriteStorage() {
        Dexter.withActivity(context as Activity?)
            .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {

                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        easyImage!!.openGallery(context as Activity)
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .check()
    }

    fun setActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        easyImage!!.handleActivityResult(requestCode, resultCode, data, context as Activity, object : DefaultCallback() {
            override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                for (file: MediaFile in imageFiles) {
                    isLogDebug(Configs.EasyImageCache, "Image file returned: $file")
                }
                onPhotosReturned(imageFiles)
            }
            override fun onImagePickerError(error: Throwable, source: MediaSource) { //Some error handling
                error.printStackTrace()
                listener.onCameraError(error.toString())
            }
            override fun onCanceled(source: MediaSource) { //Not necessary to remove any files manually anymore
            }
        })
    }

    private fun onPhotosReturned(returnedPhotos: Array<MediaFile>) {
        resizeImage(context, returnedPhotos, Configs.ImgSizeProfile,
            object : OnResizeImageListener {
                override fun onSuccessResult(file: File, value: String) {
                    listener.onCameraSuccess(file.path)
                }
                override fun onErrorResult(msg: String) {
                    listener.onCameraError(msg)
                }
            })
    }
}