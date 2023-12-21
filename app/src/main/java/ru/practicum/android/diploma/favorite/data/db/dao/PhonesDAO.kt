package ru.practicum.android.diploma.favorite.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.practicum.android.diploma.favorite.data.db.entity.PhonesEntity

@Dao
interface PhonesDAO {

    @Query("DELETE FROM Phones WHERE idVacancy LIKE :idVacancy")
    suspend fun delete(idVacancy: String)

    @Query("SELECT * FROM Phones WHERE idVacancy LIKE :idVacancy")
    suspend fun getPhones(idVacancy: String): List<PhonesEntity>

    @Insert(entity = PhonesEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(phonesEntity: PhonesEntity)
}