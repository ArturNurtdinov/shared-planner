import org.gradle.api.artifacts.dsl.DependencyHandler

/**
 * To define dependencies
 */
object Deps {
    private val timber by lazy { "com.jakewharton.timber:timber:${Versions.timber}" }
    private val appCompat by lazy { "androidx.appcompat:appcompat:${Versions.appCompat}" }
    private val materialDesign by lazy { "com.google.android.material:material:${Versions.material}" }
    private val constraintLayout by lazy { "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}" }
    private val junit by lazy { "junit:junit:${Versions.jUnit}" }
    private val junitExt by lazy { "androidx.test.ext:junit:${Versions.junitExt}" }
    private val espresso by lazy { "androidx.test.espresso:espresso-core:${Versions.espresso}" }
    private val kotlinStdLib by lazy { "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}" }
    private val coreKtx by lazy { "androidx.core:core-ktx:${Versions.androidx}" }
    private val livedataKtx by lazy { "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycleKtx}" }
    private val viewModelKtx by lazy { "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleKtx}" }
    private val fragmentKtx by lazy { "androidx.navigation:navigation-fragment-ktx:${Versions.navigationKtx}" }
    private val runtimeNavKtx by lazy { "androidx.navigation:navigation-runtime-ktx:${Versions.navigationKtx}" }
    private val uiKtx by lazy { "androidx.navigation:navigation-ui-ktx:${Versions.navigationKtx}" }
    private val legacySupport by lazy { "androidx.legacy:legacy-support-v4:1.0.0" }
    private val glide by lazy { "com.github.bumptech.glide:glide:4.12.0" }
    private val coordinator by lazy { "androidx.coordinatorlayout:coordinatorlayout:${Versions.coordinator}" }
    val daggerDep by lazy { "com.google.dagger:dagger:${Versions.daggerVersion}" }
    val glideKapt by lazy { "com.github.bumptech.glide:compiler:4.12.0" }
    val daggerKapt by lazy { "com.google.dagger:dagger-compiler:${Versions.daggerVersion}" }
    private val circleImageView by lazy { "de.hdodenhof:circleimageview:${Versions.circleImageView}" }
    private val gson by lazy { "com.google.code.gson:gson:${Versions.gson}" }
    private val okhttp by lazy { "com.squareup.okhttp3:okhttp:${Versions.okhttp}" }
    private val retrofit by lazy { "com.squareup.retrofit2:retrofit:${Versions.retrofit}" }
    private val eventBus by lazy { "org.greenrobot:eventbus:${Versions.eventBus}" }
    private val gsonConverter by lazy { "com.squareup.retrofit2:converter-gson:${Versions.retrofit}" }
    private val security by lazy { "androidx.security:security-crypto:${Versions.security}" }
    private val rxKotlin by lazy { "io.reactivex.rxjava3:rxkotlin:${Versions.rxKotlin}" }
    private val calendarView by lazy { "com.github.kizitonwose:CalendarView:${Versions.calendarView}" }
    val desugar_jdk by lazy { "com.android.tools:desugar_jdk_libs:${Versions.desugar}" }
    val vk by lazy { "com.vk:androidsdk:${Versions.vk}" }
    val ok by lazy { "ru.ok:odnoklassniki-android-sdk:${Versions.ok}" }
    val googleAuth by lazy { "com.google.android.gms:play-services-auth:${Versions.google}" }
    private val spinner by lazy { "com.github.skydoves:powerspinner:${Versions.spinner}" }
    private val colorPicker by lazy { "me.jfenn.ColorPickerDialog:base:${Versions.colorPicker}" }
    private val paging by lazy { "androidx.paging:paging-runtime:${Versions.paging3}" }
    private val recycler by lazy { "androidx.recyclerview:recyclerview:${Versions.recycler}" }
    val firebaseBom by lazy { "com.google.firebase:firebase-bom:${Versions.firebaseBom}" }
    private val firebaseMessaging by lazy { "com.google.firebase:firebase-messaging:${Versions.firebaseMessaging}" }
    private val firebaseAnalytics by lazy { "com.google.firebase:firebase-analytics-ktx" }

    val appLibraries = arrayListOf<String>().apply {
        add(kotlinStdLib)
        add(coreKtx)
        add(appCompat)
        add(constraintLayout)
        add(livedataKtx)
        add(viewModelKtx)
        add(fragmentKtx)
        add(runtimeNavKtx)
        add(uiKtx)
        add(materialDesign)
        add(timber)
        add(legacySupport)
        add(glide)
        add(circleImageView)
        add(coordinator)
        add(eventBus)
        add(security)
        add(rxKotlin)
        add(retrofit)
        add(calendarView)
        add(spinner)
        add(colorPicker)
        add(paging)
        add(recycler)
    }

    val firebase = arrayListOf<String>().apply {
        add(firebaseAnalytics)
        add(firebaseMessaging)
    }

    val network = arrayListOf<String>().apply {
        add(gson)
        add(okhttp)
        add(gsonConverter)
    }

    val androidTestLibraries = arrayListOf<String>().apply {
        add(junitExt)
        add(espresso)
    }

    val testLibraries = arrayListOf<String>().apply {
        add(junit)
    }
}

//util functions for adding the different type dependencies from build.gradle file
fun DependencyHandler.kapt(list: List<String>) {
    list.forEach { dependency ->
        add("kapt", dependency)
    }
}

fun DependencyHandler.implementation(list: List<String>) {
    list.forEach { dependency ->
        add("implementation", dependency)
    }
}

fun DependencyHandler.androidTestImplementation(list: List<String>) {
    list.forEach { dependency ->
        add("androidTestImplementation", dependency)
    }
}

fun DependencyHandler.testImplementation(list: List<String>) {
    list.forEach { dependency ->
        add("testImplementation", dependency)
    }
}