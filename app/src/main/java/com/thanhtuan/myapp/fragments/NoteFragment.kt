package com.thanhtuan.myapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.thanhtuan.myapp.repositories.adapters.NoteAdapter
import com.thanhtuan.myapp.databinding.FragmentNoteBinding
import com.thanhtuan.myapp.viewmodels.NoteViewModel
import com.thanhtuan.myapp.viewmodels.NoteViewModelFactory

class NoteFragment : Fragment() {
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: NoteViewModel by viewModels { NoteViewModelFactory() }
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter()
        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = noteAdapter
        }
    }

    private fun setupObservers() {
        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            noteAdapter.submitList(notes)
        }
    }

    private fun setupClickListeners() {
        binding.addNoteFab.setOnClickListener {
            // TODO: Show dialog to add new note
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 