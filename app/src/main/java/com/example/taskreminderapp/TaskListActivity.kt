package com.example.taskreminderapp

import Task
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskreminderapp.databinding.ActivityTaskListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskListActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskList: MutableList<Task>

    private val binding by lazy {
        ActivityTaskListBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Load daftar task dari SharedPreferences
        val sharedPreferences = getSharedPreferences("task_pref", Context.MODE_PRIVATE)
        taskList = loadTaskList(sharedPreferences)

        // Periksa apakah daftar task kosong
        checkTaskListEmpty()

        // Set RecyclerView dengan taskAdapter
        taskAdapter = TaskAdapter(taskList) { task ->
            deleteTask(task, sharedPreferences)
        }
        binding.rvTaskList.layoutManager = LinearLayoutManager(this)
        binding.rvTaskList.adapter = taskAdapter

        // Tombol untuk menambah task baru
        binding.btnAddTask.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
            finish()
        }
    }

    // Fungsi untuk memeriksa apakah taskList kosong
    private fun checkTaskListEmpty() {
        if (taskList.isEmpty()) {
            // Jika taskList kosong, tampilkan pesan dan sembunyikan RecyclerView
            binding.tvEmptyTaskList.visibility = android.view.View.VISIBLE
            binding.rvTaskList.visibility = android.view.View.GONE
        } else {
            // Jika taskList tidak kosong, tampilkan RecyclerView dan sembunyikan pesan
            binding.tvEmptyTaskList.visibility = android.view.View.GONE
            binding.rvTaskList.visibility = android.view.View.VISIBLE
        }
    }

    // Fungsi untuk memuat daftar task dari SharedPreferences
    private fun loadTaskList(sharedPreferences: android.content.SharedPreferences): MutableList<Task> {
        val gson = Gson()
        val json = sharedPreferences.getString("task_list", null)
        val type = object : TypeToken<MutableList<Task>>() {}.type
        return if (json == null) mutableListOf() else gson.fromJson(json, type)
    }

    // Fungsi untuk menyimpan daftar task ke SharedPreferences
    private fun saveTaskList(
        sharedPreferences: android.content.SharedPreferences,
        taskList: MutableList<Task>
    ) {
        val gson = Gson()
        val editor = sharedPreferences.edit()
        val json = gson.toJson(taskList)
        editor.putString("task_list", json)
        editor.apply()
    }

    // Fungsi untuk menghapus task dari daftar
    private fun deleteTask(task: Task, sharedPreferences: android.content.SharedPreferences) {
        taskList.remove(task)
        // Cek apakah task berhasil dihapus
        if (!taskList.contains(task)) {
            Toast.makeText(this, "Berhasil menghapus task", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Gagal menghapus task", Toast.LENGTH_SHORT).show()
        }
        saveTaskList(sharedPreferences, taskList)
        taskAdapter.notifyDataSetChanged()
        // Periksa lagi apakah taskList kosong setelah penghapusan
        checkTaskListEmpty()
    }
}