package com.example.myapplication.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.*
import com.example.myapplication.api.ApiInterface
import com.example.myapplication.api.CoinDataListItem
import kotlinx.android.synthetic.main.fragment_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.system.exitProcess


/**
 * A simple [Fragment] subclass.
 * Use the [Search.newInstance] factory method to
 * create an instance of this fragment.
 */


/**
 *
 */
class Search : Fragment() {

    var dialog = context?.let { Dialog(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getMyData();
        addDialog()
    }

    /**
     * Function to build a loading dialog
     */
    fun addDialog(){
        val builder = AlertDialog.Builder(activity)
        val inflater = LayoutInflater.from(activity?.applicationContext)
        builder.setView(inflater.inflate(R.layout.custom_dialog, null))
        builder.setCancelable(false)
        dialog = builder.create()
        (dialog as AlertDialog?)?.show()

    }


    /**
     * Function that will fetch from the API, and send the response to the Cryptoadapter.
     */
     private fun getMyData() {
       val retrofitBuilder = Retrofit.Builder()
           .addConverterFactory(GsonConverterFactory.create())
           .baseUrl(API_Coin)
           .build()
           .create(ApiInterface::class.java)

       val retrofitData = retrofitBuilder.getCoins(Currency.getInstance(Locale.getDefault()))

       retrofitData.enqueue(object : Callback<List<CoinDataListItem>> {
           @RequiresApi(Build.VERSION_CODES.M)
           override fun onResponse(call: Call<List<CoinDataListItem>>, response: Response<List<CoinDataListItem>>) {
               val responseBody = response.body()
               dialog?.dismiss()


                   if (response.code() != 200 || responseBody == null) {
                       onFailure(call, Throwable(""))
                   }else {
                       responseBody.let {
                           recyclerview_coins.layoutManager = LinearLayoutManager(context)
                           recyclerview_coins.adapter =
                               context?.let { it1 -> CryptoAdapter(it1, responseBody!!) }
                       }
                   }

           }

           @RequiresApi(Build.VERSION_CODES.M)
           override fun onFailure(call: Call<List<CoinDataListItem>>, t: Throwable) {
               Log.d("MainActivity", "OnFailure: " + t.message)
               dialog()
           }
       })
    }


    /**
     *Function to create and return the view hierarchy associated with the fragment.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
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
                this.getMyData()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { _ , _ ->
                exitProcess(-1)
            }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.error))
        alert.show()
    }


}



