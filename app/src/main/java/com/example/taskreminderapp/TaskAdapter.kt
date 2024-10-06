package com.example.taskreminderapp

import Task
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.taskreminderapp.databinding.LayoutTaskBinding

class TaskAdapter(
    // Daftar task yang akan ditampilkan
    private val tasks: MutableList<Task>,
    // Fungsi callback untuk menghapus task saat opsi delete dipilih
    private val onDeleteTask: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    // ViewHolder untuk menampilkan item layout task
    inner class TaskViewHolder(val binding: LayoutTaskBinding) :
        RecyclerView.ViewHolder(binding.root)

    // Fungsi untuk membuat ViewHolder baru, layout task akan di-inflate dari file XML ke dalam ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = LayoutTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    // Fungsi untuk mengikat data dari sebuah task ke ViewHolder
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        // Ambil task berdasarkan posisi di daftar
        val task = tasks[position]

        // Mengikat data task ke komponen UI di layout task
        holder.binding.apply {
            tvTaskTitle.text = task.title
            tvTaskDate.text = task.date
            tvTaskTime.text = "${task.hour}:${task.minute}"
            tvTaskRepeat.text = task.repeat

            // Mengatur opsi klik untuk tombol more options (tiga titik)
            btnMoreOptions.setOnClickListener { view ->
                // Membuat PopupMenu saat tombol more options diklik
                val popup = PopupMenu(view.context, view)
                // Inflate menu dari resource menu_task_options.xml
                popup.menuInflater.inflate(R.menu.menu_task_options, popup.menu)
                // Mengatur aksi saat salah satu item menu diklik
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        // Jika item 'Delete' dipilih
                        R.id.action_delete -> {
                            onDeleteTask(task) // Memanggil fungsi callback untuk menghapus task
                            true
                        }

                        else -> false
                    }
                }
                popup.show()
            }
        }
    }

    // Mengembalikan jumlah task dalam daftar
    override fun getItemCount(): Int = tasks.size
}
