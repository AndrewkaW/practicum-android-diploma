package ru.practicum.android.diploma.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.search.domain.models.Codes
import ru.practicum.android.diploma.search.domain.models.Filter
import ru.practicum.android.diploma.search.domain.models.JobsInfo
import ru.practicum.android.diploma.search.domain.models.Vacancy
import ru.practicum.android.diploma.search.domain.use_cases.GetSearchFilterUseCase
import ru.practicum.android.diploma.search.domain.use_cases.LoadJobsUseCase
import ru.practicum.android.diploma.search.presentation.models.SearchStates
import ru.practicum.android.diploma.util.debounce

class SearchViewModel(
    private val loadJobsUseCase: LoadJobsUseCase,
    private val getSearchFilterUseCase: GetSearchFilterUseCase
) : ViewModel() {

    private var filter: Filter

    init {
        filter = getFilter()
    }

    private val vacancyList = mutableListOf<Vacancy>()
    private var state: SearchStates = SearchStates.Start
    private val stateLiveData = MutableLiveData(state)
    private var searchJob: Job? = null
    private var page = 0
    private var maxPage = 0
    private var founded = 0
    private val searchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY_MILS, viewModelScope, true) {
            filter.request = it
            search()
        }
    private val pageLoaderDebounce =
        debounce<Unit>(PAGE_LOAD_DEBOUNCE_DELAY_MILS, viewModelScope, true) {
            search()
        }

    fun loadJobs(text: String) {
        if (text.isBlank() || text == filter.request) return
        searchDebounce(text)
    }

    private fun search() {
        stateLiveData.value = SearchStates.Loading
        viewModelScope.launch {
            loadJobsUseCase.execute(filter = filter).collect { jobsInfo ->
                requestHandler(jobsInfo)
            }
        }
    }

    private fun refreshSearch() {
        vacancyList.clear()
        if (filter.request.isNotBlank()) search()
    }

    fun getState(): LiveData<SearchStates> = stateLiveData

    private fun getFilter() = getSearchFilterUseCase.execute()

    fun refreshFilter() {
        val newFiler = getFilter()
        newFiler.request = filter.request
        newFiler.page = filter.page
        stateLiveData.value = state

        if (newFiler != filter) {
            newFiler.page = 0
            maxPage = 0
            stateLiveData.value = SearchStates.FilterChanged(checkFilterState(newFiler))
            filter = newFiler
            refreshSearch()
            return
        }
    }

    fun getNewPage() {
        if (page < maxPage - 1) {
            filter.page = page + 1
            pageLoaderDebounce(Unit)
        }
    }

    private fun requestHandler(jobsInfo: JobsInfo) {
        when (jobsInfo.responseCodes) {
            Codes.ERROR -> {
                stateLiveData.value = SearchStates.ServerError
            }

            Codes.SUCCESS -> {
                vacancyList.addAll(jobsInfo.jobs!!)
                page = jobsInfo.page
                maxPage = jobsInfo.pages
                if (page == 0)
                    founded = jobsInfo.found

                state = jobsInfo.let {
                    SearchStates.Success(
                        jobList = vacancyList,
                        page = it.page,
                        found = founded
                    )
                }
                stateLiveData.value = state
            }

            Codes.NO_NET_CONNECTION -> {
                state = SearchStates.ConnectionError
                stateLiveData.value = state
            }

            Codes.NO_RESULTS -> {
                state = SearchStates.InvalidRequest
                stateLiveData.value = state
            }
        }
    }

    private fun checkFilterState(filterForCheck: Filter): Boolean =
        filterForCheck != Filter(0, filterForCheck.request, null, null, null, false)

    fun startFilterCheck() {
        stateLiveData.value = state
        stateLiveData.value = SearchStates.FilterChanged(checkFilterState(filter))
    }

    fun clearAll() {
        stateLiveData.value = SearchStates.Start
        vacancyList.clear()
        filter.request = ""
        searchJob?.cancel()
        maxPage = 0
        page = 0
    }

    override fun onCleared() {
        super.onCleared()
        searchJob = null
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY_MILS = 2000L
        const val PAGE_LOAD_DEBOUNCE_DELAY_MILS = 100L
    }
}