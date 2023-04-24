package ie.setu.fooddiary.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ie.setu.fooddiary.R
import ie.setu.fooddiary.models.ExperienceModel

class ExperiencesAdapter(
    private val experiences: List<ExperienceModel>,
) :
    RecyclerView.Adapter<ExperiencesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val experienceTitle: TextView = view.findViewById(R.id.titleTextView)
        val experienceDescription: TextView = view.findViewById(R.id.descriptionTextView)
        val experienceRating: RatingBar = view.findViewById(R.id.rating_bar_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.experience_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val experience = experiences[position]
        holder.experienceTitle.text = experience.dishName
        holder.experienceDescription.text = experience.comment
        holder.experienceRating.rating = experience.rating
    }

    override fun getItemCount(): Int {
        return experiences.size
    }
}
