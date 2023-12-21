package ru.practicum.android.diploma.search.data.net

import ru.practicum.android.diploma.search.data.models.JobSearchRequest
import ru.practicum.android.diploma.search.data.models.JobSearchResponseDto
import ru.practicum.android.diploma.search.data.models.LogoUrls
import ru.practicum.android.diploma.search.domain.models.Codes
import ru.practicum.android.diploma.search.domain.models.Filter
import ru.practicum.android.diploma.search.domain.models.Vacancy
import ru.practicum.android.diploma.search.domain.models.JobsInfo
import ru.practicum.android.diploma.similarjob.data.dto.JobSearchSimilarResponseDto
import ru.practicum.android.diploma.util.ResourceProvider
import ru.practicum.android.diploma.util.TextUtils

class AdapterJob(private val resourceProvider: ResourceProvider) {
    fun jobInfoDtoToJobInfo(response: JobSearchResponseDto, code: Int) = JobsInfo(
        responseCodes = codeMapper(code, response.found),
        jobs = response.items.map {
            Vacancy(
                id = it.id,
                area = it.area.name,
                department = it.department?.name ?: "",
                employerImgUrl = getLogo(it.employer.logoUrls),
                employer = it.employer.name,
                name = it.name,
                salary = TextUtils.getSalaryString(it.salary, resourceProvider),
                type = it.type.name ?: ""
            )
        },
        found = response.found,
        page = response.page,
        pages = response.pages
    )

    fun jobInfoDtoToJobInfo(response: JobSearchSimilarResponseDto, code: Int) = JobsInfo(
        responseCodes = codeMapper(code, response.found),
        jobs = response.items.map {
            Vacancy(
                id = it.id,
                area = it.area.name,
                department = it.department?.name ?: "",
                employerImgUrl = getLogo(it.employer.logoUrls),
                employer = it.employer.name,
                name = it.name,
                salary = TextUtils.getSalaryString(it.salary, resourceProvider),
                type = it.type.name ?: ""
            )
        },
        found = response.found,
        page = response.page,
        pages = response.pages
    )

    fun filterToJobReq(filter: Filter) = JobSearchRequest(
        makeHasMap(filter)
    )

    private fun codeMapper(code: Int, found: Int) = when (code) {
        200 -> if (found == 0) Codes.NO_RESULTS else Codes.SUCCESS
        500 -> Codes.NO_NET_CONNECTION
        else -> Codes.ERROR
    }

    private fun getLogo(logoUrls: LogoUrls?): String {
        if (logoUrls?.mediumIcon != null) return logoUrls.mediumIcon.toString()
        if (logoUrls?.original != null) return logoUrls.original.toString()
        return ""
    }

    private fun makeHasMap(filter: Filter): HashMap<String, String> {
        val request = HashMap<String, String>()
        request["text"] = filter.request
        request["page"] = filter.page.toString()
        request["only_with_salary"] = filter.onlyWithSalary.toString()
        if (filter.area != null) request["area"] = filter.area
        if (filter.industry != null) request["industry"] = filter.industry
        if (filter.salary != null) request["salary"] = filter.salary.toString()
        return request
    }
}