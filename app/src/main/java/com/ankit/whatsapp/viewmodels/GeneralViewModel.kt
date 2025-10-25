package com.ankit.whatsapp.viewmodels

import androidx.lifecycle.ViewModel
import com.ankit.whatsapp.respository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GeneralViewModel @Inject constructor(
    private val repo: FirebaseRepository
) : ViewModel() {



}