import { mensajeDeError } from '../services/api'

// Muestra el estado de carga, error o lista vacía. Devuelve null si hay datos.
export default function EstadoConsulta({ cargando, error, vacio, textoVacio, onReintentar }) {
  if (cargando) {
    return <p className="alerta cargando">Cargando datos desde la API…</p>
  }
  if (error) {
    return (
      <div className="alerta error">
        <p>{mensajeDeError(error)}</p>
        <button onClick={onReintentar}>Reintentar</button>
      </div>
    )
  }
  if (vacio) {
    return <p className="alerta vacio">{textoVacio}</p>
  }
  return null
}
