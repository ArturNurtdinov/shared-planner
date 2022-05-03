package ru.spbstu.auth.auth.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import ru.ok.android.sdk.Odnoklassniki
import ru.ok.android.sdk.util.OkAuthType
import ru.ok.android.sdk.util.OkScope
import ru.spbstu.auth.R
import ru.spbstu.auth.databinding.FragmentAuthBinding
import ru.spbstu.auth.di.AuthApi
import ru.spbstu.auth.di.AuthComponent
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extensions.setDebounceClickListener
import timber.log.Timber
import javax.inject.Inject

class AuthFragment : Fragment() {
    @Inject
    lateinit var viewModel: AuthViewModel

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleResultLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val contract = GoogleSignInContractResult(googleSignInClient)
        googleResultLauncher = registerForActivityResult(contract) {
            it?.let {
                handleSignInResult(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(layoutInflater, container, false)
        inject()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!viewModel.onBackClicked()) {
                        requireActivity().finish()
                    }
                }
            })

        binding.frgAuthGoogle.setDebounceClickListener {
            showProgress()
            googleResultLauncher.launch("GoogleSignIn")
        }

        binding.frgAuthVk.setDebounceClickListener {
            showProgress()
            VK.login(requireActivity(), arrayListOf(VKScope.FRIENDS))
        }

        binding.frgAuthOk.setDebounceClickListener {
            showProgress()
            Odnoklassniki.createInstance(requireContext(), APP_ID, APP_KEY)
            val odnoklassniki = Odnoklassniki.getInstance()
            odnoklassniki.requestAuthorization(
                requireActivity(),
                REDIRECT_URI,
                OkAuthType.ANY,
                OkScope.VALUABLE_ACCESS,
                OkScope.LONG_ACCESS_TOKEN
            )
        }

        binding.frgAuthShadow.setOnClickListener { }
        return binding.root
    }

    private fun showProgress() {
        binding.frgMainProgressBar.isVisible = true
        binding.frgAuthShadow.isVisible = true
    }

    public fun hideProgress() {
        binding.frgMainProgressBar.isVisible = false
        binding.frgAuthShadow.isVisible = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val vkCallback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                // User passed authorization
                viewModel.openMainPage()
            }

            override fun onLoginFailed(errorCode: Int) {
                Timber.e(TAG, "vk login failed with errorCode=$errorCode")
                Toast.makeText(requireContext(), getString(R.string.auth_failed), Toast.LENGTH_LONG).show()
                hideProgress()
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, vkCallback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account!!.idToken
            viewModel.openMainPage()
            Timber.tag(TAG).w("Sign In: $idToken")
        } catch (e: ApiException) {
            Timber.tag(TAG).e(e, "Sign In: failed, code=${e.statusCode}")
            Toast.makeText(requireContext(), getString(R.string.auth_failed), Toast.LENGTH_LONG).show()
            hideProgress()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inject() {
        FeatureUtils.getFeature<AuthComponent>(this, AuthApi::class.java)
            .authSubComponentFactory()
            .create(this)
            .inject(this)
    }

    private inner class GoogleSignInContractResult(val client: GoogleSignInClient) :
        ActivityResultContract<String, Task<GoogleSignInAccount>?>() {
        override fun createIntent(context: Context, input: String?): Intent {
            return client.signInIntent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Task<GoogleSignInAccount>? {
            return GoogleSignIn.getSignedInAccountFromIntent(intent)
        }

    }

    companion object {
        private val TAG = AuthFragment::class.simpleName!!
        const val RC_SIGN_IN = 93201
        const val APP_ID = "512001154908"
        const val APP_KEY = "CNFQJGKGDIHBABABA"
        const val REDIRECT_URI = "okauth://ok$APP_ID"
        const val OK_OAUTH_URL =
            "https://connect.ok.ru/oauth/authorize?client_id=$APP_ID&scope=VALUABLE_ACCESS;" +
                    "GET_EMAIL&response_type=token&redirect_uri=$REDIRECT_URI&layout=m"
    }
}