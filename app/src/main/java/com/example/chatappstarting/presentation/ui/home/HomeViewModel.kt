package com.example.chatappstarting.presentation.ui.home

import com.example.chatappstarting.presentation.navgraph.AppNavigator
import com.example.chatappstarting.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(appNavigator: AppNavigator) : BaseViewModel(appNavigator) {
}