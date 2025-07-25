package com.dag.mobinchapp.features.splash

import com.dag.mobinchapp.base.BaseVS


sealed class SplashVS: BaseVS {
    data object StartApp: SplashVS()
    data object CloseApp: SplashVS()
}