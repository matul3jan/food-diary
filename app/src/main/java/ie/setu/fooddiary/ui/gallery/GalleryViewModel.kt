package ie.setu.fooddiary.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.setu.fooddiary.firebase.FirebaseDBManager
import ie.setu.fooddiary.models.ExperienceModel
import timber.log.Timber

class GalleryViewModel : ViewModel() {

    private val experienceList = MutableLiveData<List<ExperienceModel>>()
    val observableExperienceList: LiveData<List<ExperienceModel>> get() = experienceList
    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    init {
        load()
    }

    fun load() {
        try {
            FirebaseDBManager.findAll(liveFirebaseUser.value?.uid!!, experienceList)
            Timber.i("Report Load Success : ${experienceList.value.toString()}")
        } catch (e: Exception) {
            Timber.i("Report Load Error : $e.message")
        }
    }

    fun delete(userId: String, id: String, imageUrl: String) {
        try {
            FirebaseDBManager.delete(userId, id, imageUrl)
            load()
            Timber.i("Experience Delete Success")
        } catch (e: Exception) {
            Timber.i("Experience Delete Error : $e.message")
        }
    }
}