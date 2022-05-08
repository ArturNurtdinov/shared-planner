package ru.spbstu.calendar.settings.profile.presentation

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.spbstu.calendar.R
import ru.spbstu.calendar.databinding.ProfileFragmentBinding
import ru.spbstu.calendar.di.CalendarApi
import ru.spbstu.calendar.di.CalendarComponent
import ru.spbstu.calendar.domain.model.Profile
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.common.extensions.getInitials
import ru.spbstu.common.extensions.setDebounceClickListener
import javax.inject.Inject

class ProfileFragment : Fragment() {

    @Inject
    lateinit var viewModel: ProfileViewModel

    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileFragmentBinding.inflate(layoutInflater, container, false)
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

        binding.fragmentProfileBack.setDebounceClickListener {
            if (!viewModel.onBackClicked()) {
                requireActivity().finish()
            }
        }

        binding.fragmentProfileLogout.setDebounceClickListener {
            viewModel.logout()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.errorMessage
            .onEach {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
            .launchIn(lifecycleScope)

        viewModel.state
            .filterNotNull()
            .onEach {
                val profile = it.profile
                binding.fragmentProfileName.text = profile.name
                if (!profile.avatarUrl.isNullOrEmpty()) {
                    binding.fragmentProfileAvatar.layoutAvatarAvatar.isVisible = true
                    binding.fragmentProfileAvatar.layoutAvatarInitials.isVisible = false
                    Glide.with(this)
                        .load(profile.avatarUrl)
                        .centerCrop()
                        .into(binding.fragmentProfileAvatar.layoutAvatarAvatar)
                } else {
                    val initials = profile.name.getInitials()
                    if (initials == null) {
                        binding.fragmentProfileAvatar.layoutAvatarAvatar.isVisible = true
                        binding.fragmentProfileAvatar.layoutAvatarInitials.isVisible = false
                        binding.fragmentProfileAvatar.layoutAvatarAvatar.setImageDrawable(
                            ColorDrawable(
                                ContextCompat.getColor(requireContext(), R.color.primaryVariant)
                            )
                        )
                    } else {
                        binding.fragmentProfileAvatar.layoutAvatarAvatar.isVisible = false
                        binding.fragmentProfileAvatar.layoutAvatarInitials.isVisible = true
                        binding.fragmentProfileAvatar.layoutAvatarInitials.text =
                            profile.name.getInitials()
                    }
                }

                if (profile.email.isNullOrEmpty()) {
                    binding.fragmentProfileEmailLabel.isVisible = false
                    binding.fragmentProfileEmailCard.isVisible = false
                } else {
                    binding.fragmentProfileEmailLabel.isVisible = true
                    binding.fragmentProfileEmailCard.isVisible = true
                    binding.fragmentProfileEmail.text = profile.email
                }

                if (profile.phone.isNullOrEmpty()) {
                    binding.fragmentProfilePhoneLabel.isVisible = false
                    binding.fragmentProfilePhoneCard.isVisible = false
                } else {
                    binding.fragmentProfilePhoneLabel.isVisible = true
                    binding.fragmentProfilePhoneCard.isVisible = true
                    binding.fragmentProfilePhone.text = profile.phone
                }
            }
            .launchIn(lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun inject() {
        FeatureUtils.getFeature<CalendarComponent>(this, CalendarApi::class.java)
            .profileComponentFactory()
            .create(this)
            .inject(this)
    }

}