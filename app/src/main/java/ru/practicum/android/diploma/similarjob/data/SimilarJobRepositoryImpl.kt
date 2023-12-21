package ru.practicum.android.diploma.similarjob.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.search.data.models.ResultCodes
import ru.practicum.android.diploma.search.data.net.AdapterJob
import ru.practicum.android.diploma.search.domain.api.DtoConsumer
import ru.practicum.android.diploma.similarjob.data.dto.JobSearchSimilarResponseDto
import ru.practicum.android.diploma.similarjob.data.dto.JobDtoSimilarRequest
import ru.practicum.android.diploma.similarjob.domain.api.SimilarJobRepository
import ru.practicum.android.diploma.util.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import ru.practicum.android.diploma.search.domain.models.JobsInfo

class SimilarJobRepositoryImpl(
    private val networkClient: NetworkClient,
    private val adapterJob: AdapterJob
) : SimilarJobRepository {
    override fun getSimilarJobs(vacancyId: String, page: Int): Flow<DtoConsumer<JobsInfo>> =
        flow {
            val response = networkClient.doRequest(JobDtoSimilarRequest(vacancyId, page))

            when (response.responseCode) {
                ResultCodes.SUCCESS -> emit(
                    DtoConsumer.Success(
                        adapterJob
                            .jobInfoDtoToJobInfo(
                                code = response.responseCode.code,
                                response = (response as JobSearchSimilarResponseDto)
                            )
                    )
                )
                ResultCodes.NO_NET_CONNECTION -> emit(DtoConsumer.NoInternet(response.responseCode.code))
                ResultCodes.ERROR -> emit(DtoConsumer.Error(response.responseCode.code))
            }
        }.flowOn(Dispatchers.IO)
}