import { useState } from 'react'
import Login from './components/Login'
import Panel from './components/Panel'
import { borrarSesion, guardarSesion, leerSesion } from './services/api'

export default function App() {
  const [sesion, setSesion] = useState(leerSesion)

  function iniciarSesion(datos) {
    guardarSesion(datos)
    setSesion(datos)
  }

  function cerrarSesion() {
    borrarSesion()
    setSesion(null)
  }

  return sesion
    ? <Panel sesion={sesion} onCerrarSesion={cerrarSesion} />
    : <Login onLogin={iniciarSesion} />
}
