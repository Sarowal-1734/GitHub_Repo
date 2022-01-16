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
import java.text.SimpleDateFormat
import java.time.ZonedDateTime

class RepositoryAdapter : RecyclerView.Adapter<RepositoryAdapter.RepositoryViewHolder>() {

    inner class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewRepositoryName: TextView = itemView.findViewById(R.id.textViewRepositoryName)
        val textViewRepoAuthor: TextView = itemView.findViewById(R.id.textViewRepoAuthor)
        val textViewRepoDescription: TextView = itemView.findViewById(R.id.textViewRepoDescription)
        val textViewLanguage: TextView = itemView.findViewById(R.id.textViewLanguage)
        val textViewUpdatedTime: TextView = itemView.findViewById(R.id.textViewUpdatedTime)
        /*val textViewUsername: ImageView = itemView.findViewById(R.id.textViewUsername)
        val textViewAdditionsCount: ImageView = itemView.findViewById(R.id.textViewAdditionsCount)
        val textViewDeletionsCount: ImageView = itemView.findViewById(R.id.textViewDeletionsCount)
        val textViewCommitsCount: ImageView = itemView.findViewById(R.id.textViewCommitsCount)*/
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
            textViewUpdatedTime.text = repository.updated_at
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(repository) }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Item) -> Unit)? = null

    fun setOnItemClickListener(listener: (Item) -> Unit) {
        onItemClickListener = listener
    }

}