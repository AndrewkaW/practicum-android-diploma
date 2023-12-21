package ru.practicum.android.diploma.filter.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterSettingsBinding
import ru.practicum.android.diploma.filter.presentation.view_model.FilterSettingsViewModel


class FilterSettingsFragment : Fragment() {

    private var _binding: FragmentFilterSettingsBinding? = null
    private val binding get() = _binding!!
    private val vM: FilterSettingsViewModel by viewModel()

    private var salaryIsChange: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ibArrowBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvIndustryClear.setOnClickListener {
            findNavController().navigate(R.id.action_filterSettingsFragment_to_choosingIndustryFragment2)
        }

        binding.clIndustry.setOnClickListener {
            findNavController().navigate(R.id.action_filterSettingsFragment_to_choosingIndustryFragment2)
        }

        binding.tvPlaceOfWorkClear.setOnClickListener {
            findNavController().navigate(R.id.action_filterSettingsFragment_to_placesOfWorkFragment2)
        }

        binding.clPlaceOfWork.setOnClickListener {
            findNavController().navigate(R.id.action_filterSettingsFragment_to_placesOfWorkFragment2)
        }

        binding.ivClearPlaceOfWork.setOnClickListener {
            vM.clearPlaceOfWork()
        }

        binding.ivClearIndustry.setOnClickListener {
            vM.clearIndustry()
        }

        vM.placeOfWork.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.tvPlaceOfWorkClear.visibility = View.VISIBLE
                binding.clPlaceOfWork.visibility = View.GONE
            } else {
                binding.tvPlaceOfWorkText.text = it
                binding.tvPlaceOfWorkClear.visibility = View.GONE
                binding.clPlaceOfWork.visibility = View.VISIBLE
            }
        }

        vM.industry.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.tvIndustryClear.visibility = View.VISIBLE
                binding.clIndustry.visibility = View.GONE
            } else {
                binding.tvIndustryText.text = it
                binding.clIndustry.visibility = View.VISIBLE
                binding.tvIndustryClear.visibility = View.GONE
            }
        }

        vM.salary.observe(viewLifecycleOwner) {
            binding.etSalary.setText(it)
        }

        vM.showWithSalary.observe(viewLifecycleOwner) {
            binding.cbShowWithSalary.isChecked = it
        }

        binding.etSalary.doOnTextChanged { text, _, _, _ ->
            salaryIsChange = !text.isNullOrEmpty()
            buttonVisibility()
        }
        binding.cbShowWithSalary.setOnClickListener {
            buttonVisibility()
        }

        binding.btnAccept.setOnClickListener {
            vM.saveSalarySettings(
                salary = binding.etSalary.text.toString(),
                onlyWithSalary = binding.cbShowWithSalary.isChecked
            )
            findNavController().popBackStack()
        }

        binding.btnReset.setOnClickListener {
            vM.clearFilterSettings()
            vM.setFiltersInFragment()
            buttonVisibility()
        }

        buttonVisibility()

        vM.setFiltersInFragment()

    }

    override fun onResume() {
        super.onResume()
        buttonVisibility()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun buttonVisibility() {
        if (!salaryIsChange
            && binding.tvPlaceOfWorkText.text.isNullOrEmpty()
            && binding.tvIndustryText.text.isNullOrEmpty()
            && binding.cbShowWithSalary.isChecked == vM.showWithSalary.value
        ) {
            binding.btnAccept.isVisible = false
            binding.btnReset.isVisible = false
        } else {
            binding.btnAccept.isVisible = true
            binding.btnReset.isVisible = true
        }
    }
}