package com.example.animalcrossingserotoningenerator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animalcrossingserotoningenerator.ProfileViewModel
import com.example.animalcrossingserotoningenerator.R
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class ProfilePhotoFragment : Fragment() {
    private lateinit var viewModel: ProfilePhotoViewModel

    private fun submitList(list: List<String>, adapter: ProfilePhotoRVAdapter) {
        adapter.submitList(list)
    }

    private fun initRecyclerView(root: View): ProfilePhotoRVAdapter {
        val rv = root.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = ProfilePhotoRVAdapter(viewModel)
        rv.adapter = adapter
        rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        itemDecor.setDrawable(ContextCompat.getDrawable(rv.context, (R.drawable.divider))!!)
        rv.addItemDecoration(itemDecor)
        return adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Each fragment has its own ViewModel
        viewModel =
            ViewModelProviders.of(this)[ProfilePhotoViewModel::class.java]
        var root = inflater.inflate(R.layout.profile_photo, container, false)

        viewModel.init(context!!)
        val adapter = initRecyclerView(root)

        viewModel.getList().observe(this, Observer {
            Log.d(javaClass.simpleName, "getList Observe")
            submitList(it, adapter)
        })

        return root
    }
}