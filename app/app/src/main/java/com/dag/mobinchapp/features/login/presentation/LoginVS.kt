package com.dag.mobinchapp.features.login.presentation

import com.dag.mobinchapp.base.BaseVS
import com.dag.mobinchapp.data.model.User


sealed class LoginVS: BaseVS {
    data object Loading : LoginVS()
    data class Error(val message: String) : LoginVS()
    data class NavigateToHome(val user: User) : LoginVS()
}