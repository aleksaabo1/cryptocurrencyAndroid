package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.fragments.News
import com.example.myapplication.fragments.Search
import kotlinx.android.synthetic.main.activity_main.*


const val API_Coin = "https://api.coingecko.com/api/v3/"

/**
 * MainActivity class to host the fragments of the application.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    /**
     * Function to create the Activity.
     *
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)
        startApplication()

    }


    /**
     *Function to start the application.
     *
     * If the device is connected to internet, the user will be able to use the device.
     * If the device is not connected to the internet, the user will get a
     * notification to turn on internet.
     *
     */
    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.M)
    fun startApplication(){
        val searchFragment = Search()
        val newsFragment = News()

        if(isOnline(this)){
            setCurrentFragment(searchFragment)
            bottomNavigationView.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.search -> setCurrentFragment(searchFragment)
                    R.id.news -> setCurrentFragment(newsFragment)
                }
                true
            }
        }else{
            dialog()
        }
    }


    /**
     * Function to create a dialog, that will notify the user to
     * turn on internet on their device.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun dialog(){
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage(resources.getString(R.string.connectToInternet))
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.try_again)) { _, _ ->
                this.startApplication()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { _ , _ ->
                finish()
            }

        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.noInternet))
        alert.show()
    }


    /**
     * Function that will replace the current fragment, with a new one.
     */
    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }


    /**
     * Function to check if the device is connected to Internet,
     * either on WIFI, cellular or ethernet.
     *
     * Return true if the device is on internet,
     * false if not.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
        return false
    }
}
