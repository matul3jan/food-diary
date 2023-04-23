package ie.setu.fooddiary.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ie.setu.fooddiary.R
import ie.setu.fooddiary.databinding.CardExperienceBinding
import ie.setu.fooddiary.databinding.CardHeaderExperienceBinding
import ie.setu.fooddiary.models.ExperienceModel

interface ExperienceClickListener {
    fun onExperienceClick(experience: ExperienceModel)
}

class ExperienceAdapter(
    private var experiences: List<ExperienceModel>,
    private val listener: ExperienceClickListener,
) : Adapter<ViewHolder>() {

    companion object {
        private const val HEADER_VIEW_TYPE = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            HEADER_VIEW_TYPE -> {
                val binding =
                    CardHeaderExperienceBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                HeaderHolder(binding)
            }
            else -> {
                val binding =
                    CardExperienceBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                MainHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is HeaderHolder) {
            holder.bind()
        }
        if (holder is MainHolder) {
            val experience = experiences[position - 1]
            holder.bind(experience, listener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) HEADER_VIEW_TYPE else position
    }

    fun removeAt(position: Int) {
        experiences.drop(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = experiences.size + 1

    inner class MainHolder(private val binding: CardExperienceBinding) :
        ViewHolder(binding.root) {

        fun bind(experience: ExperienceModel, listener: ExperienceClickListener) {
            binding.root.tag = experience
            binding.experience = experience
            binding.imageIcon.setImageResource(R.mipmap.ic_launcher_round)
            binding.root.setOnClickListener { listener.onExperienceClick(experience) }
            binding.executePendingBindings()
        }
    }

    inner class HeaderHolder(private val binding: CardHeaderExperienceBinding) :
        ViewHolder(binding.root) {

        fun bind() {
            val context = binding.root.context
            binding.headerText1.text = context.getString(R.string.image)
            binding.headerText2.text = context.getString(R.string.restaurant)
            binding.headerText3.text = context.getString(R.string.dish)
        }
    }
}