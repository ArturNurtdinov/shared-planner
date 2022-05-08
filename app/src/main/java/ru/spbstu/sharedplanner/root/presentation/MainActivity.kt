package ru.spbstu.sharedplanner.root.presentation

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.ok.android.sdk.OkAuthActivity
import ru.ok.android.sdk.Shared
import ru.spbstu.auth.auth.presentation.AuthFragment
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.events.AuthEvent
import ru.spbstu.sharedplanner.R
import ru.spbstu.sharedplanner.databinding.ActivityMainBinding
import ru.spbstu.sharedplanner.navigation.Navigator
import ru.spbstu.sharedplanner.root.di.RootApi
import ru.spbstu.sharedplanner.root.di.RootComponent
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var viewModel: MainActivityViewModel

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        EventBus.getDefault().register(this)
        inject()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        navigator.attach(navController, this)

        viewModel.sendTokenResult
            .onEach {
                if (it) {
                    navigator.openMainPage()
                }
            }
            .launchIn(lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        navigator.detach()
        EventBus.getDefault().unregister(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    @Subscribe()
    fun onEvent(event: AuthEvent) {

    }

    private fun inject() {
        FeatureUtils.getFeature<RootComponent>(this, RootApi::class.java)
            .mainActivityComponentFactory()
            .create(this)
            .inject(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Shared.OK_AUTH_REQUEST_CODE) {
            val error = data?.getStringExtra("error")
            val accessToken = data?.getStringExtra("access_token")
            if (!accessToken.isNullOrEmpty() && error.isNullOrEmpty()) {
                viewModel.sendToken()
            } else {
                Timber.tag(TAG).e("Sign In: failed, error=$error")
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
                navHostFragment.childFragmentManager.fragments.forEach {
                    if (it is AuthFragment && it.isVisible) {
                        it.hideProgress()
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.auth_failed),
                            Toast.LENGTH_LONG
                        ).show()
                        return@forEach
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.simpleName!!
    }
}