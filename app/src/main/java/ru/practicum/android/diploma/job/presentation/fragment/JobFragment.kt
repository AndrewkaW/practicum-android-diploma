package ru.practicum.android.diploma.job.presentation.fragment

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentJobBinding
import ru.practicum.android.diploma.job.data.impl.mapper.TypeForTextUtils
import ru.practicum.android.diploma.job.data.secondarymodels.Phones
import ru.practicum.android.diploma.job.domain.models.JobForScreen
import ru.practicum.android.diploma.job.presentation.fragment.adapter.PhonesAdapter
import ru.practicum.android.diploma.job.presentation.fragment.adapter.viewholder.PhonesViewHolder
import ru.practicum.android.diploma.job.presentation.states.JobScreenState
import ru.practicum.android.diploma.job.presentation.viewmodel.JobFragmentViewModel
import ru.practicum.android.diploma.similarjob.presentation.SimilarJobFragment
import ru.practicum.android.diploma.util.ImgFunctions
import ru.practicum.android.diploma.util.TextUtils
import java.util.Locale


class JobFragment : Fragment(), PhonesViewHolder.PhoneClickListener {
    private val jobFragmentViewModel: JobFragmentViewModel by viewModel()
    private var _binding: FragmentJobBinding? = null
    private val binding get() = _binding!!
    private lateinit var jobData: JobForScreen
    private val args: JobFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()

        jobFragmentViewModel.getJob(args.jobId)
        jobFragmentViewModel.includedToFavorite(args.jobId)

        jobFragmentViewModel.observeJobScreenLiveData()
            .observe(viewLifecycleOwner) { status ->
                showContentBasedOnState(status)
            }

        binding.ibFavourite.setOnClickListener {
            checkIsFavourite(jobFragmentViewModel.getFavouriteState())
        }

