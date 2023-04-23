package ie.setu.fooddiary.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ie.setu.fooddiary.MainActivity
import ie.setu.fooddiary.R
import ie.setu.fooddiary.databinding.ActivityLoginBinding
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    private lateinit var loginRegisterViewModel: LoginRegisterViewModel
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        loginRegisterViewModel = ViewModelProvider(this)[LoginRegisterViewModel::class.java]

        setButtonHandlers()
        setAuthListeners()
        setResultLauncher()

        setContentView(loginBinding.root)
    }

    private fun setButtonHandlers() {
        loginBinding.emailSignInButton.setOnClickListener {
            signIn(
                loginBinding.fieldEmail.text.toString(),
                loginBinding.fieldPassword.text.toString()
            )
        }
        loginBinding.emailCreateAccountButton.setOnClickListener {
            createAccount(
                loginBinding.fieldEmail.text.toString(),
                loginBinding.fieldPassword.text.toString()
            )
        }
        loginBinding.googleSignInButton.setOnClickListener {
            googleSignIn()
        }
    }

    private fun setAuthListeners() {
        loginRegisterViewModel.liveFirebaseUser.observe(this) {
            if (it != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        loginRegisterViewModel.firebaseAuthManager.errorStatus.observe(this) {
            if (it) {
                Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun setResultLauncher() {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                loginRegisterViewModel.googleSignIn(result) { success ->
                    if (success) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
    }

    private fun createAccount(email: String, password: String) {
        Timber.d("createAccount:$email")
        if (!validateForm()) return
        loginRegisterViewModel.register(email, password)
    }

    private fun signIn(email: String, password: String) {
        Timber.d("signIn:$email")
        if (!validateForm()) return
        loginRegisterViewModel.login(email, password)
    }

    private fun googleSignIn() {
        val googleSignInIntent = loginRegisterViewModel.firebaseAuthManager.getGoogleSignInIntent()
        resultLauncher.launch(googleSignInIntent)
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = loginBinding.fieldEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            loginBinding.fieldEmail.error = "Required."
            valid = false
        } else {
            loginBinding.fieldEmail.error = null
        }

        val password = loginBinding.fieldPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            loginBinding.fieldPassword.error = "Required."
            valid = false
        } else {
            loginBinding.fieldPassword.error = null
        }
        return valid
    }


}
