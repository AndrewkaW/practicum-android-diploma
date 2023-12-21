package ru.practicum.android.diploma.filter.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentChoosingIndustryBinding
import ru.practicum.android.diploma.filter.presentation.adapter.FilterAdapter
import ru.practicum.android.diploma.filter.presentation.adapter.model.AreaDataInterface
import ru.practicum.android.diploma.filter.presentation.view_model.ChoosingIndustryViewModel
import ru.practicum.android.diploma.filter.presentation.view_model.model.FilterParametersState
import ru.practicum.android.diploma.util.DataUtils

class ChoosingIndustryFragment : Fragment() {

    private var _binding: FragmentChoosingIndustryBinding? = null
    private val binding get() = _binding!!

    private val adapter = FilterAdapter { clickOnIndustry(it) }
    private var recyclerView: RecyclerView? = null
    private var inputEditText: EditText? = null

    private val vM: ChoosingIndustryViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChoosingIndustryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ibArrowBack.setOnClickListener {
            findNavController().popBackStack()
        }

        recyclerView = binding.rvIndustry
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        vM.stateLiveData.observe(viewLifecycleOwner) {
            showState(it)
        }
        vM.getIndustries()

        vM.buttonIsVisibleLiveData.observe(viewLifecycleOwner) {
            binding.btnChoose.isVisible = it
        }

        binding.btnChoose.setOnClickListener {
            vM.saveIndustryToFilter()
            findNavController().popBackStack()
        }

        inputEditText = binding.etSearch
        inputEditText?.addTextChangedListener(tWCreator())
        inputEditText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                vM.loadJobs(inputEditText?.text.toString())
                true
            }
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        recyclerView = null
        inputEditText = null
    }

    private fun showState(type: FilterParametersState) {
        when (type) {
            FilterParametersState.Loading -> {
                setLoading()
            }

            is FilterParametersState.Error -> {
                setError(type.errorMessage)
            }

            is FilterParametersState.ParametersResult -> {
                setResult(type.list)
            }
        }
    }

    private fun setLoading() {
        binding.rvIndustry.visibility = View.GONE
        binding.pbLoading.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE
        binding.ivError.visibility = View.GONE
        binding.btnChoose.visibility = View.GONE
    }

    private fun setError(errorMassage: String) {
        binding.rvIndustry.visibility = View.GONE
        binding.pbLoading.visibility = View.GONE
        binding.tvError.visibility = View.VISIBLE
        binding.ivError.visibility = View.VISIBLE
        binding.btnChoose.visibility = View.GONE

        when (errorMassage) {
            DataUtils.ERROR -> {
                binding.tvError.setText(R.string.failed_to_get_the_list)
                binding.ivError.setImageResource(R.drawable.not_found_list)
            }

            DataUtils.CONNECTION_ERROR -> {
                binding.tvError.setText(R.string.internet_connection_issue)
                binding.ivError.setImageResource(R.drawable.disconnect)
            }

            DataUtils.NOT_FOUND -> {
                binding.tvError.setText(R.string.there_is_no_such_industry)
                binding.ivError.setImageResource(R.drawable.error_list_favorite)
            }
        }
    }

    private fun setResult(list: List<AreaDataInterface>) {
        binding.rvIndustry.visibility = View.VISIBLE
        binding.pbLoading.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        binding.ivError.visibility = View.GONE
        binding.btnChoose.visibility = View.GONE
        adapter.list = list
        adapter.notifyDataSetChanged()
    }

    private fun clickOnIndustry(industryUi: AreaDataInterface) {
        vM.saveToVm(industryUi)
    }

    private fun tWCreator() = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            changingIconEt()
            vM.loadJobs(inputEditText?.text.toString())
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun changingIconEt() {
        if (inputEditText?.text.isNullOrEmpty()) {
            binding.tilSearch.endIconDrawable = requireContext().getDrawable(R.drawable.search)
            binding.tilSearch.endIconMode = TextInputLayout.END_ICON_CUSTOM
            val view = requireActivity().currentFocus
            if (view != null) {
                val inputMethodManager =
                    requireContext().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                view.clearFocus()
            }
            vM.getIndustries()
        } else {
            binding.tilSearch.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
            binding.tilSearch.endIconDrawable = requireContext().getDrawable(R.drawable.cross)
        }
    }
}