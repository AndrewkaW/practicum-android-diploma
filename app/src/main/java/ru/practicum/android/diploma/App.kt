package ru.practicum.android.diploma

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.practicum.android.diploma.favorite.di.favoriteModule
import ru.practicum.android.diploma.filter.di.filterModule
import ru.practicum.android.diploma.job.di.jobModule
import ru.practicum.android.diploma.search.di.searchModule
import ru.practicum.android.diploma.similarjob.di.similarJobModule
import ru.practicum.android.diploma.team.di.teamModule
import ru.practicum.android.diploma.util.utilDi

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                teamModule, similarJobModule, searchModule,
                jobModule, filterModule, favoriteModule, utilDi
            )
        }
    }
}