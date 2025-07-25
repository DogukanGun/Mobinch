package com.dag.mobinchapp.base.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dag.mobinchapp.R

@Composable
fun getDestinationTitle(destination: String): String{
    return when(destination) {
        Destination.HomeScreen.toString() -> {
            stringResource(R.string.home_destination_title)
        }
        else -> {
            ""
        }
    }
}