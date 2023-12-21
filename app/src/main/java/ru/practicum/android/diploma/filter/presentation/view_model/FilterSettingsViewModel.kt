package ru.practicum.android.diploma.filter.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.filter.domain.FilterInteractor
import ru.practicum.android.diploma.filter.domain.models.FilterParameters

class FilterSettingsViewModel(private val filterInteractor: FilterInteractor) : ViewModel() {

    private var filterParameters: FilterParameters = filterInteractor.getFilterSettings()

    private val _placeOfWork = MutableLiveData<String>()
    val placeOfWork: LiveData<String> get() = _placeOfWork

    private val _industry = MutableLiveData<String>()
    val industry: LiveData<String> get() = _industry

    private val _salary = MutableLiveData<String>()
    val salary: LiveData<String> get() = _salary

    private val _showWithSalary = MutableLiveData<Boolean>()
    val showWithSalary: LiveData<Boolean> get() = _showWithSalary

    fun setFiltersInFragment() {

        filterParameters = filterInteractor.getFilterSettings()

        if (filterParameters.country.isNullOrBlank()) {
            _placeOfWork.value = ""
        } else {
            if (!filterParameters.area.isNullOrEmpty()) {
                _placeOfWork.value = "${filterParameters.country ?: ""}, ${filterParameters.area}"
            } else {
                _placeOfWork.value = filterParameters.country!!
            }
        }

        if (filterParameters.industry.isNullOrEmpty()) {
            _industry.value = ""
        } else {
            _industry.value = filterParameters.industry!!
        }

        if (filterParameters.salary.isNullOrEmpty()) {
            _salary.value = ""
        } else {
            _salary.value = filterParameters.salary!!
        }

        _showWithSalary.value = filterParameters.onlyWithSalary
    }

    fun clearFilterSettings() {
        filterInteractor.clearFilterSettings()
    }

    fun saveSalarySettings(salary: String, onlyWithSalary: Boolean) {
        filterInteractor.saveSalarySettings(salary, onlyWithSalary)
    }

    fun clearPlaceOfWork() {
        filterInteractor.clearCountryInFilter()
        setFiltersInFragment()
    }

    fun clearIndustry() {
        filterInteractor.deleteIndustryFromFilter()
        setFiltersInFragment()
    }
}