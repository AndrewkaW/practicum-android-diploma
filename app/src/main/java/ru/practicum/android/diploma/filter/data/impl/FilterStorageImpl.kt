package ru.practicum.android.diploma.filter.data.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import ru.practicum.android.diploma.filter.data.FilterStorage
import ru.practicum.android.diploma.filter.data.models.CountryDto
import ru.practicum.android.diploma.filter.data.models.FilterParametersDto
import ru.practicum.android.diploma.filter.data.models.IndustrySp
import ru.practicum.android.diploma.filter.data.models.RegionDto
import ru.practicum.android.diploma.util.DataUtils.Companion.FILTER_COUNTRY
import ru.practicum.android.diploma.util.DataUtils.Companion.FILTER_REGION
import ru.practicum.android.diploma.util.DataUtils.Companion.FILTER_SETTINGS

class FilterStorageImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : FilterStorage {

    override fun saveFilterSettings(filterParameters: FilterParametersDto) {
        val jSON = gson.toJson(filterParameters)
        sharedPreferences
            .edit()
            .putString(FILTER_SETTINGS, jSON)
            .apply()
    }

    override fun getFilterSettings(): FilterParametersDto {
        val jSON = sharedPreferences.getString(FILTER_SETTINGS, "")
        return if (jSON.isNullOrEmpty()) {
            FilterParametersDto(
                country = null,
                areaId = null,
                area = null,
                industryId = null,
                industry = null,
                salary = null,
                onlyWithSalary = false
            )
        } else {
            gson.fromJson(jSON, FilterParametersDto::class.java)
        }
    }

    override fun clearFilterSettings() {
        sharedPreferences
            .edit()
            .remove(FILTER_SETTINGS)
            .apply()
        clearCountry()
    }

    private fun saveCountry(country: CountryDto) {
        val jSON = gson.toJson(country)
        sharedPreferences
            .edit()
            .putString(FILTER_COUNTRY, jSON)
            .apply()
    }

    override fun getCountry(): CountryDto? {
        val jSON = sharedPreferences.getString(FILTER_COUNTRY, null)
        return if (jSON.isNullOrEmpty()) {
            null
        } else {
            gson.fromJson(jSON, CountryDto::class.java)
        }
    }

    private fun clearCountry() {
        sharedPreferences
            .edit()
            .remove(FILTER_COUNTRY)
            .apply()
    }

    override fun saveCountryToFilter(country: CountryDto) {
        val filterParam = getFilterSettings()

        if (filterParam.country != country.name) {
            saveFilterSettings(
                filterParam.copy(
                    country = country.name,
                    areaId = country.id,
                    area = null
                )
            )
            saveCountry(country)
        }
    }

    override fun clearCountryInFilter() {
        val filterParam = getFilterSettings()
        saveFilterSettings(
            filterParam.copy(
                country = null,
                areaId = null,
                area = null
            )
        )
        clearCountry()
        clearRegion()
    }

    private fun saveRegion(areaDto: RegionDto) {
        val jSON = gson.toJson(areaDto)
        sharedPreferences
            .edit()
            .putString(FILTER_REGION, jSON)
            .apply()
    }

    private fun getRegion(): RegionDto? {
        val jSON = sharedPreferences.getString(FILTER_REGION, null)
        return if (jSON.isNullOrEmpty()) {
            null
        } else {
            gson.fromJson(jSON, RegionDto::class.java)
        }
    }

    private fun clearRegion() {
        sharedPreferences
            .edit()
            .remove(FILTER_REGION)
            .apply()
    }

    override fun saveRegionToFilter(region: RegionDto) {
        val filterParam = getFilterSettings()

        if (filterParam.areaId != region.id) {
            saveFilterSettings(
                filterParam.copy(
                    country = region.countryName,
                    areaId = region.id,
                    area = region.name
                )
            )
            saveCountry(
                CountryDto(
                    id = region.countryId,
                    name = region.countryName
                )
            )
            saveRegion(region)
        }
    }

    override fun deleteRegionFromFilter() {
        val filterParam = getFilterSettings()
        val region = getRegion()
        if (region != null) {
            saveFilterSettings(
                filterParam.copy(
                    country = region.countryName,
                    areaId = region.countryId,
                    area = null
                )
            )
            clearRegion()
        }
    }

    override fun deleteIndustryFromFilter() {
        val filterParam = getFilterSettings()
        saveFilterSettings(
            filterParam.copy(
                industryId = null,
                industry = null,
            )
        )
    }

    override fun saveIndustryToFilter(industrySp: IndustrySp) {
        val filterParam = getFilterSettings()
        saveFilterSettings(
            filterParam.copy(
                industryId = industrySp.id,
                industry = industrySp.name,
            )
        )
    }
}