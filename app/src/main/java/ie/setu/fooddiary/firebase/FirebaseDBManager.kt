package ie.setu.fooddiary.firebase

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import ie.setu.fooddiary.models.ExperienceModel
import ie.setu.fooddiary.models.ExperienceStore
import timber.log.Timber
import java.util.*

object FirebaseDBManager : ExperienceStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun findAll(experienceList: MutableLiveData<List<ExperienceModel>>) {
        TODO("Not yet implemented")
    }

    override fun findAll(userId: String, experienceList: MutableLiveData<List<ExperienceModel>>) {

        database.child("user-experiences").child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Experience error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<ExperienceModel>()
                    val children = snapshot.children
                    children.forEach {
                        val experience = it.getValue(ExperienceModel::class.java)
                        localList.add(experience!!)
                    }
                    database.child("user-experiences").child(userId)
                        .removeEventListener(this)

                    experienceList.value = localList
                }
            })
    }

    override fun findById(
        userId: String,
        experienceId: String,
        experience: MutableLiveData<ExperienceModel>,
    ) {
        database.child("user-experiences").child(userId)
            .child(experienceId).get().addOnSuccessListener {
                experience.value = it.getValue(ExperienceModel::class.java)
                Timber.i("firebase Got value ${it.value}")
            }.addOnFailureListener {
                Timber.e("firebase Error getting data $it")
            }
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, experience: ExperienceModel) {
        Timber.i("Firebase DB Reference : $database")

        val uid = firebaseUser.value!!.uid
        val key = database.child("experiences").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        experience.uid = key
        val experienceValues = experience.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/experiences/$key"] = experienceValues
        childAdd["/user-experiences/$uid/$key"] = experienceValues

        database.updateChildren(childAdd)
    }

    override fun delete(userId: String, experienceId: String, imageUrl: String) {

        val childDelete: MutableMap<String, Any?> = HashMap()
        childDelete["/experiences/$experienceId"] = null
        childDelete["/user-experiences/$userId/$experienceId"] = null

        database.updateChildren(childDelete)

        if (imageUrl.isNotEmpty()) {
            deleteImage(imageUrl)
        }
    }

    override fun update(userId: String, experienceId: String, experience: ExperienceModel) {

        val experienceValues = experience.toMap()

        val childUpdate: MutableMap<String, Any?> = HashMap()
        println("-----------------------------------------------------------------------------------------------")
        println("experiences/$experienceId")
        println("user-experiences/$userId/$experienceId")
        childUpdate["experiences/$experienceId"] = experienceValues
        childUpdate["user-experiences/$userId/$experienceId"] = experienceValues

        database.updateChildren(childUpdate)
    }

    override fun upload(uri: Uri?, callback: (imageUrl: String) -> Unit) {

        if (uri == null) {
            callback("")
            return
        }

        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance()
            .reference
            .child("images/$fileName")

        refStorage.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    callback.invoke(it.toString())
                }
            }.addOnFailureListener { e ->
                callback.invoke("")
                Timber.e("Error uploading image to firebase storage: ${e.message}")
            }
    }

    private fun deleteImage(imageUrl: String) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageRef.delete()
            .addOnSuccessListener {
                Timber.e("Success deleting image from Firebase Storage")
            }
            .addOnFailureListener {
                Timber.e("Error deleting image from Firebase Storage: ${it.message}")
            }
    }
}