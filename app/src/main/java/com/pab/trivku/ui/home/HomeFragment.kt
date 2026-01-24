package com.pab.trivku.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.pab.trivku.LoginActivity
import com.pab.trivku.R
import com.pab.trivku.data.DestinationRepository
import com.pab.trivku.data.FavoriteRepository
import com.pab.trivku.data.local.AppDatabase
import com.pab.trivku.data.pref.SessionManager
import com.pab.trivku.ui.adapters.DestinationAdapter
import com.pab.trivku.viewmodel.DestinationViewModelFactory
import com.pab.trivku.viewmodel.DestinationsViewModel
import com.pab.trivku.viewmodel.FavoriteViewModel
import com.pab.trivku.viewmodel.FavoriteViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var viewModel: DestinationsViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var adapter: DestinationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader(view)
        setupViewModel()
        setupRecyclerView(view)
        setupTabLayout(view)
        observeData()
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
            useSecondaryCard = false,
            favoriteDao = db.favoriteDao(),
            userId = userId,
            scope = viewLifecycleOwner.lifecycleScope
        )
        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            this.adapter = this@HomeFragment.adapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupTabLayout(view: View) {
        val tabLayout = view.findViewById<TabLayout>(R.id.tabFilters)

        val categories = listOf("Semua", "Alam", "Sejarah")

        tabLayout.removeAllTabs()

        for (category in categories) {
            tabLayout.addTab(tabLayout.newTab().setText(category))
            val tabs = tabLayout.getChildAt(0) as ViewGroup
            for (i in 0 until tabs.childCount) {
                val tabView = tabs.getChildAt(i)
                val p = tabView.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(0, 0, 16, 0)
                tabView.requestLayout()
            }
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.text?.let { categoryName ->
                    viewModel.setCategory(categoryName.toString())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeData() {
        viewModel.filteredDestinations.observe(viewLifecycleOwner) { destinations ->
            destinations?.let {
                adapter.updateList(it)
            }
        }
    }
}