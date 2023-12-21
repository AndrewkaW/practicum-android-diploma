package ru.practicum.android.diploma.search.presentation

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.search.presentation.models.SearchStates
import ru.practicum.android.diploma.util.TextUtils

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SearchViewModel>()
    private lateinit var jobClickCb: (String) -> Unit
    private lateinit var adapter: JobAdapter

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshFilter()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        jobClickCb = initClickCb()
        viewModel.startFilterCheck()
        val recyclerView = binding.rvSearch
        adapter = JobAdapter(jobClickCb)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        viewModel.getState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchStates.Start -> setDefaultScreen()
                is SearchStates.ServerError -> setErrorScreen()
                is SearchStates.ConnectionError -> setConnectionLostScreen()
                is SearchStates.InvalidRequest -> setInvalidRequestScreen()
                is SearchStates.Success -> {
                    setSuccessScreen(state.found)
                    adapter.jobsList = state.jobList.toMutableList()
                }

                is SearchStates.Loading -> setLoadingPaggScreen()
                is SearchStates.FilterChanged -> changeFilterTint(state.filterNotBase)
                else -> Unit
            }
        }

        binding.etSearch.addTextChangedListener(tWCreator())

        binding.ivFilter.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_filterSettingsFragment)
        }

        binding.bClearText.setOnClickListener {
            clearText()
        }

        binding.rvSearch.addOnScrollListener(initScrlLsnr())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setDefaultScreen() {
        with(binding) {
            rvSearch.visibility = GONE
            ivError.setImageResource(R.drawable.search_start)
            tvError.visibility = GONE
            tvRvHeader.visibility = GONE
            pagingPrBar.visibility = GONE
            ivError.visibility = VISIBLE
        }
    }

    private fun setErrorScreen() {
        with(binding) {
            rvSearch.visibility = GONE
            ivError.visibility = VISIBLE
            ivError.setImageResource(R.drawable.error_server_2)
            tvError.visibility = VISIBLE
            tvError.setText(R.string.server_error)
            tvRvHeader.visibility = GONE
            pagingPrBar.visibility = GONE
        }
    }

    private fun setConnectionLostScreen() {
        with(binding) {
            rvSearch.visibility = GONE
            ivError.visibility = VISIBLE
            ivError.setImageResource(R.drawable.disconnect)
            tvError.visibility = VISIBLE
            tvError.setText(R.string.internet_connection_issue)
            tvRvHeader.visibility = GONE
            pagingPrBar.visibility = GONE
        }
    }

    private fun setSuccessScreen(amount: Int) {
        with(binding) {
            rvSearch.visibility = VISIBLE
            ivError.visibility = GONE
            tvError.visibility = GONE
            tvRvHeader.visibility = VISIBLE
            tvRvHeader.text = getString(R.string.founded, TextUtils.addSeparator(amount))
            pagingPrBar.visibility = GONE
        }
    }

    private fun setInvalidRequestScreen() {
        with(binding) {
            rvSearch.visibility = GONE
            ivError.visibility = VISIBLE
            ivError.setImageResource(R.drawable.error_list_favorite)
            tvError.visibility = VISIBLE
            tvError.setText(R.string.error_list_favorite)
            tvRvHeader.visibility = VISIBLE
            tvRvHeader.setText(R.string.vacancy_mismatch)
            pagingPrBar.visibility = GONE
        }
    }

    private fun setLoadingPaggScreen() {
        with(binding) {
            ivError.visibility = GONE
            tvError.visibility = GONE
            pagingPrBar.visibility = VISIBLE
        }
    }

    private fun changeFilterTint(hasFilter: Boolean) {
        if (hasFilter)
            binding.ivFilter.setImageResource(R.drawable.filter_blue)
        else
            binding.ivFilter.setImageResource(R.drawable.filter)
    }

    private fun tWCreator() = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            changeVisBottomNav(GONE)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (count > 0) {
                val endDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.cross)
                binding.etSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    endDrawable,
                    null
                )
            }

            if (start != before) {
                viewModel.loadJobs(s?.toString() ?: "")
            }
        }

        override fun afterTextChanged(s: Editable?) {
            changeVisBottomNav(VISIBLE)
        }
    }

    private fun initClickCb(): (String) -> Unit = { jobId ->
        val arg = SearchFragmentDirections.actionSearchFragmentToJobFragment(jobId)
        findNavController().navigate(arg)
    }

    private fun initScrlLsnr() = object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val pos =
                (binding.rvSearch.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            val itemsCount = adapter.itemCount
            if (pos >= itemsCount - 1) {
                viewModel.getNewPage()
            }
        }
    }

    private fun changeVisBottomNav(state: Int) {
        val bottomNav =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.visibility = state
    }

    private fun clearText() {
        binding.etSearch.setText("")
        viewModel.clearAll()
        val endDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.search)
        binding.etSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            endDrawable,
            null
        )
    }

    companion object {
        const val VISIBLE = View.VISIBLE
        const val GONE = View.GONE
    }
}
