import { useState } from "react"
import TaskManager from "./TaskManager"

function App() {
  const [user] = useState({
    name: "admin",
    role: "admin",
    password: "14141414"
  })

  const [authorization, setAuthorization] = useState(true)
  const [login, setLogin] = useState("")
  const [password, setPassword] = useState("")

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%', marginTop: '50px' }}>
      {
        authorization ?
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '12px', minWidth: '250px' }}>
          <input
            placeholder="Логін"
            value={login}
            onChange={(e) => setLogin(e.target.value)}
            style={{ width: '100%', padding: '4px' }}
          />

          <input
            placeholder="Пароль"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            style={{ width: '100%', padding: '4px' }}
          />

          <button onClick={() => {
            if (login === user.name && password === user.password) {
              setAuthorization(false)
            } else {
              alert("Невірний логін або пароль")
            }
          }}>
            Авторизуватися
          </button>
        </div>
        : <TaskManager />
      }
    </div>
  )
}

export default App