package ru.practicum.android.diploma.filter.presentation.view_model

import ru.practicum.android.diploma.filter.domain.models.Country
import ru.practicum.android.diploma.filter.domain.models.Industry
import ru.practicum.android.diploma.filter.domain.models.Region
import ru.practicum.android.diploma.filter.presentation.adapter.model.AreaDataInterface

object UiConvertor {

    private fun convertCountryToCountryUi(country: Country): AreaDataInterface.CountryUi {
        return AreaDataInterface.CountryUi(
            country.id,
            country.name
        )
    }

    fun convertCountryUiToCountry(country: AreaDataInterface): Country {
        return Country(
            country.id,
            country.name
        )
    }

    fun convertCountryListToCountryUiList(list: List<Country>): List<AreaDataInterface> {
        return list.map { country -> convertCountryToCountryUi(country) }
    }

    private fun regionToRegionUi(region: Region): AreaDataInterface.RegionUi {
        return AreaDataInterface.RegionUi(
            id = region.id,
            name = region.name,
            countryId = region.countryId,
            countryName = region.countryName
        )
    }

    fun regionListToRegionUiList(list: List<Region>): List<AreaDataInterface> {
        return list.map { region -> regionToRegionUi(region) }
    }

    fun regionUiToRegion(region: AreaDataInterface): Region {
        return when (region) {
            is AreaDataInterface.RegionUi -> Region(
                id = region.id,
                name = region.name,
                countryName = region.countryName,
                countryId = region.countryId
            )

            else -> throw IllegalArgumentException("Invalid data type")
        }
    }

    private fun industryToIndustryUi(industry: Industry): AreaDataInterface.IndustryUi {
        return AreaDataInterface.IndustryUi(
            id = industry.id,
            name = industry.name
        )
    }

    fun industriesToIndustriesUi(list: List<Industry>): List<AreaDataInterface> {
        return list.map { industry -> industryToIndustryUi(industry) }
    }

    fun industryUiToIndustry(industryUi: AreaDataInterface.IndustryUi): Industry {
        return Industry(
            id = industryUi.id,
            name = industryUi.name
        )
    }
}


