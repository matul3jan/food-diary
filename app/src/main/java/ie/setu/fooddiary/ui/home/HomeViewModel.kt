package ie.setu.fooddiary.ui.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.setu.fooddiary.firebase.FirebaseDBManager
import ie.setu.fooddiary.models.ExperienceModel

class HomeViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()
    val observableStatus: LiveData<Boolean> get() = status

    fun addExperience(
        firebaseUser: MutableLiveData<FirebaseUser>,
        experience: ExperienceModel,
    ) {
        status.value = try {
            FirebaseDBManager.create(firebaseUser, experience)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun editExperience(
        firebaseUser: MutableLiveData<FirebaseUser>,
        experience: ExperienceModel,
    ) {
        status.value = try {
            FirebaseDBManager.update(firebaseUser.value?.uid!!, experience.uid!!, experience)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun uploadImage(uri: Uri?, callback: (imageUrl: String) -> Unit) {
        FirebaseDBManager.upload(uri, callback)
    }
}