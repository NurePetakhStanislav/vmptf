import { useState } from "react"

function TaskManager(){
  const files = [
    { id: 1, title: "Застелити ліжко", dateTime: "2026-05-18T09:00" },
    { id: 2, title: "Приготувати сніданок", dateTime: "2026-05-18T09:30" }
  ]

  const [tasks, setTasks] = useState(files)
  const [text, setText] = useState("")
  const [dateTime, setDateTime] = useState("")
  const [nextId, setId] = useState(3)

  const [editingId, setEditingId] = useState(null)
  const [editText, setEditText] = useState("")
  const [editDateTime, setEditDateTime] = useState("")

  function addTask() {
    if (!text.trim() || !dateTime) return

    setTasks(prev => [
      ...prev,
      {
        id: nextId,
        title: text,
        dateTime: dateTime
      }
    ])

    setId(nextId => nextId + 1)
    setText("")
    setDateTime("")
  }

  function deleteTask(id) {
    setTasks(tasks.filter(task => task.id !== id))
  }

  function startEdit(task) {
    setEditingId(task.id)
    setEditText(task.title)
    setEditDateTime(task.dateTime)
  }

  function saveEdit(id) {
    setTasks(prev =>
      prev.map(task =>
        task.id === id
          ? {
              ...task,
              title: editText,
              dateTime: editDateTime
            }
          : task
      )
    )

    setEditingId(null)
    setEditText("")
    setEditDateTime("")
  }

  function isDue(taskTime) {
    return new Date(taskTime) <= new Date()
  }

  return (
    <div style={{ display: "flex", flexDirection: "column", alignItems: "center", width: "100%" }}>
      <h1>To-Do List</h1>

      <div style={{ display: "flex", flexDirection: "column", gap: "12px", minWidth: "320px" }}>

        {/* TASK LIST */}
        {tasks.map(task => (
          <div
            key={task.id}
            style={{
              display: "flex",
              flexDirection: "row",
              alignItems: "center",
              gap: "10px",
              width: "100%"
            }}
          >
            {/* ⬇️ ОЦЕ СЮДИ */}
            {task.dateTime && new Date(task.dateTime) <= new Date() && (
              <span>⏰ Час задачі настав!</span>
            )}

            {editingId === task.id ? (
              <>
                <input
                  value={editText}
                  onChange={(e) => setEditText(e.target.value)}
                />

                <input
                  type="datetime-local"
                  value={editDateTime}
                  onChange={(e) => setEditDateTime(e.target.value)}
                />

                <button onClick={() => saveEdit(task.id)}>
                  ✔
                </button>
              </>
            ) : (
              <>
                <span>{task.title}</span>
                <small>{task.dateTime}</small>

                <button onClick={() => startEdit(task)}>
                  Редагувати
                </button>

                <button onClick={() => deleteTask(task.id)}>
                  Видалити
                </button>
              </>
            )}

          </div>
        ))}

        {/* ADD TASK */}
        <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
          <input
            placeholder="Назва задачі"
            value={text}
            onChange={(e) => setText(e.target.value)}
          />

          <input
            type="datetime-local"
            value={dateTime}
            onChange={(e) => setDateTime(e.target.value)}
          />

          <button onClick={addTask}>
            Додати задачу
          </button>
        </div>

      </div>
    </div>
  )
}

export default TaskManager