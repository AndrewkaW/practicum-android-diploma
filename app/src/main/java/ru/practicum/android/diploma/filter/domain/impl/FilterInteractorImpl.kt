package ru.practicum.android.diploma.filter.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.filter.domain.FilterInteractor
import ru.practicum.android.diploma.filter.domain.FilterRepository
import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.filter.domain.models.FilterParameters
import ru.practicum.android.diploma.filter.domain.models.Industry
import ru.practicum.android.diploma.filter.domain.models.Region
import ru.practicum.android.diploma.search.domain.api.DtoConsumer

class FilterInteractorImpl(private val filterRepository: FilterRepository) : FilterInteractor {
    override fun getFilterSettings(): FilterParameters {
        return filterRepository.getFilterSettings()
    }

    override fun clearFilterSettings() {
        return filterRepository.clearFilterSettings()
    }

    override fun saveSalarySettings(salary: String, onlyWithSalary: Boolean) {
        filterRepository.saveSalarySettings(salary, onlyWithSalary)
    }

    override suspend fun getCountry(): Flow<DtoConsumer<List<Country>>> {
        return filterRepository.getCountries()
    }

    override fun saveCountryToFilter(country: Country) {
        filterRepository.saveCountryToFilter(country)
    }

    override fun clearCountryInFilter() {
        filterRepository.clearCountryInFilter()
    }

    override suspend fun getRegions(): Flow<DtoConsumer<List<Region>>> {
        return filterRepository.getRegions()
    }

    override fun saveRegionToFilter(region: Region) {
        filterRepository.saveRegionToFilter(region)
    }

    override fun deleteRegionFromFilter() {
        filterRepository.deleteRegionFromFilter()
    }

    override fun getAreasByName(name: String): Flow<DtoConsumer<List<Region>>> {
        return filterRepository.getAreasByName(name)
    }

    override fun getIndustries(): Flow<DtoConsumer<List<Industry>>> {
        return filterRepository.getIndustries()
    }

    override fun deleteIndustryFromFilter() {
        filterRepository.deleteIndustryFromFilter()
    }

    override fun saveIndustryToFilter(industry: Industry) {
        filterRepository.saveIndustryToFilter(industry)
    }

    override fun getIndustriesByName(name: String): Flow<DtoConsumer<List<Industry>>> {
        return filterRepository.getIndustriesByName(name)
    }
}