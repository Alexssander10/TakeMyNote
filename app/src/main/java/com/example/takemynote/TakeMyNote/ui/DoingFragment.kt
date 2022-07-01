package com.example.takemynote.TakeMyNote.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.takemynote.R
import com.example.takemynote.TakeMyNote.helper.FirebaseHelper
import com.example.takemynote.TakeMyNote.model.Note
import com.example.takemynote.TakeMyNote.ui.adapter.NoteAdapter
import com.example.takemynote.databinding.FragmentDoingBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DoingFragment : Fragment() {

    private var _binding: FragmentDoingBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteAdapter: NoteAdapter

    private val noteList = mutableListOf<Note>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDoingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getNotes()
    }

    private fun getNotes() {
        FirebaseHelper
            .getDatabase()
            .child("note")
            .child(FirebaseHelper.getIdUser() ?: "")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        noteList.clear()
                        for (snap in snapshot.children) {
                            val note = snap.getValue(Note::class.java) as Note

                            if(note.status == 1) noteList.add(note)
                        }

                        noteList.reverse()
                        initAdapter()
                    }

                    notesEmpty()

                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Erro", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun notesEmpty() {
        binding.textInfo.text = if(noteList.isEmpty()){
            getText(R.string.text_note_list_empty_doing_fragment)
        }else {
            ""
        }
    }

    private fun initAdapter() {
        binding.rvNote.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNote.setHasFixedSize(true)
        noteAdapter = NoteAdapter(requireContext(), noteList) { note, select ->
            optionSelect(note, select)
        }
        binding.rvNote.adapter = noteAdapter
    }

    private fun optionSelect(note: Note, select: Int) {
        when(select) {
            NoteAdapter.SELECT_REMOVE -> {
                deleteNote(note)
            }
            NoteAdapter.SELECT_EDIT -> {
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFormNoteFragment(note)
                findNavController().navigate(action)
            }
            NoteAdapter.SELECT_BACK -> {
                note.status = 0
                updateNote(note)
            }
            NoteAdapter.SELECT_NEXT -> {
                note.status = 2
                updateNote(note)
            }
        }
    }

    private fun updateNote(note: Note) {
        FirebaseHelper
            .getDatabase()
            .child("note")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(note.id)
            .setValue(note)
            .addOnCompleteListener { note ->
                if (note.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Tarefa atualizada com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Falha ao salvar a tarefa!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                binding.progressBar.isVisible = false
                Toast.makeText(
                    requireContext(),
                    "Falha ao salvar a tarefa!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun deleteNote(note: Note) {
        FirebaseHelper
            .getDatabase()
            .child("note")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(note.id)
            .removeValue()

        noteList.remove(note)
        noteAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}