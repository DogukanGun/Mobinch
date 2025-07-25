package com.dag.mobinchapp

import com.dag.mobinchapp.base.BaseVS

sealed class MainVS: BaseVS {
    data object LoggedOut: MainVS()
}