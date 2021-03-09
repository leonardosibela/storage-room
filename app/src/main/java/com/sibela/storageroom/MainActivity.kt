package com.sibela.storageroom

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), TaskAdapter.Callback {

    private var selectedTask: Task? = null
    private lateinit var adapter: TaskAdapter
    private lateinit var taskDao: TaskDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupTaskDatabase()
        setupRecyclerView()
        saveButton.setOnClickListener { onSaveClicked() }
        editButton.setOnClickListener { onEditClicked() }
    }

    override fun onEditClicked(task: Task) {
        displayEditMode()
        taskInput.setText(task.name)
        this.selectedTask = task
    }

    private fun displayEditMode() {
        editButton.visibility = View.VISIBLE
        saveButton.visibility = View.INVISIBLE
        taskInput.requestFocus()
    }

    private fun displaySaveMode() {
        editButton.visibility = View.INVISIBLE
        saveButton.visibility = View.VISIBLE
        taskInput.requestFocus()
    }

    private fun setupTaskDatabase() {
        taskDao = Room
            .databaseBuilder(this, TaskDatabase::class.java, TaskDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build().taskDao()
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(callback = this)
        val tasks = arrayListOf<Task>()
        tasks.addAll(taskDao.getAll())
        adapter.setTaskArray(tasks)
        taskRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun onSaveClicked() {
        val task = Task(name = taskInput.text.toString())
        saveTask(task)
    }

    private fun onEditClicked() {
        val editedTask = Task(selectedTask!!.id, taskInput.text.toString())
        editTask(editedTask)
        selectedTask = null
        displaySaveMode()
    }

    override fun onDeleteClicked(task: Task) {
        taskDao.delete(task)
        val taskArray = arrayListOf<Task>()
        val tasks = taskDao.getAll()
        taskArray.addAll(tasks)
        adapter.setTaskArray(taskArray)
    }

    private fun saveTask(task: Task) {
        taskDao.insert(task)
        val tasks = arrayListOf<Task>()
        tasks.addAll(taskDao.getAll())
        adapter.setTaskArray(tasks)
        taskInput.setText("")
    }

    private fun editTask(task: Task) {
        taskDao.update(task)
        val tasks = arrayListOf<Task>()
        tasks.addAll(taskDao.getAll())
        adapter.setTaskArray(tasks)
        taskInput.setText("")
    }
}