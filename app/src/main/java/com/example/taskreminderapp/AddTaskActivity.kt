package com.example.taskreminderapp

import Task
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.taskreminderapp.databinding.ActivityAddTaskBinding
import com.example.taskreminderapp.databinding.ViewDialogCustomBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAddTaskBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Setup Spinner Adapter untuk Repeat (pengulangan tugas)
        val repeatOptions = listOf("Once", "Daily", "Mon to Fri")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, repeatOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRepeat.adapter = adapter

        // DatePicker untuk memilih tanggal, akan menampilkan dialog saat diklik
        binding.etDatePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Mengatur teks pada EditText setelah tanggal dipilih
                    binding.etDatePicker.setText("$dayOfMonth/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show() // Menampilkan dialog DatePicker
        }

        // Tombol untuk menambah task baru
        binding.btnAddTask.setOnClickListener {
            val taskTitle = binding.etTaskTitle.text.toString()
            val repeatOption = binding.spinnerRepeat.selectedItem.toString()
            val selectedDate = binding.etDatePicker.text.toString()
            val selectedHour = binding.timePicker.hour
            val selectedMinute = binding.timePicker.minute

            // Validasi: Pastikan semua data telah diisi, jika tidak maka tampilkan Toast
            if (taskTitle.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(
                    this,
                    "Mohon isi semua data terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Jika validasi berhasil, tampilkan custom dialog
                showCustomDialog(
                    taskTitle,
                    repeatOption,
                    selectedDate,
                    selectedHour,
                    selectedMinute
                )
            }
        }
    }

    // Fungsi untuk menampilkan dialog konfirmasi sebelum task ditambahkan
    private fun showCustomDialog(
        taskTitle: String,
        repeatOption: String,
        selectedDate: String,
        selectedHour: Int,
        selectedMinute: Int
    ) {
        val dialogBinding = ViewDialogCustomBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)

        // Mengatur ukuran dialog (tinggi wrap_content, lebar match_parent)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Tombol OK pada dialog, ketika diklik akan menambah task baru
        dialogBinding.btnOk.setOnClickListener {
            // Buat object Task baru dengan data yang diinputkan
            val newTask = Task(taskTitle, repeatOption, selectedDate, selectedHour, selectedMinute)

            // Ambil daftar task lama dari SharedPreferences
            val sharedPreferences = getSharedPreferences("task_pref", Context.MODE_PRIVATE)
            val taskList = loadTaskList(sharedPreferences)

            // Tambahkan task baru ke dalam daftar task
            taskList.add(newTask)

            // Simpan daftar task yang diperbarui ke SharedPreferences
            saveTaskList(sharedPreferences, taskList)

            // Cek apakah task berhasil ditambahkan
            if (taskList.contains(newTask)) {
                Toast.makeText(this, "Berhasil menambahkan task", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal menambahkan task", Toast.LENGTH_SHORT).show()
            }

            // Kembali ke TaskListActivity setelah task berhasil ditambahkan
            startActivity(Intent(this, TaskListActivity::class.java))
            dialog.dismiss() // Menutup dialog
            finish() // Mengakhiri activity saat ini
        }

        // Tombol Cancel pada dialog untuk menutup dialog tanpa menambahkan task
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // Fungsi untuk memuat daftar task dari SharedPreferences
    private fun loadTaskList(sharedPreferences: android.content.SharedPreferences): MutableList<Task> {
        val gson = Gson()
        val json = sharedPreferences.getString("task_list", null)
        val type = object : TypeToken<MutableList<Task>>() {}.type
        // Jika data kosong, kembalikan daftar kosong, jika ada data, parse JSON menjadi daftar task
        return if (json == null) mutableListOf() else gson.fromJson(json, type)
    }

    // Fungsi untuk menyimpan daftar task ke SharedPreferences
    private fun saveTaskList(
        sharedPreferences: android.content.SharedPreferences,
        taskList: MutableList<Task>
    ) {
        val gson = Gson()
        val editor = sharedPreferences.edit()
        val json = gson.toJson(taskList) // Konversi daftar task ke dalam format JSON
        editor.putString("task_list", json) // Simpan JSON ke SharedPreferences
        editor.apply() // Terapkan perubahan ke SharedPreferences
    }
}
