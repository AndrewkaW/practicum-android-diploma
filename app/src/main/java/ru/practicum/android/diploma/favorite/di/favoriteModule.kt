package ru.practicum.android.diploma.favorite.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.favorite.data.impl.FavoriteRepositoryImpl
import ru.practicum.android.diploma.favorite.data.db.AppDataBase
import ru.practicum.android.diploma.favorite.data.db.mapper.JobMapper
import ru.practicum.android.diploma.favorite.data.impl.JobFavoriteRepositoryImpl
import ru.practicum.android.diploma.favorite.domain.impl.FavoriteInteractorImpl
import ru.practicum.android.diploma.favorite.domain.api.FavoriteRepository
import ru.practicum.android.diploma.favorite.domain.api.JobFavoriteRepository
import ru.practicum.android.diploma.favorite.domain.impl.JobFavoriteInteractorImpl
import ru.practicum.android.diploma.favorite.presentation.FavoriteViewModel
import ru.practicum.android.diploma.favorite.presentation.api.FavoriteInteractor
import ru.practicum.android.diploma.favorite.presentation.api.JobFavoriteInteractor

val favoriteModule = module {

    viewModel {
        FavoriteViewModel(get())
    }

    single<FavoriteInteractor> {
        FavoriteInteractorImpl(get())
    }

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }

    single {
        Room.databaseBuilder(androidContext(), AppDataBase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    single<JobFavoriteInteractor> {
        JobFavoriteInteractorImpl(get())
    }

    single<JobFavoriteRepository> {
        JobFavoriteRepositoryImpl(get(), get())
    }

    single {
        JobMapper()
    }
}