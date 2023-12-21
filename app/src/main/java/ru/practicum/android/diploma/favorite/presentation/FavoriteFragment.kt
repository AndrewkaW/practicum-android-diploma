package ru.practicum.android.diploma.favorite.presentation

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFavoriteBinding
import ru.practicum.android.diploma.favorite.domain.FavoriteState
import ru.practicum.android.diploma.search.presentation.JobAdapter

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private val viewModel: FavoriteViewModel by viewModel()

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("UseSwitchCompatOrMaterialCode", "NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.rvSearch
        val adapter = JobAdapter(initClickCb())

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.getFavoriteLiveData().observe(viewLifecycleOwner) { pairDataFavorite ->
            when (pairDataFavorite.first) {
                FavoriteState.DATA -> {
                    binding.rvSearch.isGone = false
                    binding.tvMessage.isGone = true
                    binding.ivPlaceholderPng.isGone = true
                    adapter.jobsList = pairDataFavorite.second
                    adapter.notifyDataSetChanged()
                }

                FavoriteState.EMPTY -> {
                    binding.ivPlaceholderPng.setImageResource(R.drawable.empty_list_favorite)
                    binding.tvMessage.text = getText(R.string.empty_list)
                    binding.rvSearch.isGone = true
                    binding.tvMessage.isGone = false
                    binding.ivPlaceholderPng.isGone = false
                }

                else -> {
                    binding.ivPlaceholderPng.setImageResource(R.drawable.error_list_favorite)
                    binding.tvMessage.text = getText(R.string.error_list_favorite)
                    binding.rvSearch.isGone = true
                    binding.tvMessage.isGone = false
                    binding.ivPlaceholderPng.isGone = false
                }
            }
        }

        viewModel.getFavorite()
    }

    private fun initClickCb(): (String) -> Unit = { jobId ->
        val arg = FavoriteFragmentDirections.actionFavoriteFragmentToJobFragment(jobId)
        findNavController().navigate(arg)
    }
}