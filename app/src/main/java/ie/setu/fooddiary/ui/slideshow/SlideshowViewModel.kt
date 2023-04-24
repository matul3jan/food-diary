package ie.setu.fooddiary.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.setu.fooddiary.firebase.FirebaseDBManager
import ie.setu.fooddiary.models.ExperienceModel
import timber.log.Timber

class SlideshowViewModel : ViewModel() {

    private val experienceList = MutableLiveData<List<ExperienceModel>>()
    val observableExperienceList: LiveData<List<ExperienceModel>> get() = experienceList

    fun load() {
        try {
            FirebaseDBManager.findAll(experienceList)
            Timber.i("All Experiences Load Success")
        } catch (e: Exception) {
            Timber.i("All Experiences Load Error : $e.message")
        }
    }

    fun loadForUser(uid: String?) {
        try {
            FirebaseDBManager.findAll(uid!!, experienceList)
            Timber.i("User Experiences Load Success")
        } catch (e: Exception) {
            Timber.i("User Experiences Load Error : $e.message")
        }
    }
}