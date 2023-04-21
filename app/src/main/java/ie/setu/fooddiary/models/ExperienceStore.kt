package ie.setu.fooddiary.models

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface ExperienceStore {
    fun findAll(experienceList: MutableLiveData<List<ExperienceModel>>)
    fun findAll(userId: String, experienceList: MutableLiveData<List<ExperienceModel>>)
    fun findById(userId: String, experienceId: String, experience: MutableLiveData<ExperienceModel>)
    fun create(firebaseUser: MutableLiveData<FirebaseUser>, experience: ExperienceModel)
    fun delete(userId: String, experienceId: String)
    fun update(userId: String, experienceId: String, experience: ExperienceModel)
    fun upload(uri: Uri?, callback: (imageUrl: String) -> Unit)
}