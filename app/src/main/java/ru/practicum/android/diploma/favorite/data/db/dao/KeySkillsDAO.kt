package ru.practicum.android.diploma.favorite.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.practicum.android.diploma.favorite.data.db.entity.KeySkillsEntity

@Dao
interface KeySkillsDAO {

    @Query("DELETE FROM KeySkills WHERE idVacancy LIKE :idVacancy")
    suspend fun delete(idVacancy: String)

    @Query("SELECT * FROM KeySkills WHERE idVacancy LIKE :idVacancy")
    suspend fun getSkills(idVacancy: String): List<KeySkillsEntity>

    @Insert(entity = KeySkillsEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(keySkillsEntity: KeySkillsEntity)
}