package com.primosjoyeria.ui.theme



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
class ViewModelFactory<T : ViewModel>(private val creator: () -> T) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T1 : ViewModel> create(modelClass: Class<T1>): T1 = creator() as T1
}