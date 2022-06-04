package com.example.gpsv2

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var client = OkHttpClient()

    private val timer = object: CountDownTimer(2000, 1000) {
        override fun onTick(millisUntilFinished: Long) {

        }
        override fun onFinish() {
            getLocation()
            startTimer()
        }
    }

    private val str: String = "I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. I must not tell lies. HP"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        findViewById<Button>(R.id.btn_get_location).setOnClickListener {
            startTimer()
        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            cancelTimer()
        }
    }

    private fun getLocation() {
        val task = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    101
            )
            return
        }

        task.addOnSuccessListener {
            if (it != null) {

                run("http://206.189.8.42:8000/get?${str}")
//                run("http://164.92.153.55:8080/get?${str}")
//                run("http://20.115.96.179:8000/v1/writeonly/add-tracking?latitude=${it.latitude}&longitude=${it.longitude}")
            }
        }
    }

    private fun startTimer() {
        timer.start()
    }


    private fun cancelTimer() {
        timer.cancel()
    }


    @SuppressLint("MissingPermission")
    private fun run(url: String) {
        val telephonyManager: TelephonyManager =
                getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        var cellInfoList: List<CellInfo> = telephonyManager.allCellInfo
        var networkStatus: String = ""
        if (cellInfoList.isNotEmpty()) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoGsm) {
                    val cellSignalStrengthGsm = cellInfo.cellSignalStrength
                    var intSignalStrength = cellSignalStrengthGsm.dbm
                    networkStatus = intSignalStrength.toString()
                } else if (cellInfo is CellInfoWcdma) {
                    val cellSignalStrengthWcdma = cellInfo.cellSignalStrength
                    var intSignalStrength = cellSignalStrengthWcdma.dbm
                    networkStatus = intSignalStrength.toString()
                } else if (cellInfo is CellInfoLte) {
                    val cellSignalStrengthLte = cellInfo.cellSignalStrength
                    var intSignalStrength = cellSignalStrengthLte.dbm
                    networkStatus = intSignalStrength.toString()
                }
            }
        }

        val request = Request.Builder()
                .addHeader("X-DBM", networkStatus)
                .addHeader("X-TYPE", networkTypeClass(telephonyManager.networkType))
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) = println(
                    response.body()?.string()
            )
        })

        createOkHttpClient(applicationContext)

    }


    fun networkTypeClass(networkType: Int): String {

        when (networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN,
            TelephonyManager.NETWORK_TYPE_GSM
            -> return "2G"
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_TD_SCDMA
            -> return "3G"
            TelephonyManager.NETWORK_TYPE_LTE
            -> return "4G"
            TelephonyManager.NETWORK_TYPE_NR
            -> return "5G"
            else -> return "Unknown"
        }
    }


    private fun createOkHttpClient(context: Context) {

        client = OkHttpClient.Builder()
                .addInterceptor(
                        ChuckerInterceptor.Builder(context)
                                .collector(ChuckerCollector(context))
                                .maxContentLength(250000L)
                                .redactHeaders(emptySet())
                                .alwaysReadResponseBody(false)
                                .build()
                )
                .build()
    }

}