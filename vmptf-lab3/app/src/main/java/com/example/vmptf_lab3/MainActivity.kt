package com.example.vmptf_lab3

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.get
import kotlin.inc

data class Task(
    var id: Int,
    var title: String,
    var time: String,
    var isCompleted: Boolean = false
)

class MainActivity : AppCompatActivity() {
    private val taskList = mutableListOf<Task>()
    private lateinit var taskAdapter: TaskAdapter
    private var nextTaskId = 3
    private var isAdmin: Boolean = true

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "todo_channel",
                "Todo Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        val rvTasks = findViewById<RecyclerView>(R.id.rvTasks)
        val btnAddTask = findViewById<Button>(R.id.btnAddTask)
        val swRole = findViewById<Switch>(R.id.swRole)

        fun scheduleNotification(taskTitle: String, delaySeconds: Long) {
            val intent = Intent(this, ReminderReceiver::class.java).apply {
                putExtra("title", taskTitle)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                this, nextTaskId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val triggerTime = System.currentTimeMillis() + (delaySeconds * 1000)

            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            Toast.makeText(this, "Нагадування з'явиться через $delaySeconds сек!", Toast.LENGTH_SHORT).show()
        }

        fun showTaskDialog(position: Int?) {
            val dialogLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(40, 20, 40, 20)
            }

            val etTitle = EditText(this).apply {
                hint = "Що потрібно зробити?"
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                if (position != null) setText(taskList[position].title)
            }

            val etTime = EditText(this).apply {
                hint = "Через скільки секунд нагадати?"
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = 20 }
                if (position != null) setText(taskList[position].time)
            }

            dialogLayout.addView(etTitle)
            dialogLayout.addView(etTime)

            val dialogTitle = if (position == null) "Нове завдання" else "Редагувати завдання"

            AlertDialog.Builder(this)
                .setTitle(dialogTitle)
                .setView(dialogLayout)
                .setPositiveButton("Підтвердити") { dialog, _ ->
                    val titleText = etTitle.text.toString().trim()
                    val timeText = etTime.text.toString().trim()

                    if (titleText.isNotEmpty() && timeText.isNotEmpty()) {
                        if (position == null) {
                            taskList.add(Task(nextTaskId, titleText, timeText))
                            val lastPosition = taskList.size - 1
                            taskAdapter.notifyItemInserted(lastPosition)
                            findViewById<RecyclerView>(R.id.rvTasks).scrollToPosition(lastPosition)

                            val delaySeconds = timeText.toLongOrNull() ?: 60L
                            scheduleNotification(titleText, delaySeconds)
                            nextTaskId++
                        } else {
                            // Режим редактирования
                            taskList[position].title = titleText
                            taskList[position].time = timeText
                            taskAdapter.notifyItemChanged(position)
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Скасувати") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }

        taskList.add(Task(1, "Здати лабораторну №3", "Сьогодні, 16:30"))
        taskList.add(Task(2, "Показати Юлічці вайбкодинг", "Сьогодні, 18:00", true))

        rvTasks.layoutManager = LinearLayoutManager(this)

        taskAdapter = TaskAdapter(
            taskList,
            isAdmin,
            onDeleteClick = { position ->
                taskList.removeAt(position)
                taskAdapter.notifyItemRemoved(position)
                Toast.makeText(this, "Завдання видалено", Toast.LENGTH_SHORT).show()
            },
            onCheckedChange = { position, isChecked ->
                taskList[position].isCompleted = isChecked
                taskAdapter.notifyItemChanged(position)
            },
            onEditClick = { position ->
                showTaskDialog(position)
            }
        )
        rvTasks.adapter = taskAdapter

        swRole.setOnCheckedChangeListener { _, isChecked ->
            isAdmin = isChecked
            swRole.text = if (isAdmin) "Роль: Адміністратор (Повний доступ)" else "Роль: Студент (Тільки читання)"

            taskAdapter.updateRole(isAdmin)
        }

        btnAddTask.setOnClickListener {
            if (isAdmin) {
                showTaskDialog(null)
            } else {
                Toast.makeText(this, "Помилка доступу: Тільки Адміністратор може додавати!", Toast.LENGTH_SHORT).show()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }
}

class TaskAdapter(
    private val tasks: List<Task>,
    private var isAdmin: Boolean,
    private val onDeleteClick: (Int) -> Unit,
    private val onCheckedChange: (Int, Boolean) -> Unit,
    private val onEditClick: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbTaskStatus: CheckBox = view.findViewById(R.id.cbTaskStatus)
        val tvTaskTitle: TextView = view.findViewById(R.id.tvTaskTitle)
        val tvTaskTime: TextView = view.findViewById(R.id.tvTaskTime)
        val btnEditTask: ImageButton = view.findViewById(R.id.btnEditTask)
        val btnDeleteTask: ImageButton = view.findViewById(R.id.btnDeleteTask)
    }

    fun updateRole(newAdminStatus: Boolean) {
        this.isAdmin = newAdminStatus
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.cbTaskStatus.setOnCheckedChangeListener(null)
        holder.tvTaskTitle.text = task.title
        holder.tvTaskTime.text = "Таймер: ${task.time} сек"
        holder.cbTaskStatus.isChecked = task.isCompleted

        if (task.isCompleted) {
            holder.tvTaskTitle.paintFlags = holder.tvTaskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.tvTaskTitle.paintFlags = holder.tvTaskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        if (isAdmin) {
            holder.btnEditTask.alpha = 1.0f // Яркая кнопка
            holder.btnDeleteTask.alpha = 1.0f
            holder.btnEditTask.isEnabled = true // Кнопка кликабельна
            holder.btnDeleteTask.isEnabled = true
        } else {
            holder.btnEditTask.alpha = 0.3f // Бледная кнопка (визуальный блок)
            holder.btnDeleteTask.alpha = 0.3f
            holder.btnEditTask.isEnabled = false // Клик полностью отключен
            holder.btnDeleteTask.isEnabled = false
        }

        holder.cbTaskStatus.setOnCheckedChangeListener { _, isChecked -> onCheckedChange(holder.adapterPosition, isChecked) }
        holder.btnEditTask.setOnClickListener { onEditClick(holder.adapterPosition) }
        holder.btnDeleteTask.setOnClickListener { onDeleteClick(holder.adapterPosition) }
    }

    override fun getItemCount(): Int = tasks.size
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Завдання!"

        // Створюємо будівельник сповіщень. Для нових пристроїв додаємо ID каналу "todo_channel"
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, "todo_channel")
        } else {
            NotificationCompat.Builder(context)
        }

        val notification = builder
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Нагадування про задачу")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}