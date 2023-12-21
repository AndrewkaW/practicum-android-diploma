package ru.practicum.android.diploma.search.domain.use_cases.impl

import ru.practicum.android.diploma.search.domain.api.FilterShPrefRepository
import ru.practicum.android.diploma.search.domain.models.Filter
import ru.practicum.android.diploma.search.domain.use_cases.GetSearchFilterUseCase

class GetSearchFilterUseCaseImp(private val repository: FilterShPrefRepository) : GetSearchFilterUseCase {
    override fun execute(): Filter =
        repository.getFilter()
}