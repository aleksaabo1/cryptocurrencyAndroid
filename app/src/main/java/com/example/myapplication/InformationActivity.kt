package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.api.ApiInterface
import com.example.myapplication.api.HistoricalData
import com.example.myapplication.api.coinDescriptionData
import com.example.myapplication.databinding.ActivityInformationBinding
import com.example.myapplication.databinding.ActivityInformationBinding.inflate
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 *
 */
@RequiresApi(Build.VERSION_CODES.O)
class InformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInformationBinding
    val chartStyle = GraphStyle(this)
    val currency: Currency = Currency.getInstance(Locale.getDefault())
    private var API = "https://api.coingecko.com/"
    val prices = ArrayList<Float>()
    private val CHART_LABEL = "DAY_CHART"
    private val dayData = mutableListOf<Entry>()
    private val _lineDataSet = MutableLiveData(LineDataSet(dayData, CHART_LABEL))
    val lineDataSet: LiveData<LineDataSet> = _lineDataSet


    /**
     * Function to initialize the activity.
     * The function initialize the
     */
    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setText()
        setContentView(binding.root)
        binding.dayChart.description = null;
        binding.dayChart.setNoDataText(getResources().getString(R.string.Waiting));
        getMyData("90", "hourly")
        getDescription()

    }


    /**
     *Function that will display the information of the cryptocurrency.
     *
     * The function will fetch the data from the API.
     * Depending on the users location, the currency information will change currency.
     */
    private fun getDescription(){
        val retrofitBuilder = retroFitAPI(API)
        val id = intent.getStringExtra("coinId")
        val retrofitData = id?.let { retrofitBuilder.getDescription(it) }

        retrofitData?.enqueue(object : Callback<coinDescriptionData> {
             @SuppressLint("SetTextI18n")
             override fun onResponse(call: Call<coinDescriptionData>, response: Response<coinDescriptionData>) {
                 val responseBody = response.body()
                 var volume = ""
                 var marketCap = ""

                 if (response.code() != 200){
                     onFailure(call, Throwable("We have some trouble connection to our server. Try Again later"))
                 }else if(responseBody == null){
                     onFailure(call, Throwable("No data were found. Try again later."))
                 }

                 if (responseBody != null) {
                     when (currency.currencyCode) {
                         "NOK" -> {

                             volume = NumberFormat.getNumberInstance(Locale.US)
                                 .format(responseBody.market_data.total_volume.nok)
                             marketCap = NumberFormat.getNumberInstance(Locale.US)
                                 .format(responseBody.market_data.market_cap.nok)
                         }

                         "USD" -> {
                             volume = NumberFormat.getNumberInstance(Locale.US)
                                 .format(responseBody.market_data.total_volume.usd)
                             marketCap = NumberFormat.getNumberInstance(Locale.US)
                                 .format(responseBody.market_data.market_cap.usd)
                         }
                     }
                     binding.volumeView.text = "$volume ${currency.currencyCode}"
                     binding.descriptionView.text = responseBody.description.en
                     binding.marketCapText.text = "$marketCap ${currency.currencyCode}"
                 }


             }

                override fun onFailure(call: Call<coinDescriptionData>, t: Throwable) {
                    Log.d("MainActivity", "OnFailure: " + t.message)
                    dialogInformation(t.message!!)

                }
        })
    }


    /**
     * Function to set the image, and values of change of each individual coin.
     * If the change in the percent and currency is negative, then the text will be displayed in red.
     * If the change is positive, the text will be displayed in green.
     */
    @SuppressLint("SetTextI18n")
    fun setText(){
        val id = intent.getStringExtra("coinId")
        val price = intent.getStringExtra("price")
        val image = intent.getStringExtra("image")
        val priceChange = intent.getStringExtra("priceChange")
        val priceChangeValue = intent.getStringExtra("priceChangeValue")

        binding.priceView.text = (price + " " + currency.currencyCode)
        binding.coinNameHeader.text = id!!.uppercase()
        Picasso.get().load(image).into(binding.coinLogo)
        when(priceChange?.get(0)){
            '-' -> { binding.priceChange24.setTextColor(resources.getColor(R.color.red))
                        binding.priceChange24.text = "${priceChange.subSequence(0,5)}%"}
            else -> { binding.priceChange24.setTextColor(resources.getColor(R.color.green))
                    binding.priceChange24.text = "${priceChange?.subSequence(0,4)}%"}
        }
        when(priceChangeValue?.get(0)){
            '-' -> {binding.priceChangeCurrency.setTextColor(resources.getColor(R.color.red))
                binding.priceChangeCurrency.text = "${priceChangeValue.subSequence(0,5)}${currency.currencyCode}"}
            else -> { binding.priceChangeCurrency.setTextColor(resources.getColor(R.color.green))
                binding.priceChangeCurrency.text = "${priceChangeValue?.subSequence(0,4)}${currency.currencyCode}"}
        }

    }


    /**
     *Function that will fetch the dataset used in the chart.
     * The data will be added into an arraylist.
     */
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun getMyData(days: String, interval: String ) {
        val retrofitBuilder = retroFitAPI(API)

        val id = intent.getStringExtra("coinId")
        val retrofitData = id?.let {
            retrofitBuilder.getData(it, currency.currencyCode.lowercase(), days, interval)
        }

         retrofitData?.enqueue(object : Callback<HistoricalData> {
                override fun onResponse(
                    call: Call<HistoricalData>,
                    response: Response<HistoricalData>) {
                    val data = response.body()
                    if (data == null){
                        onFailure(call, Throwable("No data were found. Try again later."))
                    }else if (response.code() != 200){
                        onFailure(call, Throwable("We have some trouble connection to our server. Try Again later"))
                    }
                    if (data != null) {
                        for (datas in data.prices) {
                            val priceData = datas[1].toFloat()
                            prices.add(priceData)
                        }
                    }
                }

                override fun onFailure(call: Call<HistoricalData>, t: Throwable) {
                    Log.d("MainActivity", "OnFailure: " + t.message)
                    dialog(t.message!!)
                }
         })
    }

    /**
     * Function to create a dialog, and notify the user of an error.
     */
    fun dialog(message: String){
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.try_again)) { _, _ ->
                getMyData("90", "hourly")
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { _ , _ ->
                finish()
            }
        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.problems))
        alert.show()
    }



    /**
     * Function to create a dialog, and notify the user of an error.
     */
    fun dialogInformation(message: String){
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.try_again)) { _, _ ->
                getDescription()
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { _ , _ ->
                finish()
            }
        val alert = dialogBuilder.create()
        alert.setTitle(resources.getString(R.string.problems))
        alert.show()
    }








    /**
     *Init block that will initialize the dataset.
     * Since the dataset is fetched from the API, the init block had to be delayed by 3 seconds.
     */
    init {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                chartStyle.styleChart(binding.dayChart)
                val entries1 = prices.mapIndexed { index, list ->
                    Entry(index.toFloat() , list)
                }
                _lineDataSet.value = LineDataSet(entries1, CHART_LABEL)
                lineDataSet.observe(this) { lineDataSet ->
                    chartStyle.styleLine(lineDataSet)
                    binding.dayChart.data = LineData(lineDataSet)
                    binding.dayChart.invalidate()

                }

            },
            3000 // value in milliseconds
        )
    }


    /**
     * Generic function to fetch the data from the API.
     */
    private fun retroFitAPI(baseURL: String): ApiInterface {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseURL)
            .build()
            .create(ApiInterface::class.java)

        return retrofitBuilder
    }
}



