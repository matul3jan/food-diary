package ie.setu.fooddiary.ui.home

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import ie.setu.fooddiary.MainActivity
import ie.setu.fooddiary.R
import ie.setu.fooddiary.databinding.FragmentHomeBinding
import ie.setu.fooddiary.models.ExperienceModel
import ie.setu.fooddiary.ui.login.LoggedInViewModel
import ie.setu.fooddiary.utils.getImageUri
import ie.setu.fooddiary.utils.isSystemInDarkMode
import ie.setu.fooddiary.utils.resetMap
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.*

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private var experienceModel: ExperienceModel = ExperienceModel()
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
    private var args: HomeFragmentArgs? = null

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, instanceState: Bundle?,
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // View model
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.observableStatus.observe(viewLifecycleOwner) { render(it) }

        // Google Maps
        val mapFrag = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFrag.getMapAsync(this)

        // Save button
        setOnSaveHandler()

        // Upload image
        setUploadImageHandler()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = HomeFragmentArgs.fromBundle(requireArguments())
        if (args?.experience != null) {
            fillExistingExperience(args!!.experience!!)
            (requireActivity() as MainActivity).supportActionBar?.title = "Edit Experience"
        } else {
            (requireActivity() as MainActivity).supportActionBar?.title = "Add Experience"
        }
    }

    private fun fillExistingExperience(experience: ExperienceModel) {
        experienceModel = experience.copy()
        binding.dishNameEdittext.setText(experience.dishName)
        binding.ratingBar.rating = experience.rating
        binding.commentEdittext.setText(experience.comment)
        binding.cuisineSpinner.setSelection(
            resources.getStringArray(R.array.cuisines).indexOf(experience.cuisine)
        )
        renderImage(experience.imageUrl)
    }

    private fun render(status: Boolean) {
        when (status) {
            true -> Toast.makeText(
                context,
                "Experience saved successfully :)",
                Toast.LENGTH_LONG
            ).show()
            false -> Toast.makeText(
                context,
                "Error : Could not add a Experience :(",
                Toast.LENGTH_LONG
            ).show()
        }
        // Reset
        experienceModel = ExperienceModel()
        (requireActivity() as MainActivity).supportActionBar?.title = "Add Experience"
    }

    override fun onMapReady(googleMap: GoogleMap) {

        if (isSystemInDarkMode()) {
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.map_night
                )
            )
        }

        resetMap(googleMap)

        val autocompleteFragment = initializeMapSearch(googleMap)

        val experience = args?.experience
        if (experience != null) {
            val latLng = LatLng(experience.latitude, experience.longitude)
            val markerOptions = MarkerOptions().position(latLng).title(experience.restaurantName)
            googleMap.clear()
            googleMap.addMarker(markerOptions)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            autocompleteFragment.setText(experience.restaurantName)
        }
    }

    private fun initializeMapSearch(googleMap: GoogleMap): AutocompleteSupportFragment {

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setHint("Search restaurant")

        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )

        autocompleteFragment.setTypesFilter(
            listOf(
                "bakery",
                "restaurant",
                "meal_takeaway",
                "meal_delivery"
            )
        )

        autocompleteFragment.setOnPlaceSelectedListener(
            placeSelectionListener(
                googleMap,
                autocompleteFragment
            )
        )

        autocompleteFragment.requireView()
            .findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_clear_button)
            .setOnClickListener {
                autocompleteFragment.setText("")
                resetMap(googleMap)
            }

        return autocompleteFragment
    }

    private fun placeSelectionListener(
        googleMap: GoogleMap,
        autocompleteFragment: AutocompleteSupportFragment,
    ) = object : PlaceSelectionListener {
        override fun onPlaceSelected(place: Place) {

            val placeText = place.name!!

            experienceModel.restaurantName = placeText
            experienceModel.latitude = place.latLng?.latitude!!
            experienceModel.longitude = place.latLng?.longitude!!

            // Add a marker to the map for the selected place
            val markerOptions = MarkerOptions().position(place.latLng!!).title(place.name)
            googleMap.clear()
            googleMap.addMarker(markerOptions)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng!!, 15f))

            // Set the text of the AutocompleteSupportFragment to the selected place's name and address
            Handler(Looper.getMainLooper()).postDelayed({
                autocompleteFragment.setText(placeText)
            }, 300)
        }

        override fun onError(status: Status) {
            Timber.e("Google map - Location selection error: ${status.statusMessage}")
        }
    }

    private fun setOnSaveHandler() {
        binding.saveButton.setOnClickListener {
            experienceModel.userId = loggedInViewModel.liveFirebaseUser.value?.email!!
            experienceModel.creationTime = Calendar.getInstance().time.toString()
            experienceModel.dishName = binding.dishNameEdittext.text.toString()
            experienceModel.cuisine = binding.cuisineSpinner.selectedItem.toString()
            experienceModel.rating = binding.ratingBar.rating
            experienceModel.comment = binding.commentEdittext.text.toString()

            if (!checkValidity(experienceModel)) {
                return@setOnClickListener
            }

            // Upload image to Firebase storage and then save the url (for Firebase database)
            viewModel.uploadImage(imageUri) { imageUrl ->
                if (args?.experience != null) {
                    // Edit existing experience
                    experienceModel.uid = args?.experience?.uid
                    if (imageUrl.isNotEmpty() && args?.experience?.imageUrl != imageUrl) {
                        experienceModel.imageUrl = imageUrl
                    }
                    viewModel.editExperience(loggedInViewModel.liveFirebaseUser, experienceModel)
                } else {
                    // Add new experience
                    experienceModel.imageUrl = imageUrl
                    viewModel.addExperience(loggedInViewModel.liveFirebaseUser, experienceModel)
                }
                clearValues()
            }
        }
    }

    private fun checkValidity(experienceModel: ExperienceModel): Boolean {
        if (experienceModel.restaurantName.isEmpty()) {
            Toast.makeText(context, "Please enter restaurant name", Toast.LENGTH_LONG).show()
            return false
        }
        if (experienceModel.dishName.isEmpty()) {
            Toast.makeText(context, "Please enter dish name", Toast.LENGTH_LONG).show()
            return false
        }
        if (experienceModel.cuisine.isEmpty()) {
            Toast.makeText(context, "Please select cuisine", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun setUploadImageHandler() {

        val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (result.data?.data != null) {
                        imageUri = result.data?.data!!
                        renderImage(imageUri.toString())
                    } else {
                        @Suppress("DEPRECATION")
                        val image = result.data?.extras?.get("data") as Bitmap
                        imageUri = getImageUri(requireContext(), image)
                        renderBitmap(image)
                    }
                } else {
                    renderImage("")
                }
            }

        binding.uploadImageButton.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose the dish photo")

            builder.setItems(options) { dialog, item ->
                when {
                    options[item] == "Take Photo" -> {
                        if (EasyPermissions.hasPermissions(
                                requireContext(),
                                Manifest.permission.CAMERA
                            )
                        ) {
                            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            pickImageLauncher.launch(takePictureIntent)
                        } else {
                            EasyPermissions.requestPermissions(
                                this,
                                "Camera permission is required to take a picture",
                                100,
                                Manifest.permission.CAMERA
                            )
                        }
                    }
                    options[item] == "Choose from Gallery" -> {
                        val intent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        pickImageLauncher.launch(intent)
                    }
                    options[item] == "Cancel" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
        }
    }

    private fun renderImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_image_placeholder) // placeholder
            .error(R.drawable.ic_image_error) // error image if load fails
            .into(binding.imgView)
    }

    private fun renderBitmap(bitmap: Bitmap?) {
        Glide.with(this)
            .load(bitmap)
            .placeholder(R.drawable.ic_image_placeholder) // placeholder
            .error(R.drawable.ic_image_error) // error image if load fails
            .into(binding.imgView)
    }

    private fun clearValues() {
        // Clear values
        binding.cuisineSpinner.setSelection(0)
        binding.ratingBar.rating = 0F
        binding.dishNameEdittext.text.clear()
        binding.commentEdittext.text.clear()

        // Clear image
        renderImage("")

        // Reset map
        val clearBtn =
            requireView().findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_clear_button)
        clearBtn?.callOnClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.observableStatus.removeObservers(viewLifecycleOwner)
        args = null
        _binding = null
    }
}