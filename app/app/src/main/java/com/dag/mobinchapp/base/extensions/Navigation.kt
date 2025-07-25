package com.dag.mobinchapp.base.extensions

import androidx.navigation.NavController
import com.dag.mobinchapp.base.navigation.Destination


fun NavController.startAsTopComposable(destination: Destination){
    this.navigate(destination) {
        launchSingleTop = true
        popUpTo(0) { inclusive = true }
    }
}