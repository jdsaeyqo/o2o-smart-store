package com.ssafy.smartstore.service

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.smartstore.activity.MainActivity
import com.ssafy.smartstore.util.FCMUtil

private const val TAG = "FirebaseMessaging"
class FirebaseMessagingService :  FirebaseMessagingService(){


    // 새로운 토큰이 생성될 때 마다 해당 콜백이 호출된다.
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // 새로운 토큰 수신 시 서버로 전송
        MainActivity.uploadToken(token)

        // 새로운 토큰 로그 남기기
        Log.d(TAG, "onNewToken: $token")
    }

    // Foreground에서 Push Service를 받기위해 Notification 설정
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: ")

        remoteMessage.notification.let { message ->
            val messageTitle = message!!.title
            val messageContent = message!!.body

            FCMUtil.setMessageToSharedPreference(messageContent.toString())

            val mainIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val mainPendingIntent =
                PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_CANCEL_CURRENT)

            val builder = NotificationCompat.Builder(this, MainActivity.channel_id)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(messageTitle)
                .setContentText(messageContent)
                .setAutoCancel(true)
                .setContentIntent(mainPendingIntent)

            NotificationManagerCompat.from(this).apply {
                notify(101, builder.build())
            }
        }
    }
}