package com.example.myapplication.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.api.NewsApiInterface
import com.example.myapplication.api.NewsData
import com.example.myapplication.NewsAdapter
import com.example.myapplication.databinding.NewsElementBinding
import kotlinx.android.synthetic.main.fragment_news.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.system.exitProcess

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val newsData = "https://newsapi.org/v2/"


/**
 * A simple [Fragment] subclass.
 * Use the [News.newInstance] factory method to
 * create an instance of this fragment.
 */
class News : Fragment() {
    var dialog = context?.let { Dialog(it) }
    private lateinit var binding: NewsElementBinding


    /**
     * Function to create the fragment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewsElementBinding.inflate(layoutInflater)
        showDialog()
        getNewsData()
    }


    /**
     * Function to display a loading screen.
     */
    fun showDialog(){
        val builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity?.applicationContext)
        builder.setView(inflater.inflate(R.layout.custom_dialog, null))
        builder.setCancelable(false)
        dialog = builder.create()
        (dialog as AlertDialog?)?.show()
    }


    /**
     * Function to fetch the news from the API.
     *
     */
    private fun getNewsData() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(newsData)
            .build()
            .create(NewsApiInterface::class.java)


        val retrofitData = retrofitBuilder.getData()
        retrofitData.enqueue(object : Callback<NewsData> {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onResponse(call: Call<NewsData>, response: Response<NewsData>) {
                val responseBody = response.body()

                if (response.code() != 200 || responseBody == null) {
                    onFailure(call, Throwable(""))
                } else {


                    dialog?.dismiss()
                    responseBody.let {
                        recyclerview_news.layoutManager = LinearLayoutManager(context)
                        recyclerview_news.adapter = NewsAdapter(requireContext(), responseBody)
                    }
                }
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onFailure(call: Call<NewsData>, t: Throwable) {
                Log.d("MainActivity", "OnFailure: " + t.message)
                dialog()
            }
            })
    }


    /**
     * Function to create a dialog, that will notify the user to
     * turn on internet on their device.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun dialog(){
        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setMessage(resources.getString(R.string.problems))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.try_again)) { _, _ ->
                this.getNewsData()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { _ , _ ->
                exitProcess(-1)
            }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.error))
        alert.show()
    }






    /**
     *Function to create and return the view hierarchy associated with the fragment.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

}



