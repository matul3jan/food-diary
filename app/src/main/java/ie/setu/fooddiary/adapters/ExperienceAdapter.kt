package ie.setu.fooddiary.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.setu.fooddiary.R
import ie.setu.fooddiary.databinding.CardExperienceBinding
import ie.setu.fooddiary.models.ExperienceModel

interface ExperienceClickListener {
    fun onExperienceClick(experience: ExperienceModel)
}

class ExperienceAdapter constructor(
    private var experiences: List<ExperienceModel>,
    private val listener: ExperienceClickListener,
) : RecyclerView.Adapter<ExperienceAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding =
            CardExperienceBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val experience = experiences[holder.adapterPosition]
        holder.bind(experience, listener)
    }

    fun removeAt(position: Int) {
        experiences.drop(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = experiences.size

    inner class MainHolder(private val binding: CardExperienceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(experience: ExperienceModel, listener: ExperienceClickListener) {
            binding.root.tag = experience
            binding.experience = experience
            binding.imageIcon.setImageResource(R.mipmap.ic_launcher_round)
            binding.root.setOnClickListener { listener.onExperienceClick(experience) }
            binding.executePendingBindings()
        }
    }
}