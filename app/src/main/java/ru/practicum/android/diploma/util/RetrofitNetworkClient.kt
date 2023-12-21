package ru.practicum.android.diploma.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.filter.data.models.AllAreasResponse
import ru.practicum.android.diploma.filter.data.models.AreasByIdResponse
import ru.practicum.android.diploma.filter.data.models.CountriesResponse
import ru.practicum.android.diploma.filter.data.models.FilterRequest
import ru.practicum.android.diploma.filter.data.models.IndustriesResponse
import ru.practicum.android.diploma.job.data.mainmodels.JobDtoForScreenRequest
import ru.practicum.android.diploma.search.data.models.JobSearchRequest
import ru.practicum.android.diploma.search.data.models.ResponseDto
import ru.practicum.android.diploma.search.data.models.ResultCodes
import ru.practicum.android.diploma.similarjob.data.dto.JobDtoSimilarRequest

class RetrofitNetworkClient(val context: Context) : NetworkClient {
    private val retrofitHh =
        Retrofit.Builder()
            .baseUrl(BASE_HH_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    private val hhService = retrofitHh.create(HhApiJobInfo::class.java)

    override suspend fun doRequest(dto: Any): ResponseDto {
        if (!isConnected()) return ResponseDto()

        return when (dto) {
            is JobSearchRequest -> try {
                val resp = hhService.getJobList(
                    options = dto.queryMap
                )
                resp.apply { responseCode = ResultCodes.SUCCESS }
            } catch (e: Exception) {
                ResponseDto().apply { responseCode = ResultCodes.ERROR }
            }

            is JobDtoForScreenRequest -> try {
                val resp = hhService.getJobById(
                    id = dto.id
                )
                resp.apply { responseCode = ResultCodes.SUCCESS }
            } catch (e: Exception) {
                ResponseDto().apply { responseCode = ResultCodes.ERROR }
            }

            is FilterRequest.Countries -> try {
                val data = hhService.getCountries()
                val response = CountriesResponse(data)
                response.apply { responseCode = ResultCodes.SUCCESS }
            } catch (e: Exception) {
                ResponseDto().apply { responseCode = ResultCodes.ERROR }
            }

            is FilterRequest.Areas -> try {
                val data = hhService.getAllAreas()
                val response = AllAreasResponse(data)
                response.apply { responseCode = ResultCodes.SUCCESS }
            } catch (e: Exception) {
                ResponseDto().apply { responseCode = ResultCodes.ERROR }
            }

            is FilterRequest.AreasById -> try {
                val data = hhService.getAreasById(
                    id = dto.idArea
                )
                val response = AreasByIdResponse(data)
                response.apply { responseCode = ResultCodes.SUCCESS }
            } catch (e: Exception) {
                ResponseDto().apply { responseCode = ResultCodes.ERROR }
            }

            is FilterRequest.Industries -> try {
                val data = hhService.getIndustries()
                val response = IndustriesResponse(data)
                response.apply { responseCode = ResultCodes.SUCCESS }
            } catch (e: Exception) {
                ResponseDto().apply { responseCode = ResultCodes.ERROR }
            }

            is JobDtoSimilarRequest -> try {
                val resp = hhService.getSimilarVacancies(dto.id, dto.page)
                resp.apply { responseCode = ResultCodes.SUCCESS }

            } catch (e: Exception) {
                ResponseDto().apply { responseCode = ResultCodes.ERROR }
            }

            else -> ResponseDto().apply { responseCode = ResultCodes.ERROR }
        }
    }

    @SuppressLint("NewApi")
    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

    companion object {
        const val BASE_HH_API = "https://api.hh.ru/"
    }
}