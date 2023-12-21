package ru.practicum.android.diploma.job.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.favorite.data.db.AppDataBase
import ru.practicum.android.diploma.favorite.data.impl.JobFavoriteRepositoryImpl
import ru.practicum.android.diploma.favorite.domain.api.JobFavoriteRepository
import ru.practicum.android.diploma.favorite.domain.impl.JobFavoriteInteractorImpl
import ru.practicum.android.diploma.favorite.presentation.api.JobFavoriteInteractor
import ru.practicum.android.diploma.job.data.impl.JobRepositoryImpl
import ru.practicum.android.diploma.job.data.impl.mapper.JobForScreenMapper
import ru.practicum.android.diploma.job.domain.interactor.LoadJobInteractor
import ru.practicum.android.diploma.job.domain.impl.LoadJobInteractorImpl
import ru.practicum.android.diploma.job.presentation.viewmodel.JobFragmentViewModel
import ru.practicum.android.diploma.sharing.data.SharingRepositoryImpl
import ru.practicum.android.diploma.sharing.domain.impl.SharingInteractorImpl
import ru.practicum.android.diploma.sharing.domain.interactor.SharingInteractor
import ru.practicum.android.diploma.sharing.domain.repository.SharingRepository

val jobModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDataBase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    single<JobFavoriteRepository> {
        JobFavoriteRepositoryImpl(get(), get())
    }

    single<JobFavoriteInteractor> {
        JobFavoriteInteractorImpl(get())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(androidContext())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    single {
        JobForScreenMapper(get())
    }

    single {
        JobRepositoryImpl(get(), get())
    }

    factory<LoadJobInteractor> {
        LoadJobInteractorImpl(get())
    }

    viewModel {
        JobFragmentViewModel(get(), get(), get())
    }

}