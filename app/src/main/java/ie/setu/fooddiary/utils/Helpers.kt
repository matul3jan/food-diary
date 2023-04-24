package ie.setu.fooddiary.utils

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import ie.setu.fooddiary.R

fun createLoader(activity: FragmentActivity): AlertDialog {
    val loaderBuilder = AlertDialog.Builder(activity)
        .setCancelable(true)
        .setView(R.layout.loading)
    val loader = loaderBuilder.create()
    loader.setTitle(R.string.app_name)
    loader.setIcon(R.mipmap.ic_launcher_round)
    return loader
}

fun showLoader(loader: AlertDialog, message: String) {
    if (!loader.isShowing) {
        loader.setTitle(message)
        loader.show()
    }
}

fun hideLoader(loader: AlertDialog) {
    if (loader.isShowing)
        loader.dismiss()
}

fun resetMap(map: GoogleMap) {
    map.clear()
    val defaultLatLng = LatLng(0.0, 0.0)
    map.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 0F))
}

fun fitMarkersInMap(markers: MutableList<Marker?>, map: GoogleMap) {
    val builder = LatLngBounds.builder()
    for (marker in markers) {
        builder.include(marker!!.position)
    }
    val bounds = builder.build()
    val padding = 200 // offset from edges of the map in pixels
    val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
    map.animateCamera(cu)
}

fun applyTheme(theme: Theme) {
    when (theme) {
        Theme.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Theme.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}

fun isSystemInDarkMode(): Boolean {
    return when (AppCompatDelegate.getDefaultNightMode()) {
        AppCompatDelegate.MODE_NIGHT_YES -> true
        AppCompatDelegate.MODE_NIGHT_NO -> false
        else -> false
    }
}