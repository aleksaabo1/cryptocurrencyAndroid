package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.api.NewsData
import com.example.myapplication.databinding.NewsElementBinding
import com.example.myapplication.fragments.News
import kotlinx.android.synthetic.main.news_element.view.*
import kotlin.coroutines.coroutineContext


/**
 * Class of newsAdapter.
 */
class NewsAdapter(val context: Context, val newsList: NewsData): RecyclerView.Adapter<NewsAdapter.ViewHolder>() {


    /**
     *Class of viewholder.
     */
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {


        var headLine: TextView
        var description: TextView
        var publisher: TextView
        var imageView: ImageView

        init {

            headLine = itemView.titleView
            description = itemView.descriptionView
            publisher = itemView.publisherView
            imageView = itemView.imageView2

        }
    }

    /**
     * Function to bind the news_element.xml file to the recycler view.
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.news_element, parent, false)
        return ViewHolder(itemView)
    }

    /**
     * Function to bind all the elements in different sections in the recyclerview.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.headLine.text = newsList.articles[position].title
        holder.description.text = newsList.articles[position].description
        holder.publisher.text = newsList.articles[position].source.name

        if (newsList.articles[position].urlToImage.isNotEmpty()) {
            Glide.with(context).load(newsList.articles[position].urlToImage).into(holder.imageView)
        }
        holder.itemView.setOnClickListener {
            val webPage = Uri.parse(newsList.articles[position].url)
            val openNews =  Intent(Intent.ACTION_VIEW, webPage)
            startActivity(context, openNews, Bundle.EMPTY)
        }
    }


    /**
     * Function to count the items in the array.
     *
     */
    override fun getItemCount(): Int {
        return newsList.articles.size
    }

}
