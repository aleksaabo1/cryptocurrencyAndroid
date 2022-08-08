package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.api.CoinDataListItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.raw_items.view.*

/**
 *Class to create a recycler view with a list of crypto currencies.
 */
class CryptoAdapter(val context: Context, val cryptoList: List<CoinDataListItem>): RecyclerView.Adapter<CryptoAdapter.ViewHolder>() {


    /**
     *
     */
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var coinID: TextView
        var coinName: TextView
        var imageView: ImageView
        init {
            coinID = itemView.coinID
            coinName = itemView.coinName
            imageView = itemView.imageView
        }
    }

    /**
     * Function to create and returns the view hierarchy associated with the fragment.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.raw_items, parent, false)
        return ViewHolder(itemView)
    }

    /**
     * Function to bind the items to single elements in the recyclerview.
     * If the user clicks on an item, the user will be redirected to the information site.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.coinID.text = cryptoList[position].id
        holder.coinName.text = cryptoList[position].name
        Picasso.get().load(cryptoList[position].image).into(holder.imageView)

        holder.itemView.setOnClickListener {
            val id = cryptoList[position].id
            val volume = cryptoList[position].total_volume
            val price = cryptoList[position].current_price
            val image = cryptoList[position].image
            val priceChange24Hours = cryptoList[position].price_change_percentage_24h
            val priceChange24Value = cryptoList[position].price_change_24h

            //Intent that will send more detail to the InformationActivity class.
            val intent = Intent(context, InformationActivity::class.java).apply{
                putExtra("coinId", id)
                putExtra("volume", volume.toString())
                putExtra("price", price.toString())
                putExtra("image", image)
                putExtra("priceChange", priceChange24Hours.toString())
                putExtra("priceChangeValue", priceChange24Value.toString())
            }
            startActivity(context, intent, Bundle.EMPTY)
        }
    }

    /**
     * Function to return the size of the array.
     */
    override fun getItemCount(): Int {
        return cryptoList.size
    }


}
