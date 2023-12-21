package ru.practicum.android.diploma.search.domain.use_cases

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.search.domain.models.Filter
import ru.practicum.android.diploma.search.domain.models.JobsInfo

interface LoadJobsUseCase {
    suspend fun execute(filter: Filter): Flow<JobsInfo>
}