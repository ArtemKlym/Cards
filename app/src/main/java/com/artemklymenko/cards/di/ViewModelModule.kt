package com.artemklymenko.cards.di

import com.artemklymenko.cards.repository.WordsRepositoryDb
import com.artemklymenko.cards.vm.WordsViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    @ViewModelScoped
    fun provideWordsViewModel(repositoryDb: WordsRepositoryDb): WordsViewModel {
        return WordsViewModel(repositoryDb)
    }
}