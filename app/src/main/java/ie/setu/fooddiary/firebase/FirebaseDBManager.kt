package ie.setu.fooddiary.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import ie.setu.fooddiary.models.ExperienceModel
import ie.setu.fooddiary.models.ExperienceStore
import timber.log.Timber

object FirebaseDBManager : ExperienceStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun findAll(experienceList: MutableLiveData<List<ExperienceModel>>) {
        TODO("Not yet implemented")
    }

    override fun findAll(userid: String, experienceList: MutableLiveData<List<ExperienceModel>>) {

        database.child("user-experiences").child(userid)
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
                    database.child("user-experiences").child(userid)
                        .removeEventListener(this)

                    experienceList.value = localList
                }
            })
    }

    override fun findById(
        userid: String,
        experienceId: String,
        experience: MutableLiveData<ExperienceModel>
    ) {
        database.child("user-experiences").child(userid)
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

    override fun delete(userid: String, experienceId: String) {

        val childDelete: MutableMap<String, Any?> = HashMap()
        childDelete["/experiences/$experienceId"] = null
        childDelete["/user-experiences/$userid/$experienceId"] = null

        database.updateChildren(childDelete)
    }

    override fun update(userid: String, experienceId: String, experience: ExperienceModel) {

        val experienceValues = experience.toMap()

        val childUpdate: MutableMap<String, Any?> = HashMap()
        childUpdate["experiences/$experienceId"] = experienceValues
        childUpdate["user-experiences/$userid/$experienceId"] = experienceValues

        database.updateChildren(childUpdate)
    }
}