package com.baseer.baseer.di

import com.baseer.baseer.data.network.networking.HttpClientEngineProvider
import com.baseer.baseer.data.network.networking.createEmergencyClient
import com.baseer.baseer.data.repo.EmergencyRepository
import com.baseer.baseer.domain.service.GeocoderService
import com.baseer.baseer.domain.service.GeocoderServiceImpl
import com.baseer.baseer.presentation.details.viewmodel.ReportDetailsViewModel
import com.baseer.baseer.presentation.location.viewmodel.LocationPickerViewModel
import com.baseer.baseer.presentation.reportSelect.viewmodel.ReportSelectionViewModel
import com.baseer.baseer.presentation.utils.PhoneDialer
import com.baseer.baseer.utils.PhoneDialerImpl
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // Services
    single<GeocoderService> { GeocoderServiceImpl() }
    single<PhoneDialer> { PhoneDialerImpl() }

    // ViewModels
    viewModelOf(::ReportSelectionViewModel)
    viewModelOf(::LocationPickerViewModel)
    viewModelOf(::ReportDetailsViewModel)
    single<HttpClientEngineProvider> { HttpClientEngineProvider() }

    single { createEmergencyClient(get()) }
    single { EmergencyRepository(get()) }
}

