package com.pab.trivku.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pab.trivku.LoginActivity
import com.pab.trivku.R
import com.pab.trivku.data.FavoriteRepository
import com.pab.trivku.data.local.AppDatabase
import com.pab.trivku.data.models.Destination
import com.pab.trivku.data.pref.SessionManager
import com.pab.trivku.ui.adapters.DestinationAdapter
import com.pab.trivku.viewmodel.FavoriteViewModel
import com.pab.trivku.viewmodel.FavoriteViewModelFactory

class FavoriteFragment : Fragment() {

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: DestinationAdapter
    private lateinit var tvSectionTitle: TextView

    private var activeLiveData: LiveData<List<Destination>>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvSectionTitle = view.findViewById(R.id.tvSectionTitle)

        setupHeader(view)
        setupViewModel()
        setupRecyclerView(view)
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
        val db = AppDatabase.getDatabase(requireContext())
        val repo = FavoriteRepository(db.favoriteDao(), db.destinationDao())

        viewModel = ViewModelProvider(
            this,
            FavoriteViewModelFactory(repo)
        )[FavoriteViewModel::class.java]
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.destinations_recycler_view)
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()?: return

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val db = AppDatabase.getDatabase(requireContext())
        adapter = DestinationAdapter(
            destinations = emptyList(),
            onFavoriteClick = { destination, _ ->
                viewModel.toggleFavorite(userId, destination.id, destination.isFavorite)
            },
            useSecondaryCard = true,
            favoriteDao = db.favoriteDao(),
            userId = userId,
            scope = viewLifecycleOwner.lifecycleScope
        )
        recyclerView.adapter = adapter
    }

    private fun observeAll() {
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()
        switchObserver(viewModel.getFavorites(userId))
    }

    private fun switchObserver(source: LiveData<List<Destination>>) {
        activeLiveData?.removeObservers(viewLifecycleOwner)
        activeLiveData = source
        activeLiveData?.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }
    }
}