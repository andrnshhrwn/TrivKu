package com.pab.trivku.ui.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pab.trivku.LoginActivity
import com.pab.trivku.R
import com.pab.trivku.data.DestinationRepository
import com.pab.trivku.data.FavoriteRepository
import com.pab.trivku.data.local.AppDatabase
import com.pab.trivku.data.models.Destination
import com.pab.trivku.data.pref.SessionManager
import com.pab.trivku.ui.adapters.DestinationAdapter
import com.pab.trivku.viewmodel.DestinationViewModelFactory
import com.pab.trivku.viewmodel.DestinationsViewModel
import com.pab.trivku.viewmodel.FavoriteViewModel
import com.pab.trivku.viewmodel.FavoriteViewModelFactory

class SearchFragment : Fragment() {

    private lateinit var viewModel: DestinationsViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var adapter: DestinationAdapter
    private lateinit var tvSectionTitle: TextView

    private var activeLiveData: LiveData<List<Destination>>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvSectionTitle = view.findViewById(R.id.tvSectionTitle)

        setupHeader(view)
        setupViewModel()
        setupRecyclerView(view)
        setupSearch(view)
        observeAll()
    }

    private fun setupHeader(view: View) {
        val sessionManager = SessionManager(requireContext())

        // Menampilkan Username
        val tvUsername = view.findViewById<TextView>(R.id.tvUsername)
        tvUsername.text = sessionManager.getUserName() ?: "Guest"

        // Logika Logout
        view.findViewById<TextView>(R.id.logout).setOnClickListener {
            sessionManager.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    private fun setupViewModel() {
        val destinationDao = AppDatabase.getDatabase(requireContext()).destinationDao()
        val repository = DestinationRepository(destinationDao)
        val favDao = AppDatabase.getDatabase(requireContext()).favoriteDao()
        val repoFav = FavoriteRepository(favDao, destinationDao)

        viewModel = ViewModelProvider(
            this,
            DestinationViewModelFactory(repository)
        )[DestinationsViewModel::class.java]

        favoriteViewModel = ViewModelProvider(
            this,
            FavoriteViewModelFactory(repoFav)
        )[FavoriteViewModel::class.java]
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.destinations_recycler_view)
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId() ?: return

        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                gapStrategy =
                    StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            }

        val db = AppDatabase.getDatabase(requireContext())
        adapter = DestinationAdapter(
            destinations = emptyList(),
            onFavoriteClick = { destination, position ->
                // Ambil status saat ini
                val currentStatus = destination.isFavorite

                destination.isFavorite = !currentStatus

                // Refresh
                adapter.notifyItemChanged(position)

                // Kirim status ASLI (sebelum diubah) ke toggleFavorite
                favoriteViewModel.toggleFavorite(userId, destination.id, currentStatus)

                // Trigger refresh untuk sinkronisasi data
                viewModel.triggerRefresh()
            },
            useSecondaryCard = true,
            favoriteDao = db.favoriteDao(),
            userId = userId,
            scope = viewLifecycleOwner.lifecycleScope
        )
        recyclerView.adapter = adapter
    }

    private fun setupSearch(view: View) {
        val etSearch = view.findViewById<EditText>(R.id.search)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim().orEmpty()

                if (query.isEmpty()) {
                    tvSectionTitle.text = "Recommended"
                    observeAll()
                } else {
                    tvSectionTitle.text = "Result"
                    observeSearch(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeAll() {
        switchObserver(viewModel.allDestinations)
    }

    private fun observeSearch(query: String) {
        switchObserver(viewModel.searchDestinations("%$query%"))
    }

    private fun switchObserver(source: LiveData<List<Destination>>) {
        activeLiveData?.removeObservers(viewLifecycleOwner)
        activeLiveData = source
        activeLiveData?.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }
    }
}