package com.dag.mobinchapp.base.components.bottomnav

import androidx.annotation.DrawableRes
import com.dag.mobinchapp.R
import com.dag.mobinchapp.base.navigation.Destination

enum class BottomNavIcon(
    @DrawableRes var icon: Int,
    var destination: Destination
) {
    Home(R.drawable.baseline_home_filled, Destination.HomeScreen),
    AIView(R.drawable._inch_logo, Destination.AIView)
}