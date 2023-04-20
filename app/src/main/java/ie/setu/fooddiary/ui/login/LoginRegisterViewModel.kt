package ie.setu.fooddiary.ui.login

import android.app.Application
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import ie.setu.fooddiary.firebase.FirebaseAuthManager
import timber.log.Timber

class LoginRegisterViewModel(app: Application) : AndroidViewModel(app) {
    var firebaseAuthManager: FirebaseAuthManager = FirebaseAuthManager(app)
    var liveFirebaseUser: MutableLiveData<FirebaseUser> = firebaseAuthManager.liveFirebaseUser

    fun login(email: String?, password: String?) {
        firebaseAuthManager.login(email, password)
    }

    fun googleSignIn(result: ActivityResult, onComplete: (Boolean) -> Unit) {
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                // Use account to sign in to Firebase
                val idToken = account.idToken
                firebaseAuthManager.googleSignIn(idToken) { isSuccessful ->
                    onComplete.invoke(isSuccessful)
                }
            } catch (e: ApiException) {
                Timber.w("Google sign in failed: ${e.statusCode}")
            }
        }
    }

    fun register(email: String?, password: String?) {
        firebaseAuthManager.register(email, password)
    }
}