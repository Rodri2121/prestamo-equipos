import { useConsulta } from '../hooks/useConsulta'
import { obtenerEquipos } from '../services/prestamosService'
import EstadoConsulta from './EstadoConsulta'
import Etiqueta from './Etiqueta'

export default function TablaEquipos({ onNoAutorizado }) {
  const { datos: equipos, cargando, error, recargar } = useConsulta(obtenerEquipos, onNoAutorizado)
  const disponibles = equipos.filter((e) => e.estado === 'DISPONIBLE').length

  return (
    <section>
      <div className="barra">
        <h2>Equipos</h2>
        {!cargando && !error && (
          <span className="resumen">
            {equipos.length} en total · {disponibles} disponibles · {equipos.length - disponibles} prestados
          </span>
        )}
        <button onClick={recargar} disabled={cargando}>Recargar</button>
      </div>

      <EstadoConsulta
        cargando={cargando}
        error={error}
        vacio={equipos.length === 0}
        textoVacio="No hay equipos registrados."
        onReintentar={recargar}
      />

      {!cargando && !error && equipos.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>Código</th>
              <th>Nombre</th>
              <th>Tipo</th>
              <th>Estado</th>
            </tr>
          </thead>
          <tbody>
            {equipos.map((e) => (
              <tr key={e.id}>
                <td className="codigo">{e.codigoPatrimonial}</td>
                <td>{e.nombre}</td>
                <td>{e.tipo}</td>
                <td><Etiqueta valor={e.estado} /></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )
}
