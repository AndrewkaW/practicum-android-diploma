package ru.practicum.android.diploma.job.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.job.data.impl.JobRepositoryImpl
import ru.practicum.android.diploma.job.domain.interactor.LoadJobInteractor
import ru.practicum.android.diploma.job.domain.models.JobForScreenInfo
import ru.practicum.android.diploma.job.domain.impl.mapper.JobForScreenInfoMapper.createJobForScreenInfo
import ru.practicum.android.diploma.search.domain.api.DtoConsumer
import ru.practicum.android.diploma.search.domain.models.Codes

class LoadJobInteractorImpl(private val repository: JobRepositoryImpl) : LoadJobInteractor {

    override suspend fun getJob(id: String): Flow<JobForScreenInfo> {
        return repository.getJob(id = id).map { result ->
            when (result) {
                is DtoConsumer.Success -> createJobForScreenInfo(
                    resultCode = Codes.SUCCESS, result.data
                )

                is DtoConsumer.Error -> createJobForScreenInfo(Codes.ERROR, null)

                is DtoConsumer.NoInternet -> createJobForScreenInfo(Codes.NO_NET_CONNECTION, null)
            }
        }
    }
}