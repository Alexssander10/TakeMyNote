package com.example.takemynote.TakeMyNote.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.takemynote.R
import com.example.takemynote.TakeMyNote.model.Note
import com.example.takemynote.databinding.ItemAdapterBinding

class NoteAdapter(
    private val context: Context,
    private val noteList: List<Note>,
    val noteSelected: (Note, Int) -> Unit
) : RecyclerView.Adapter<NoteAdapter.MyViewHolder>() {

    companion object {
        val SELECT_BACK: Int = 1
        val SELECT_REMOVE: Int = 2
        val SELECT_EDIT: Int = 3
        val SELECT_DETAILS: Int = 4
        val SELECT_NEXT: Int = 5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val note = noteList[position]

        holder.binding.textDescription.text = note.description

        holder.binding.btnDelete.setOnClickListener { noteSelected(note, SELECT_REMOVE ) }
        holder.binding.btnEdit.setOnClickListener { noteSelected(note, SELECT_EDIT) }
        holder.binding.btnDetails.setOnClickListener { noteSelected(note, SELECT_DETAILS) }

        when (note.status) {
            0 -> {
                holder.binding.imgBtnBack.isVisible = false

                holder.binding.imgBtnNext.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_doing)
                )

                holder.binding.imgBtnNext.setOnClickListener { noteSelected(note, SELECT_NEXT) }
            }
            1 -> {
                holder.binding.imgBtnBack.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_todo)
                )
                holder.binding.imgBtnNext.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_done)
                )

                holder.binding.imgBtnBack.setOnClickListener { noteSelected(note, SELECT_BACK) }
                holder.binding.imgBtnNext.setOnClickListener { noteSelected(note, SELECT_NEXT) }
            }
            else -> {
                holder.binding.imgBtnNext.isVisible = false

                holder.binding.imgBtnBack.setColorFilter(
                    ContextCompat.getColor(context, R.color.color_doing)
                )

                holder.binding.imgBtnBack.setOnClickListener { noteSelected(note, SELECT_BACK) }
            }
        }
    }

    override fun getItemCount() = noteList.size

    inner class MyViewHolder(val binding: ItemAdapterBinding) :
            RecyclerView.ViewHolder(binding.root)

}