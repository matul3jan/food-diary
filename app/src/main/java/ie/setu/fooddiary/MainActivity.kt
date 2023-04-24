package ie.setu.fooddiary

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.libraries.places.api.Places
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import ie.setu.fooddiary.databinding.ActivityMainBinding
import ie.setu.fooddiary.databinding.NavHeaderMainBinding
import ie.setu.fooddiary.ui.login.LoggedInViewModel
import ie.setu.fooddiary.ui.login.LoginActivity
import ie.setu.fooddiary.utils.Theme
import ie.setu.fooddiary.utils.applyTheme
import ie.setu.fooddiary.utils.isSystemInDarkMode

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var homeBinding: ActivityMainBinding
    private lateinit var navHeaderBinding: NavHeaderMainBinding
    private lateinit var loggedInViewModel: LoggedInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)

        setSupportActionBar(homeBinding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = homeBinding.drawerLayout
        val navView: NavigationView = homeBinding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        Places.initialize(applicationContext, getString(R.string.google_maps_key))

        handleThemeToggle()
    }

    private fun handleThemeToggle() {

        val switch =
            homeBinding.navView.menu.findItem(R.id.nav_dark_mode).actionView as SwitchCompat

        switch.isChecked = isSystemInDarkMode()

        switch.setOnClickListener {
            if (switch.isChecked) {
                applyTheme(Theme.DARK)
            } else {
                applyTheme(Theme.LIGHT)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    public override fun onStart() {
        super.onStart()
        loggedInViewModel = ViewModelProvider(this)[LoggedInViewModel::class.java]
        loggedInViewModel.liveFirebaseUser.observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                updateNavHeader(loggedInViewModel.liveFirebaseUser.value!!)
            }
        }
        loggedInViewModel.loggedOut.observe(this) { loggedOut ->
            if (loggedOut) {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    private fun updateNavHeader(currentUser: FirebaseUser) {
        val headerView = homeBinding.navView.getHeaderView(0)
        navHeaderBinding = NavHeaderMainBinding.bind(headerView)
        navHeaderBinding.navHeaderEmail.text = currentUser.email
        navHeaderBinding.navHeaderName.text =
            getString(R.string.nav_header_title, currentUser.displayName)

        if (currentUser.photoUrl != null) {
            Glide.with(this)
                .load(currentUser.photoUrl)
                .placeholder(R.drawable.ic_image_placeholder) // placeholder
                .error(R.drawable.ic_image_error) // error image if load fails
                .override(200, 200)
                .transform(CircleCrop())
                .into(navHeaderBinding.imageView)
        }
    }

    fun signOut(@Suppress("UNUSED_PARAMETER") item: MenuItem) {
        loggedInViewModel.logOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}