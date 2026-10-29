import { useState } from 'react'
import TablaEquipos from './TablaEquipos'
import TablaPrestamos from './TablaPrestamos'

export default function Panel({ sesion, onCerrarSesion }) {
  const [vista, setVista] = useState('equipos')

  return (
    <>
      <header className="encabezado">
        <div>
          <h1>TecnoUAM · Préstamo de equipos</h1>
          <span className="sub">
            Sesión: <strong>{sesion.username}</strong> <span className={`rol rol-${sesion.rol}`}>{sesion.rol}</span>
          </span>
        </div>
        <button className="secundario" onClick={onCerrarSesion}>Cerrar sesión</button>
      </header>

      <nav className="pestanas">
        <button className={vista === 'equipos' ? 'activa' : ''} onClick={() => setVista('equipos')}>Equipos</button>
        <button className={vista === 'prestamos' ? 'activa' : ''} onClick={() => setVista('prestamos')}>Préstamos</button>
      </nav>

      <main className="contenido">
        {vista === 'equipos'
          ? <TablaEquipos onNoAutorizado={onCerrarSesion} />
          : <TablaPrestamos onNoAutorizado={onCerrarSesion} />}
      </main>
    </>
  )
}
