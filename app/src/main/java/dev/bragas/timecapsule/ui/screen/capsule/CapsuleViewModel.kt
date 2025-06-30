package dev.bragas.timecapsule.ui.screen.capsule

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dev.bragas.timecapsule.data.FirebaseService
import dev.bragas.timecapsule.model.Capsule
import dev.bragas.timecapsule.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.OffsetDateTime
import java.time.ZoneOffset

class CapsuleViewModel : ViewModel() {
    private val _auth = FirebaseService.auth
    private val _db = FirebaseService.db

    private val _users = MutableStateFlow<List<User>>(emptyList())
    private val _recipient = MutableStateFlow("")
    private val _readableAt = MutableStateFlow("")
    private val _message = MutableStateFlow("")
    private val _sent = MutableStateFlow<List<Capsule>>(emptyList())
    private val _received = MutableStateFlow<List<Capsule>>(emptyList())
    private val _capsule = MutableStateFlow<Capsule?>(null)

    val users: StateFlow<List<User>> = _users.asStateFlow()
    val recipient: StateFlow<String> = _recipient.asStateFlow()
    val readableAt: StateFlow<String> = _readableAt.asStateFlow()
    val message: StateFlow<String> = _message.asStateFlow()
    val sent: StateFlow<List<Capsule>> = _sent.asStateFlow()
    val received: StateFlow<List<Capsule>> = _received.asStateFlow()
    val capsule: StateFlow<Capsule?> = _capsule.asStateFlow()

    init {
        fetchUsers()
        fetchRealTimeCapsules()
    }

    fun onRecipientChange(recipient: String) {
        _recipient.value = recipient
    }

    fun onReadableAtChange(readableAt: String) {
        _readableAt.value = readableAt
    }

    fun onMessageChange(message: String) {
        _message.value = message
    }

    fun saveCapsule(
        onComplete: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val capsuleId = _db.child("capsules").push().key
        val capsule = Capsule(
            id = capsuleId ?: "",
            senderId = _auth.currentUser?.uid ?: "",
            senderEmail = _auth.currentUser?.email ?: "",
            recipientId = _recipient.value,
            message = _message.value,
            createdAt = OffsetDateTime.now(ZoneOffset.UTC).toString(),
            readableAt = _readableAt.value,
            isRead = false
        )
        _db.child("capsules").child(capsuleId!!).setValue(capsule)
            .addOnSuccessListener {
                onComplete()
            }
            .addOnFailureListener { exception ->
                Log.e("CapsuleViewModel", "Error sending capsule", exception)
                onError(exception)
            }
    }

    private fun fetchUsers() {
        val currentUserUid = _auth.currentUser?.uid ?: return
        _db.child("users").get()
            .addOnSuccessListener { snapshot ->
                val userList = mutableListOf<User>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null && user.uid != currentUserUid) {
                        userList.add(user)
                    }
                }
                _users.value = userList
            }
            .addOnFailureListener { exception ->
                Log.e("CapsuleViewModel", "Error fetching users", exception)
            }
    }

//    private fun fetchCapsules() {
//        val currentUserUid = _auth.currentUser?.uid ?: return
//        _db.child("capsules").get()
//            .addOnSuccessListener { snapshot ->
//                val sentList = mutableListOf<Capsule>()
//                val receivedList = mutableListOf<Capsule>()
//                for (capsuleSnapshot in snapshot.children) {
//                    val capsule = capsuleSnapshot.getValue(Capsule::class.java)
//                    if (capsule != null) {
//                        if (capsule.senderId == currentUserUid) {
//                            sentList.add(capsule)
//                            Log.d("DEBUG", "Capsule added to sent list: $capsule")
//                        } else if (capsule.recipientId == currentUserUid) {
//                            receivedList.add(capsule)
//                        }
//                    }
//                }
//                _sent.value = sentList
//                _received.value = receivedList
//            }
//            .addOnFailureListener { exception ->
//                Log.e("CapsuleViewModel", "Error fetching capsules", exception)
//            }
//    }

    private fun fetchRealTimeCapsules() {
        val currentUserUid = _auth.currentUser?.uid ?: return
        _db.child("capsules").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val sentList = mutableListOf<Capsule>()
                    val receivedList = mutableListOf<Capsule>()
                    for (capsuleSnapshot in snapshot.children) {
                        val capsule = capsuleSnapshot.getValue(Capsule::class.java)
                        if (capsule != null) {
                            if (capsule.senderId == currentUserUid) {
                                sentList.add(capsule)
                            } else if (capsule.recipientId == currentUserUid) {
                                receivedList.add(capsule)
                            }
                        }
                    }
                    _sent.value = sentList
                    _received.value = receivedList
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("CapsuleViewModel", "Error fetching capsules", error.toException())
                }
            }
        )
    }

    fun fetchCapsuleById(id: String) {
        _db.child("capsules").child(id).get().addOnSuccessListener { snapshot ->
            val capsule = snapshot.getValue(Capsule::class.java)

            if (capsule != null && !capsule.isRead) {
                _db.child("capsules").child(id).child("read").setValue(true)
                _capsule.value = capsule!!.copy(isRead = true)
            } else {
                _capsule.value = capsule
            }

        }.addOnFailureListener { exception ->
            Log.e("CapsuleViewModel", "Error fetching capsule by ID", exception)
        }
    }

    fun markCapsuleAsRead(id: String) {
        _db.child("capsules").child(id).child("read").setValue(true)
            .addOnSuccessListener {
                Log.d("CapsuleViewModel", "Capsule marked as read successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("CapsuleViewModel", "Error marking capsule as read", exception)
            }
    }

    fun deleteCapsule(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        _db.child("capsules").child(id).removeValue()
            .addOnSuccessListener {
                { onSuccess() }
            }
            .addOnFailureListener {
                { onError(it.message ?: "Error deleting capsule") }
            }
    }

}