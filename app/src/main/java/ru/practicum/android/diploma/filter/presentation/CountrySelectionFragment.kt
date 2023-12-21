package ru.practicum.android.diploma.filter.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentCountrySelectionBinding
import ru.practicum.android.diploma.filter.presentation.adapter.FilterAdapter
import ru.practicum.android.diploma.filter.presentation.adapter.model.AreaDataInterface
import ru.practicum.android.diploma.filter.presentation.view_model.CountrySelectionViewModel
import ru.practicum.android.diploma.filter.presentation.view_model.model.FilterParametersState
import ru.practicum.android.diploma.util.DataUtils.Companion.CONNECTION_ERROR
import ru.practicum.android.diploma.util.DataUtils.Companion.ERROR

class CountrySelectionFragment : Fragment() {

    private var _binding: FragmentCountrySelectionBinding? = null
    private val binding get() = _binding!!

    private val vM: CountrySelectionViewModel by viewModel()
    private val adapter = FilterAdapter { clickOnCountry(it) }
    private var recyclerView: RecyclerView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCountrySelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.rvRegion
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        vM.getCountryList()

        vM.stateLiveData.observe(viewLifecycleOwner) {
            showState(it)
        }

        binding.ibArrowBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        recyclerView = null
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
        binding.rvRegion.visibility = View.GONE
        binding.pbLoading.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE
        binding.ivError.visibility = View.GONE
    }

    private fun setError(errorMassage: String) {
        binding.rvRegion.visibility = View.GONE
        binding.pbLoading.visibility = View.GONE
        binding.tvError.visibility = View.VISIBLE
        binding.ivError.visibility = View.VISIBLE

        when (errorMassage) {
            ERROR -> {
                binding.tvError.setText(R.string.failed_to_get_the_list)
                binding.ivError.setImageResource(R.drawable.not_found_list)
            }

            CONNECTION_ERROR -> {
                binding.tvError.setText(R.string.internet_connection_issue)
                binding.ivError.setImageResource(R.drawable.disconnect)
            }
        }
    }

    private fun setResult(list: List<AreaDataInterface>) {
        binding.rvRegion.visibility = View.VISIBLE
        binding.pbLoading.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        binding.ivError.visibility = View.GONE
        adapter.list = list
        adapter.notifyDataSetChanged()
    }

    private fun clickOnCountry(country: AreaDataInterface) {
        vM.saveCountryInFilter(country)
        findNavController().popBackStack()
    }
}