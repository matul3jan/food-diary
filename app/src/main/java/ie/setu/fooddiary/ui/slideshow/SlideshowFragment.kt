package ie.setu.fooddiary.ui.slideshow

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import ie.setu.fooddiary.R
import ie.setu.fooddiary.adapters.ExperiencesAdapter
import ie.setu.fooddiary.databinding.FragmentSlideshowBinding
import ie.setu.fooddiary.models.ExperienceModel
import ie.setu.fooddiary.ui.login.LoggedInViewModel
import ie.setu.fooddiary.utils.fitMarkersInMap
import ie.setu.fooddiary.utils.resetMap
import timber.log.Timber

class SlideshowFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    private var markers: MutableList<Marker?> = mutableListOf()
    private val logInViewModel: LoggedInViewModel by activityViewModels()

    private lateinit var viewModel: SlideshowViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        viewModel = ViewModelProvider(this)[SlideshowViewModel::class.java]
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)

        // Initialize the map
        val mapFrag = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFrag.getMapAsync(this)

        // Initial Loading of user's experiences
        logInViewModel.liveFirebaseUser.observe(viewLifecycleOwner) { viewModel.loadForUser(it.uid) }

        setupToolbar()

        return binding.root
    }

    private fun setupToolbar() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main, menu)
                val toggle = menu.findItem(R.id.toggle_experiences)
                toggle.actionView?.setOnClickListener {
                    if ((it as SwitchCompat).isChecked) {
                        viewModel.load()
                    } else {
                        viewModel.loadForUser(logInViewModel.liveFirebaseUser.value?.uid)
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onMapReady(map: GoogleMap) {
        resetMap(map)
        initializeMapSearch(map)

        // Draw map with markers each time experiences are loaded
        viewModel.observableExperienceList.observe(viewLifecycleOwner) { loadMap(it, map) }
    }

    private fun initializeMapSearch(map: GoogleMap) {
        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_search_view) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )

        createPlaceSelectedHandler(autocompleteFragment, map)

        createClearButtonHandler(autocompleteFragment, map)
    }

    private fun createClearButtonHandler(
        autocompleteFragment: AutocompleteSupportFragment,
        map: GoogleMap,
    ) {
        autocompleteFragment.requireView()
            .findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_clear_button)
            .setOnClickListener {
                autocompleteFragment.setText("")
                fitMarkersInMap(markers, map)
            }
    }

    private fun createPlaceSelectedHandler(
        autocompleteFragment: AutocompleteSupportFragment,
        map: GoogleMap,
    ) {
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                val location = place.latLng

                val filteredMarker =
                    markers.filter { location == it?.position } as MutableList<Marker?>

                if (filteredMarker.isNotEmpty()) {
                    // Location is present in markers, so zoom into it
                    fitMarkersInMap(filteredMarker, map)
                } else {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(location!!, 12F))
                }
            }

            override fun onError(status: Status) {
                Timber.e("Google map - Location selection error: ${status.statusMessage}")
            }
        })
    }

    private fun loadMap(experiences: List<ExperienceModel>, map: GoogleMap) {

        val currentUserId = logInViewModel.liveFirebaseUser.value?.email

        // Create markers on map
        createMarkers(experiences, currentUserId, map)

        // Show popup window on marker click
        createPopupWindow(experiences, map)
    }

    private fun createPopupWindow(experiences: List<ExperienceModel>, map: GoogleMap) {
        map.setOnMarkerClickListener { marker ->
            val contentView = View.inflate(context, R.layout.popup_window, null)

            val experienceTitle = contentView.findViewById<TextView>(R.id.experiences_title)
            experienceTitle.text = marker.title

            val layoutManager = LinearLayoutManager(requireContext())
            val recyclerView = contentView.findViewById<RecyclerView>(R.id.experiences_rv)
            recyclerView.layoutManager = layoutManager

            val filteredExperiences = experiences.filter {
                it.latitude == marker.position.latitude && it.longitude == marker.position.longitude
            }
            recyclerView.adapter = ExperiencesAdapter(filteredExperiences)

            val popupWindow = PopupWindow(
                contentView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                true
            )

            popupWindow.showAtLocation(contentView, Gravity.CENTER, 0, 0)

            val closeButton = contentView.findViewById<ImageView>(R.id.close_button)
            closeButton.setOnClickListener {
                popupWindow.dismiss()
            }

            true
        }
    }

    private fun createMarkers(
        experiences: List<ExperienceModel>,
        currentUserId: String?,
        map: GoogleMap,
    ) {

        map.clear()
        markers.clear()

        for (experience in experiences) {

            val markerOptions = MarkerOptions()
                .position(LatLng(experience.latitude, experience.longitude))
                .title(experience.restaurantName)

            // Red marker for self experiences, blue for other's
            if (experience.userId == currentUserId) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            } else {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            }

            markers.add(map.addMarker(markerOptions))
        }

        fitMarkersInMap(markers, map)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        markers.clear()
        _binding = null
    }
}