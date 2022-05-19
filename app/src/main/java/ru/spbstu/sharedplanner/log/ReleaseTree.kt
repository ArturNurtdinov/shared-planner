package ru.spbstu.sharedplanner.log

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class ReleaseTree: Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        if (priority == Log.ERROR) {
            FirebaseCrashlytics.getInstance().log("error: message = $message, tag=$tag, throwable=$t")
        }
    }

}