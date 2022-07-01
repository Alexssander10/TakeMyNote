package com.example.takemynote.TakeMyNote.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.takemynote.R
import com.example.takemynote.TakeMyNote.helper.BaseFragment
import com.example.takemynote.TakeMyNote.helper.FirebaseHelper
import com.example.takemynote.TakeMyNote.helper.initToolbar
import com.example.takemynote.TakeMyNote.model.Note
import com.example.takemynote.databinding.FragmentFormNoteBinding
import com.example.takemynote.databinding.FragmentHomeBinding

class FormNoteFragment : BaseFragment() {

    private val args: FormNoteFragmentArgs by navArgs()

    private var _binding: FragmentFormNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var note: Note
    private var newNote: Boolean = true
    private var statusNote: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFormNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar(binding.toolbar)

        initListener()

        getArgs()
    }

    private fun getArgs() {
        args.note.let {
            if (it != null) {
                note = it

                configNote()
            }
        }
    }

    private fun configNote() {
        newNote = false
        statusNote = note.status
        binding.textToolbar.text = "Editando tarefa..."

        binding.edtDescription.setText(note.description)
        setStatus()
    }

    private fun setStatus() {
        binding.radioGroup.check(
            when(note.status) {
                0 -> {
                    R.id.rbTodo
                }
                1 -> {
                    R.id.rbDoing
                }
                else -> {
                    R.id.rbDone
                }
            }
        )
    }

    private fun initListener() {
        binding.btnSave.setOnClickListener { validatedData() }

        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            statusNote = when (id) {
                R.id.rbTodo -> 0
                R.id.rbDoing -> 1
                else -> 2
            }
        }
    }

    private fun validatedData() {
        val description = binding.edtDescription.text.toString()

        if (description.isNotEmpty()) {

            hideKeyboard()

            binding.progressBar.isVisible = true

            if (newNote) note = Note()
            note.description = description
            note.status = statusNote

            saveNote()
        } else {
            Toast.makeText(
                requireContext(),
                "Informe uma descrição para a tarefa.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveNote() {
        FirebaseHelper
            .getDatabase()
            .child("note")
            .child(FirebaseHelper.getIdUser() ?: "")
            .child(note.id)
            .setValue(note)
            .addOnCompleteListener { note ->
                if (note.isSuccessful) {
                    if (newNote) { // Uma nota tarefa
                        findNavController().popBackStack()
                        Toast.makeText(
                            requireContext(),
                            "Tarefa salva com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else { // Editando tarefa
                        binding.progressBar.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            "Tarefa atualizada com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}