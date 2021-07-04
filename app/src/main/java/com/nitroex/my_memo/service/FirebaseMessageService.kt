package com.nitroex.my_memo.service
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.nitroex.memo.my_memo.R
import com.nitroex.my_memo.ui.SplashScreenActivity
import com.nitroex.my_memo.utils.Configs
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonObject


class FirebaseMessageService : FirebaseMessagingService() {
    private var isAppActive: Boolean = false
    private var title: String = ""
    private var content: String = ""
    private var system: String = ""
    private var badge: String = ""

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val sharePreSystem = getSharedPreferences(Configs.PREFS_SYSTEM, Context.MODE_PRIVATE)
        val sharePreUser = getSharedPreferences(Configs.PREFS_USER, Context.MODE_PRIVATE)
        isAppActive = sharePreSystem.getBoolean(Configs.isActive, false)

        val dTitle = remoteMessage.data["title"]
        val dContent = remoteMessage.data["content"]
        val dBadge = remoteMessage.data["badge"]
        val dSystem = remoteMessage.data["system"]

        if (dTitle!=null) { title = dTitle }
        if (dContent!=null) { content = dContent }
        if (dBadge!=null) { badge = dBadge }
        if (dSystem!=null) { system = dSystem }

        if (system=="kick") {
            content = baseContext.resources.getString(R.string.alert_user_kick)
            sendNotification(title, content)
            sharePreUser.edit().clear().apply()
            if (isAppActive) {
                val intent = Intent(Configs.NoticeKickUser)
                intent.putExtra("content", content)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
        }else{
            sendNotification(title, content)
        }
    }

    private fun sendNotification(title: String, message: String) {
        val pendingIntent: PendingIntent
        var isTitle = title
        if (title=="") isTitle = baseContext.resources.getString(R.string.app_name)
        val idRequest = System.currentTimeMillis().toInt()

        val intent = Intent(this, SplashScreenActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        pendingIntent = PendingIntent.getActivity(this, idRequest, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_ex_notice)
            .setContentTitle(isTitle)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(idRequest, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}