        binding.btnSimilarJobs.setOnClickListener {
            findNavController().navigate(
                R.id.action_jobFragment_to_similarJobFragment,
                SimilarJobFragment.createArgs(jobData.id)
            )
        }
    }

    private fun showContentBasedOnState(status: JobScreenState) {
        when (status) {
            is JobScreenState.Success -> {
                showMainContent(status.jobForScreen)
                topBtnsOn()
            }

            is JobScreenState.Loading -> {
                with(binding) {
                    llMainContent.visibility = View.GONE
                    pbJob.visibility = View.VISIBLE
                    llServerError.visibility = View.GONE
                }
                topBtnsOff()
            }

            is JobScreenState.JobFromDb -> {
                showMainContent(status.jobForScreenDb)
                topBtnsOn()
            }

            is JobScreenState.ServerError -> showError()
            is JobScreenState.InvalidRequest -> showError()
            is JobScreenState.FavouriteIcon -> checkFavouriteIcon(status.isFavourite)
        }
    }

    private fun topBtnsOn() {
        with(binding) {
            ibFavourite.isClickable = true
            ibShare.isClickable = true
        }
    }

    private fun topBtnsOff() {
        with(binding) {
            ibFavourite.isClickable = false
            ibShare.isClickable = false
        }
    }

    private fun showMainContent(job: JobForScreen) {
        fillContent(job)
        with(binding) {
            llMainContent.visibility = View.VISIBLE
            pbJob.visibility = View.GONE
            llServerError.visibility = View.GONE
        }
    }

    private fun showError() {
        with(binding) {
            llMainContent.visibility = View.GONE
            pbJob.visibility = View.GONE
            llServerError.visibility = View.VISIBLE
            topBtnsOff()
        }
    }


    private fun checkIsFavourite(isFavorite: Boolean) {
        if (isFavorite) {
            jobData.id?.let { id -> jobFragmentViewModel.deleteFromFavorite(id) }
        } else {
            jobFragmentViewModel.addToFavorite(jobData)
        }
    }

    private fun checkFavouriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.ibFavourite.setImageResource(R.drawable.ic_favorites_on)
        } else {
            binding.ibFavourite.setImageResource(R.drawable.ic_favorites_off)
        }
    }

    private fun fillContent(job: JobForScreen) {
        with(binding) {
            tvJobName.text = job.name

            if (!job.salary?.replace("\\s".toRegex(), "").isNullOrEmpty()) {
                tvSalary.text = job.salary
            } else {
                tvSalary.text = getString(R.string.empty_salary)
            }

            Glide.with(binding.ivJob)
                .load(job.employerLogoUrl)
                .placeholder(R.drawable.placeholder_32px)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        ImgFunctions.roundCorner(
                            requireActivity().resources
                                .displayMetrics.densityDpi, ROUNDING_OF_CORNERS_PX
                        )
                    )
                )
                .into(binding.ivJob)
            tvEmployer.text = job.employerName
            tvEmployerCity.text = job.address
            tvRequiredExperience.text = job.experience
            tvEmployment.text = job.employment
            tvJobDiscription.text = TextUtils.fromHtml(job.description)?.trim()
        }

        checkAndShowSkills(job)
        checkAndShowContacts(job)

        job.id?.let { jobFragmentViewModel.includedToFavorite(it) }
        jobData = job
    }

    @Suppress("UNCHECKED_CAST")
    private fun checkAndShowSkills(job: JobForScreen) {
        if (job.keySkills.isNotEmpty()) {
            with(binding) {
                llKeySkills.visibility = View.VISIBLE
                tvMainSkills.text =
                    TextUtils.arrayToStrInJob(job.keySkills as Array<Any>, TypeForTextUtils.Skills)
                        .trim()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun checkAndShowContacts(job: JobForScreen) {
        job.email?.let {
            with(binding) {
                llContacts.visibility = View.VISIBLE
                tvEmailStatic.visibility = View.VISIBLE
                tvEmailContacts.visibility = View.VISIBLE
                tvEmailContacts.text = job.email.trim()
            }
        }

        job.phones?.let {
            if (job.phones.isNotEmpty()) {
                with(binding) {
                    llContacts.visibility = View.VISIBLE

                    job.contactsName?.let {
                        tvContactNameStatic.visibility = View.VISIBLE
                        tvContactName.visibility = View.VISIBLE
                        tvContactName.text = job.contactsName.trim()
                    }

                    TextUtils.arrayToStrInJob(
                        job.phones as Array<Any>,
                        TypeForTextUtils.Phones
                    ).let {
                        if (it.isNotEmpty()) {
                            tvPhoneStatic.visibility = View.VISIBLE
                            val adapterRv = PhonesAdapter(
                                formatPhones(job.phones),
                                this@JobFragment
                            )
                            rvPhonesNumbers.layoutManager = LinearLayoutManager(requireContext())
                            rvPhonesNumbers.adapter = adapterRv
                        }
                    }

                    TextUtils.arrayToStrInJob(
                        job.phones as Array<Any>,
                        TypeForTextUtils.Comment
                    ).let {
                        if (it.isNotEmpty()) {
                            tvCommentStatic.visibility = View.VISIBLE
                            tvComments.visibility = View.VISIBLE
                            tvComments.text = it.trim()
                        }
                    }
                }
            }
        }
    }

    private fun formatPhones(phonesArray: Array<Phones?>?): List<String> {
        val formattedArray = mutableListOf<String>()

        phonesArray?.let {
            for (phone in phonesArray) {
                phone?.formatted?.let {
                    formattedArray.add(
                        PhoneNumberUtils.formatNumber(
                            phone.formatted,
                            Locale.getDefault().country
                        )
                    )
                }
            }
        }
        return formattedArray
    }

    private fun initListeners() {
        with(binding) {
            ibArrowBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ibShare.setOnClickListener {
                jobData.vacancyUrl?.let { jobFragmentViewModel.shareJobLink(it) }
            }

            tvEmailContacts.setOnClickListener {
                jobFragmentViewModel.shareEmail(tvEmailContacts.text.toString())
            }
        }
    }

    override fun setPhoneClickListener(phone: String) {
        jobFragmentViewModel.sharePhoneNumber(phone)
    }

    companion object {
        private const val ROUNDING_OF_CORNERS_PX = 12
    }
}