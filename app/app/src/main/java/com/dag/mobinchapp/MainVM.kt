package com.dag.mobinchapp

import androidx.lifecycle.viewModelScope
import com.dag.mobinchapp.base.BaseVM
import com.dag.mobinchapp.base.navigation.DefaultNavigator
import com.dag.mobinchapp.base.navigation.Destination
import com.dag.mobinchapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainVM @Inject constructor(
    private var defaultNavigator: DefaultNavigator,
    private val userRepository: UserRepository
): BaseVM<MainVS>() {

    fun navigate(destination: Destination){
        viewModelScope.launch {
            defaultNavigator.navigate(destination)
        }
    }

    fun isBottomNavActive(currentRoute:String?): Boolean {
        return currentRoute?.let {
            return Destination.NAV_WITHOUT_BOTTOM_NAVBAR
                .map { it.toString() }.contains(currentRoute).not()
        } ?: false
    }

    fun signOut() {
        viewModelScope.launch {
            userRepository.clearUser()
            _viewState.value = MainVS.LoggedOut
            defaultNavigator.navigate(Destination.LoginScreen) {
                launchSingleTop = true
                popUpTo(0)
            }
        }
    }

}