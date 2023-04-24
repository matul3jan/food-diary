package ie.setu.fooddiary.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import ie.setu.fooddiary.MainActivity

private lateinit var loginRegisterViewModel: LoginRegisterViewModel

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        loginRegisterViewModel = ViewModelProvider(this)[LoginRegisterViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()

        val user = loginRegisterViewModel.liveFirebaseUser.value
        if (user != null) {
            // User is signed in, Start main activity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // No user is signed in, start login activity
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}