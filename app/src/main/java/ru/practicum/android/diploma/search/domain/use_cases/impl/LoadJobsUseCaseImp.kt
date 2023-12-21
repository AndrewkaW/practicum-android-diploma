package ru.practicum.android.diploma.search.domain.use_cases.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.search.domain.api.DtoConsumer
import ru.practicum.android.diploma.search.domain.api.JobNetworkRepository
import ru.practicum.android.diploma.search.domain.models.Codes
import ru.practicum.android.diploma.search.domain.models.Filter
import ru.practicum.android.diploma.search.domain.models.JobsInfo
import ru.practicum.android.diploma.search.domain.use_cases.AdapterDomain
import ru.practicum.android.diploma.search.domain.use_cases.LoadJobsUseCase

class LoadJobsUseCaseImp(private val repository: JobNetworkRepository) : LoadJobsUseCase {
    override suspend fun execute(filter: Filter): Flow<JobsInfo> =
        repository.getJobs(filter = filter).map { result ->
            when (result) {
                is DtoConsumer.Success -> result.data

                is DtoConsumer.Error -> AdapterDomain.codeMapper(Codes.ERROR)

                is DtoConsumer.NoInternet -> AdapterDomain.codeMapper(Codes.NO_NET_CONNECTION)
            }
        }
}