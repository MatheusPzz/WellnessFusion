//package com.example.wellnessfusionapp
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import com.google.firebase.Firebase
//import com.google.firebase.FirebaseApp
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.firestore
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//
//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//    override fun onCreate() {
//        super.onCreate()
//        FirebaseApp.initializeApp(this)
//    }
//
//    private fun saveFCMTokenForUser(token: String) {
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//        if (userId != null) {
//            val userRef = Firebase.firestore.collection("Users").document(userId)
//
//            userRef.update("fcmToken", token)
//                .addOnSuccessListener { Log.d("FCM", "FCM Token updated for user $userId") }
//                .addOnFailureListener { e ->
//                    Log.w(
//                        "FCM",
//                        "Error updating FCM Token for user $userId",
//                        e
//                    )
//                }
//        }
//    }
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//        Log.d("FCM", "Refreshed token: $token")
//        saveFCMTokenForUser(token)
//
//    }
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        // Diferencia os tipos de notificações pelo uso de dados adicionais que você enviar junto com a notificação
//        val type = remoteMessage.data["type"]
//
//        when (type) {
//            "goalCompleted" -> {
//                // Trata a notificação de meta completada de maneira específica
//                val goalName = remoteMessage.data["goalName"] ?: "Sua Meta"
//                val typeId = remoteMessage.data["typeId"] ?: ""
//                val description = remoteMessage.data["description"] ?: ""
//                handleGoalCompletion(goalName, typeId, description)
//            }
//
//            else -> {
//                // Tratamento padrão para outras notificações
//                remoteMessage.notification?.let {
//                    sendNotification(
//                        it.title ?: "Wellness App",
//                        it.body ?: "Você tem uma nova mensagem"
//                    )
//                }
//            }
//        }
//    }
//
//    private fun handleGoalCompletion(goalName: String, typeId: String, description: String) {
//        // Cria uma intenção genérica para reabrir o aplicativo
//        val intent = Intent(this, MainActivity::class.java).apply {
//            // FLAG_ACTIVITY_CLEAR_TOP garante que se a tarefa já está rodando,
//            // ela é trazida à frente e limpa as outras atividades.
//            // FLAG_ACTIVITY_SINGLE_TOP garante que a MainActivity não seja recriada
//            // se ela já estiver rodando no topo da pilha.
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//        }
//        // Personaliza a mensagem de notificação
//        val message =
//            "Parabéns, sua meta para $typeId sobre $description foi alcançada com sucesso! Continue se desafiando!"
//
//        // Chama o método para enviar a notificação
//        sendNotification("Meta Concluída!", message, intent)
//    }
//
//    private fun sendNotification(title: String, messageBody: String, intent: Intent? = null) {
//        val pendingIntent =
//            PendingIntent.getActivity(this, 0, intent ?: Intent(), PendingIntent.FLAG_IMMUTABLE)
//
//        val channelId = getString(R.string.app_name)
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle(title)
//            .setContentText(messageBody)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Para Oreo ou superior, é necessário um canal de notificação.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Wellness App Notifications",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(0, notificationBuilder.build())
//    }
//}