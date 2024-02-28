package com.txurdinaga.proyectofinaldam.ui.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer


    class NetworkConnectivityMonitor(private val context: Context) {

        private val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        private val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isConnected.postValue(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                isConnected.postValue(false)
            }
        }

        private val isConnected = MutableLiveData<Boolean>(false)

        init {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
            isConnected.value = isConnectedToInternet()
        }

        fun observeNetworkConnectivity(owner: LifecycleOwner, observer: Observer<Boolean>) {
            isConnected.observe(owner, observer)
        }

        fun isConnected(): Boolean {
            return isConnected.value ?: false
        }

        fun unregisterConnectivityReceiver() {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }

        private fun isConnectedToInternet(): Boolean {
            val activeNetwork = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                ?: false
        }

        private fun isConnectedToInternetDebug(): Boolean {
            val activeNetwork = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            if (networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.d("NETWORK CONNECTIVITY MONITOR", "Connected to Wi-Fi network")
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.d("NETWORK CONNECTIVITY MONITOR", "Connected to cellular network")
                }
                return true
            }
            return false
        }
    }
