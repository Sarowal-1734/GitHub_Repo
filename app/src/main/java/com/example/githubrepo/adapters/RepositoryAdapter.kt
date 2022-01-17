package com.example.githubrepo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.githubrepo.R
import com.example.githubrepo.models.Item
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class RepositoryAdapter : RecyclerView.Adapter<RepositoryAdapter.RepositoryViewHolder>() {

    inner class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewRepositoryName: TextView = itemView.findViewById(R.id.textViewRepositoryName)
        val textViewRepoAuthor: TextView = itemView.findViewById(R.id.textViewRepoAuthor)
        val textViewRepoDescription: TextView = itemView.findViewById(R.id.textViewRepoDescription)
        val textViewLanguage: TextView = itemView.findViewById(R.id.textViewLanguage)
        val textViewUpdatedTime: TextView = itemView.findViewById(R.id.textViewUpdatedTime)
        val textViewUsername: TextView = itemView.findViewById(R.id.textViewUsername)
        val textViewAdditionsCount: TextView = itemView.findViewById(R.id.textViewAdditionsCount)
        val textViewDeletionsCount: TextView = itemView.findViewById(R.id.textViewDeletionsCount)
        val textViewCommitsCount: TextView = itemView.findViewById(R.id.textViewCommitsCount)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        return RepositoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_git_repository_preview, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repository = differ.currentList[position]
        holder.apply {
            textViewRepositoryName.text = repository.name
            textViewRepoAuthor.text = repository.owner.login
            textViewRepoDescription.text = repository.description
            textViewLanguage.text = repository.language
            textViewUpdatedTime.text = getFormattedDate(repository.updated_at)
            textViewUsername.text = repository.best_contributor
            textViewAdditionsCount.text = repository.additions.toString()
            textViewDeletionsCount.text = repository.deletions.toString()
            textViewCommitsCount.text = repository.commits.toString()
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(repository) }
        }
    }

    private fun getFormattedDate(updatedAtDate: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.FRANCE)
        val date = formatter.parse(updatedAtDate)
        val formattedDate: String = DateFormat.getDateInstance(DateFormat.MEDIUM).format(date)
        return "Updated on $formattedDate"
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Item) -> Unit)? = null

    fun setOnItemClickListener(listener: (Item) -> Unit) {
        onItemClickListener = listener
    }

}