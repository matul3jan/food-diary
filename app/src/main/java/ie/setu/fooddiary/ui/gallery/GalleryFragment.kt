package ie.setu.fooddiary.ui.gallery

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ie.setu.fooddiary.R
import ie.setu.fooddiary.adapters.ExperienceAdapter
import ie.setu.fooddiary.adapters.ExperienceClickListener
import ie.setu.fooddiary.databinding.FragmentGalleryBinding
import ie.setu.fooddiary.models.ExperienceModel
import ie.setu.fooddiary.ui.login.LoggedInViewModel
import ie.setu.fooddiary.utils.*

class GalleryFragment : Fragment(), ExperienceClickListener {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    lateinit var loader: AlertDialog
    private val viewModel: GalleryViewModel by activityViewModels()
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()

    private lateinit var filteredExperiences: MutableList<ExperienceModel>

    companion object {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, url: String?) {
            Glide.with(view.context)
                .load(url)
                .placeholder(R.drawable.ic_image_placeholder) // placeholder
                .error(R.drawable.ic_image_error) // error image if load fails
                .into(view)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        loader = createLoader(requireActivity())
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

        showLoader(loader, "Downloading experiences")

        filteredExperiences =
            (viewModel.observableExperienceList.value
                ?: mutableListOf()) as MutableList<ExperienceModel>

        viewModel.observableExperienceList.observe(viewLifecycleOwner) {
            render(it)
            hideLoader(loader)
            checkSwipeRefresh()
        }

        setSearchHandler()
        setSwipeRefresh()
        setAddHandler()
        setDeleteHandler()
        setEditHandler()

        return binding.root
    }

    override fun onExperienceClick(experience: ExperienceModel) {
        val action = GalleryFragmentDirections.actionGalleryFragmentToHomeFragment(experience)
        findNavController().navigate(action)
    }

    private fun render(experiencesList: List<ExperienceModel>) {
        binding.recyclerView.adapter = ExperienceAdapter(experiencesList, this)
        if (experiencesList.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.experiencesNotFound.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.experiencesNotFound.visibility = View.GONE
        }
    }

    private fun filterExperiences(query: String) {
        filteredExperiences.clear()
        for (experience in viewModel.observableExperienceList.value!!) {
            if (experience.restaurantName.contains(query, true) ||
                experience.dishName.contains(query, true)
            ) {
                filteredExperiences.add(experience)
            }
        }
        render(filteredExperiences)
    }

    private fun setSearchHandler() {
        binding.searchView.queryHint = "Search here..."
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterExperiences(it) }
                return true
            }
        })
    }

    private fun setAddHandler() {
        binding.fab.setOnClickListener {
            val action = GalleryFragmentDirections.actionGalleryFragmentToHomeFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun setEditHandler() {
        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onExperienceClick(viewHolder.itemView.tag as ExperienceModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun setDeleteHandler() {
        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showLoader(loader, "Deleting experience")
                val adapter = binding.recyclerView.adapter as ExperienceAdapter
                val experience = viewHolder.itemView.tag as ExperienceModel
                adapter.removeAt(viewHolder.adapterPosition)
                viewModel.delete(
                    viewModel.liveFirebaseUser.value?.uid!!,
                    (viewHolder.itemView.tag as ExperienceModel).uid!!,
                    experience.imageUrl
                )
                hideLoader(loader)
            }
        }

        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun setSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            showLoader(loader, "Downloading experiences")
            viewModel.load()
        }
    }

    private fun checkSwipeRefresh() {
        if (binding.swipeRefresh.isRefreshing) {
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        showLoader(loader, "Downloading experiences")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.liveFirebaseUser.value = it
                viewModel.load()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}