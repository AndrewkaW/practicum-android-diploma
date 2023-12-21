package ru.practicum.android.diploma.favorite.data.impl

import ru.practicum.android.diploma.favorite.data.db.AppDataBase
import ru.practicum.android.diploma.favorite.data.db.entity.FavoriteEntity
import ru.practicum.android.diploma.favorite.data.db.mapper.JobMapper
import ru.practicum.android.diploma.favorite.domain.api.JobFavoriteRepository
import ru.practicum.android.diploma.job.data.secondarymodels.Phones
import ru.practicum.android.diploma.job.data.secondarymodels.Skills
import ru.practicum.android.diploma.job.domain.models.JobForScreen

class JobFavoriteRepositoryImpl(
    private val appDataBase: AppDataBase,
    private val mapper: JobMapper
) : JobFavoriteRepository {
    override suspend fun add(job: JobForScreen) {
        appDataBase.favoriteDAO().add(mapper.map(job))
        job.keySkills.forEach {
            appDataBase.keySkillsDAO().add(mapper.mapSkills(it!!, job.id!!))
        }
        job.phones?.forEach {
            appDataBase.phonesDAO().add(mapper.mapPhones(it!!, job.id!!))
        }
    }

    override suspend fun delete(id: String) {
        appDataBase.favoriteDAO().delete(id)
        appDataBase.keySkillsDAO().delete(id)
        appDataBase.phonesDAO().delete(id)
    }

    override suspend fun included(id: String): Boolean {
        return try {
            appDataBase.favoriteDAO().included(id)
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getFromBase(id: String): JobForScreen? {

        var favoriteEntity: FavoriteEntity
        val favoriteEntityResponse = appDataBase.favoriteDAO().getVacancy(id)

        if (favoriteEntityResponse.isEmpty()) {
            return null
        } else {
            favoriteEntity = favoriteEntityResponse[0]
        }

        val skills = ArrayList<Skills?>()
        appDataBase.keySkillsDAO().getSkills(id).forEach {
            skills.add(mapper.mapSkills(it))
        }
        val phones = ArrayList<Phones?>()
        appDataBase.phonesDAO().getPhones(id).forEach {
            phones.add(mapper.mapPhones(it))
        }

        return mapper.map(favoriteEntity, skills.toTypedArray(), phones.toTypedArray())
    }
}