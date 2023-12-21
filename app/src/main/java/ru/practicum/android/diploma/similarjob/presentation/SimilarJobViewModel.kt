package ru.practicum.android.diploma.similarjob.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.search.domain.models.Codes
import ru.practicum.android.diploma.search.domain.models.JobsInfo
import ru.practicum.android.diploma.search.domain.models.Vacancy
import ru.practicum.android.diploma.search.presentation.SearchViewModel
import ru.practicum.android.diploma.similarjob.presentation.api.SimilarJobInteractor
import ru.practicum.android.diploma.similarjob.presentation.models.SimilarState
import ru.practicum.android.diploma.util.debounce

class SimilarJobViewModel(
    private val similarJobInteractor: SimilarJobInteractor,
    private val id: String
) : ViewModel() {
    private val similar = MutableLiveData<SimilarState>()
    private var page = 0
    private var maxPage = 1
    private val vacancyList = mutableListOf<Vacancy>()
    private val pageLoaderDebounce =
        debounce<Unit>(SearchViewModel.PAGE_LOAD_DEBOUNCE_DELAY_MILS, viewModelScope, true) {
            similar.value = SimilarState.Loading(true)
            getSimilar()
        }

    fun getSimilarLiveData(): LiveData<SimilarState> = similar

    fun getSimilar() {
        viewModelScope.launch {
            similarJobInteractor.getSimilarJobs(id, page).collect {
                requestHandler(it)
            }
        }
    }

    fun getNewPage() {
        if (page < maxPage - 1) {
            page += 1
            pageLoaderDebounce(Unit)
        }
    }

    private fun requestHandler(jobsInfo: JobsInfo) {
        when (jobsInfo.responseCodes) {
            Codes.ERROR -> {
                similar.value = SimilarState.ServerError
            }

            Codes.SUCCESS -> {
                vacancyList.addAll(jobsInfo.jobs!!)
                maxPage = jobsInfo.pages
                similar.value = SimilarState.Success(
                    jobList = vacancyList
                )
            }

            Codes.NO_NET_CONNECTION -> {
                similar.value = SimilarState.ConnectionError
            }

            Codes.NO_RESULTS -> {
                similar.value = SimilarState.InvalidRequest
            }
        }
    }
}