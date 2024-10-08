package com.example.todolist

import android.annotation.SuppressLint
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), TaskAdapter.OnItemLongClickListener,
    TaskAdapter.OnTaskCompletionListener {
    private val taskList: MutableList<Task> = mutableListOf()
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskDbHelper: TaskDbHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskDbHelper = TaskDbHelper(this)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        taskAdapter = TaskAdapter(taskList)
        taskAdapter.setOnItemLongClickListener(this)
        taskAdapter.setOnTaskCompletionListener(this)

        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        taskList.addAll(taskDbHelper.getAllTasks())
        taskAdapter.notifyDataSetChanged()

        val add: Button = findViewById(R.id.button)
        add.setOnClickListener {
            val taskNameEditText: EditText = findViewById(R.id.editText)

            // Get the text from the EditText fields
            val taskName = taskNameEditText.text.toString()

            // Check if the task name is not empty
            if (taskName.isNotEmpty()) {
                // Create a new task
                val newTask = Task(
                    id = System.currentTimeMillis(), // just for a simple id
                    name = taskName,
                    isCompleted = false
                )
                taskDbHelper.insertTask(newTask);
                // Add the new task to the taskList
                taskList.add(newTask)

                // Notify the adapter that data has changed
                taskAdapter.notifyDataSetChanged()

                // Clear the EditText fields for the next task
                taskNameEditText.text.clear()
            }
        }


    }

    //    When long click, delete the task
    override fun onItemLongClick(position: Int) {
        // Handle the long-click event for the item at 'position'
        // For example, you can show a context menu, delete the task, etc.
        if (position >= 0 && position < taskList.size) {
            var task = taskList.get(position)
            taskDbHelper.removeTask(task.id)
            val text = task.name + " Removed";
            val duration = Toast.LENGTH_SHORT
            Toast.makeText(this, text, duration).show()
            taskList.removeAt(position) // Remove the task from the list
            taskAdapter.notifyItemRemoved(position) // Notify the adapter
        }
    }

    //    Update db when task is marked as complete
    override fun onTaskCompletion(task: Task) {
        taskDbHelper.updateTask(task.id, task.name, task.isCompleted)
    }


}

data class Task(
    val id: Long,
    val name: String,
    var isCompleted: Boolean
)

class TaskAdapter(private val tasks: List<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskNameTextView: TextView = itemView.findViewById(R.id.task_name)
        val taskCheckBox: CheckBox = itemView.findViewById(R.id.task_checkbox)
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.onItemLongClickListener = listener
    }

    private var onItemLongClickListener: OnItemLongClickListener? = null

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(itemView)
    }

    private var onTaskCompletionListener: OnTaskCompletionListener? = null

    fun setOnTaskCompletionListener(listener: OnTaskCompletionListener) {
        this.onTaskCompletionListener = listener
    }

    interface OnTaskCompletionListener {
        fun onTaskCompletion(task: Task)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
        holder.taskNameTextView.text = currentTask.name
        holder.taskCheckBox.isChecked = currentTask.isCompleted
        holder.taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
            currentTask.isCompleted = isChecked
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.onItemLongClick(holder.adapterPosition)
            true // Return true to consume the long-click event
        }
        holder.taskCheckBox.isChecked = currentTask.isCompleted
        holder.taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
            currentTask.isCompleted = isChecked
            onTaskCompletionListener?.onTaskCompletion(currentTask)
        }

    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}

//Time for sqllite
object TaskContract {
    // Define table name and column names
    const val TABLE_NAME = "tasks"
    const val COLUMN_ID = "id"
    const val COLUMN_NAME = "name"
    const val COLUMN_IS_COMPLETED = "is_completed"
}

class TaskDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create the tasks table
        val createTable = """
            CREATE TABLE ${TaskContract.TABLE_NAME} (
                ${TaskContract.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${TaskContract.COLUMN_NAME} TEXT NOT NULL,
                ${TaskContract.COLUMN_IS_COMPLETED} INTEGER DEFAULT 0
            )
        """
        db.execSQL(createTable)
    }

    fun insertTask(task: Task) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(TaskContract.COLUMN_NAME, task.name)
        values.put(TaskContract.COLUMN_IS_COMPLETED, if (task.isCompleted) 1 else 0)
        val newRowId = db.insert(TaskContract.TABLE_NAME, null, values)
        db.close()
    }

    fun updateTask(taskId: Long, newName: String, isCompleted: Boolean) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(TaskContract.COLUMN_NAME, newName)
        values.put(TaskContract.COLUMN_IS_COMPLETED, if (isCompleted) 1 else 0)

        val whereClause = "${TaskContract.COLUMN_ID} = ?"
        val whereArgs = arrayOf(taskId.toString())

        db.update(TaskContract.TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    //This one will
    @SuppressLint("Range")
    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        val cursor = db.query(
            TaskContract.TABLE_NAME,
            null, // Projection (null means all columns)
            null, // Selection (null means all rows)
            null, // SelectionArgs
            null, // GroupBy
            null, // Having
            null  // OrderBy
        )

        cursor.use {
            while (cursor.moveToNext()) {
                val taskId = cursor.getLong(cursor.getColumnIndex(TaskContract.COLUMN_ID))
                val taskName = cursor.getString(cursor.getColumnIndex(TaskContract.COLUMN_NAME))
                val isCompleted =
                    cursor.getInt(cursor.getColumnIndex(TaskContract.COLUMN_IS_COMPLETED)) == 1

                tasks.add(Task(taskId, taskName, isCompleted))
            }
        }

        db.close()
        return tasks
    }

    fun removeTask(taskId: Long) {
        val db = writableDatabase
        val whereClause = "${TaskContract.COLUMN_ID} = ?"
        val whereArgs = arrayOf(taskId.toString())
        db.delete(TaskContract.TABLE_NAME, whereClause, whereArgs)
        db.close()
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // NO DB UPGRADES ALLOWED
    }
}
