package dev.bragas.timecapsule.model

import com.google.firebase.database.PropertyName

data class Capsule(
    var id: String = "",
    var senderId: String = "",
    var senderEmail: String = "",
    var recipientId: String = "",
    var message: String = "",
    var createdAt: String = "",
    var readableAt: String = "",
    var isRead: Boolean = false
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "senderId" to senderId,
            "senderEmail" to senderEmail,
            "recipientId" to recipientId,
            "message" to message,
            "createdAt" to createdAt,
            "readableAt" to readableAt,
            "isRead" to isRead
        )
    }
}