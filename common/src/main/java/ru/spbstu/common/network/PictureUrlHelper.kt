package ru.spbstu.common.network

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import ru.spbstu.common.BuildConfig
import ru.spbstu.common.di.prefs.PreferencesRepository

public class PictureUrlHelper(private val preferencesRepository: PreferencesRepository) {
    public fun getPictureUrl(url: String): GlideUrl {
        return GlideUrl(
            url, LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer ${preferencesRepository.token}")
                .build()
        )
    }
}