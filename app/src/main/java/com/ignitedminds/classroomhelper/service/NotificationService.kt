package com.ignitedminds.classroomhelper.service

import android.content.Intent
import android.util.Log
import androidx.fragment.app.viewModels
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ignitedminds.classroomhelper.App
import com.ignitedminds.classroomhelper.ui.ClassroomViewModel


class NotificationService : FirebaseMessagingService() {

    companion object{
        public val TOKEN_BROADCAST = "FcmTokenBroadcast"
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        Log.d("FirebaseService", "Refreshed token: $p0")

    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.d("FirebaseService", "onMessageReceived: ${p0.data}")
       // applicationContext.sendBroadcast(Intent(TOKEN_BROADCAST))
    }

}