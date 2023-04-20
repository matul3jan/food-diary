package ie.setu.fooddiary.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface ExperienceStore {
    fun findAll(experienceList: MutableLiveData<List<ExperienceModel>>)
    fun findAll(userid: String, experienceList: MutableLiveData<List<ExperienceModel>>)
    fun findById(userid: String, experienceId: String, experience: MutableLiveData<ExperienceModel>)
    fun create(firebaseUser: MutableLiveData<FirebaseUser>, experience: ExperienceModel)
    fun delete(userid: String, experienceId: String)
    fun update(userid: String, experienceId: String, experience: ExperienceModel)
}