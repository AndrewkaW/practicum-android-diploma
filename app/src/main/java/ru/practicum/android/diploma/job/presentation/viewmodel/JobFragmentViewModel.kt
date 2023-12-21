package ru.practicum.android.diploma.job.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.favorite.presentation.api.JobFavoriteInteractor
import ru.practicum.android.diploma.job.domain.interactor.LoadJobInteractor
import ru.practicum.android.diploma.job.domain.models.JobForScreen
import ru.practicum.android.diploma.job.domain.models.JobForScreenInfo
import ru.practicum.android.diploma.job.presentation.states.JobScreenState
import ru.practicum.android.diploma.search.domain.models.Codes
import ru.practicum.android.diploma.sharing.domain.interactor.SharingInteractor

class JobFragmentViewModel(
    private val sharingInteractor: SharingInteractor,
    private val loadJobInteractor: LoadJobInteractor,
    private val jobFavoriteInteractor: JobFavoriteInteractor
) : ViewModel() {

    private val _state = MutableLiveData<JobScreenState>()
    fun observeJobScreenLiveData(): LiveData<JobScreenState> = _state

    private var isFavourite: Boolean = false

    fun shareJobLink(jobLink: String) {
        sharingInteractor.shareJobLink(jobLink)
    }

    fun shareEmail(email: String) {
        sharingInteractor.shareEmail(email)
    }

    fun sharePhoneNumber(phoneNumber: String) {
        sharingInteractor.sharePhoneNumber(phoneNumber)
    }

    fun getJob(id: String) {
        _state.postValue(JobScreenState.Loading)
        viewModelScope.launch {
            loadJobInteractor.getJob(id = id).collect { jobForScreenInfo ->
                requestHandler(jobForScreenInfo, id)
            }
        }
    }

    private fun getJobFromDb(id: String) {
        _state.postValue(JobScreenState.Loading)
        viewModelScope.launch {

            val job = jobFavoriteInteractor.getFromBase(id)
            if (job == null){
                _state.postValue(JobScreenState.ServerError)
            }else{
                _state.postValue(JobScreenState.JobFromDb(job))
            }

        }

    }

    private fun requestHandler(jobForScreenInfo: JobForScreenInfo, id: String) {
        when (jobForScreenInfo.responseCodes) {
            Codes.ERROR -> {
                _state.postValue(JobScreenState.ServerError)
            }

            Codes.SUCCESS -> {
                _state.postValue(jobForScreenInfo.job?.let { JobScreenState.Success(it) }
                    ?: JobScreenState.InvalidRequest)
            }

            Codes.NO_NET_CONNECTION -> {
                getJobFromDb(id)
            }

            Codes.NO_RESULTS -> {
            }
        }
    }

    fun addToFavorite(job: JobForScreen) {
        viewModelScope.launch {
            jobFavoriteInteractor.add(job)
            isFavourite = true
            _state.postValue(JobScreenState.FavouriteIcon((isFavourite)))
        }
    }

    fun includedToFavorite(id: String) {
        viewModelScope.launch {
            _state.postValue(JobScreenState.FavouriteIcon(jobFavoriteInteractor.included(id)))
            isFavourite = jobFavoriteInteractor.included(id)
        }
    }

    fun deleteFromFavorite(id: String) {
        viewModelScope.launch {
            jobFavoriteInteractor.delete(id)
            isFavourite = false
            _state.postValue(JobScreenState.FavouriteIcon((isFavourite)))
        }
    }

    fun getFavouriteState(): Boolean {
        return isFavourite
    }
}