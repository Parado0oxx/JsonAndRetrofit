package com.example.network

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.network.adapterRecyclerView.AdapterMovie
import com.example.network.data.RemoteMovie
import com.example.network.utils.autoCleared
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.listMovie
import kotlinx.android.synthetic.main.fragment_main.titleMovie

class MainFragment : Fragment(R.layout.fragment_main) {

    private var movieAdapter: AdapterMovie by autoCleared()

    private val viewModelMovie: ViewModelMovie by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        bindViewModel()
    }

    private fun initList() {
        movieAdapter = AdapterMovie { action -> findNavController().navigate(action) }
        with(listMovie) {
            adapter = movieAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun bindViewModel() {
        searchButton.setOnClickListener {
            viewModelMovie.search(
                titleMovie.text.toString()
            )
        }

        retryButton.setOnClickListener {
            viewModelMovie.search(
                titleMovie.text.toString()
            )
            errorTextView.isVisible = false
            retryButton.isVisible = false
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModelMovie.isLoading.observe(viewLifecycleOwner, ::updateLoading)

        viewModelMovie.movieList.observe(viewLifecycleOwner) { movies ->
            result(movies)
        }

        viewModelMovie.errorMessage.observe(viewLifecycleOwner) { message ->
            errorTextView.text = message
        }

        viewModelMovie.errorDownload.observe(viewLifecycleOwner) { error ->
            if (viewModelMovie.errorDownload.value == error) {
                progressBar.isVisible = false
                errorTextView.isVisible = true
                retryButton.isVisible = true
            }
        }
    }

    private fun result(listMovies: List<RemoteMovie>) {
        if (listMovies.isNotEmpty()) {
            movieAdapter.items = listMovies
            resultSearch.isVisible = true
            errorSearch.isVisible = false
        } else {
            errorSearch.isVisible = true
            resultSearch.isVisible = false
        }
    }

    private fun updateLoading(isLoading: Boolean) {
        progressBar.isVisible = isLoading
        titleMovie.isEnabled = isLoading.not()
        searchButton.isEnabled = isLoading.not()
    }
}