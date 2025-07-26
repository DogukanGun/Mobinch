package com.dag.mobinchapp.di

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import com.dag.mobinchapp.BuildConfig
import com.dag.mobinchapp.base.helper.AlertDialogManager
import com.dag.mobinchapp.base.helper.WalletManagement
import com.dag.mobinchapp.base.helper.WalletManagementImpl
import com.dag.mobinchapp.base.navigation.DefaultNavigator
import com.dag.mobinchapp.base.navigation.Destination
import com.dag.mobinchapp.base.scroll.ScrollStateManager
import com.dag.mobinchapp.data.repository.UserRepository
import com.dag.mobinchapp.domain.DataPreferencesStore
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ObjectModules {
    @Provides
    @Singleton
    fun provideDataPreferencesStore(
        @ApplicationContext context: Context
    ): DataPreferencesStore {
        return DataPreferencesStore(context)
    }

    @Provides
    @Singleton
    fun provideDefaultNavigator(): DefaultNavigator {
        return DefaultNavigator(startDestination = Destination.Splash)
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics {
        val firebaseAnalytics =  Firebase.analytics
        val parameters = Bundle().apply {
            this.putString("build_version", BuildConfig.VERSION_NAME)
        }
        firebaseAnalytics.setDefaultEventParameters(parameters)
        return firebaseAnalytics
    }

    @Provides
    @Singleton
    fun provideAlertDialogManager(): AlertDialogManager {
        return AlertDialogManager()
    }

    @Provides
    @Singleton
    fun provideScrollStateManager(): ScrollStateManager {
        return ScrollStateManager()
    }


    @Provides
    @Singleton
    fun providePackageManager(
        @ApplicationContext context: Context
    ): PackageManager {
        return context.packageManager
    }

    @Provides
    @Singleton
    fun provideAuthorizedKtor(
        userRepository: UserRepository
    ): HttpClient {
        return HttpClient(CIO) {
            install(Logging) {
                level = if (BuildConfig.DEBUG) {
                    LogLevel.ALL
                } else {
                    LogLevel.NONE
                }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 10000L
                connectTimeoutMillis = 10000L
                socketTimeoutMillis = 10000L
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        userRepository.getToken()
                            .getOrNull()
                            ?.let { token -> BearerTokens(token, token) }
                    }
                }
            }
            defaultRequest {
                header("Content-Type", "application/json")
            }
        }
    }

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            prettyPrint = true
        }
    }

    @Provides
    @Singleton
    fun provideWalletManagement(@ApplicationContext context: Context): WalletManagement {
        return WalletManagementImpl(context)
    }
}