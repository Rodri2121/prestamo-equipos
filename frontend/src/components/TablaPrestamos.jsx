import { useConsulta } from '../hooks/useConsulta'
import { obtenerPrestamos } from '../services/prestamosService'
import EstadoConsulta from './EstadoConsulta'
import Etiqueta from './Etiqueta'

export default function TablaPrestamos({ onNoAutorizado }) {
  const { datos: prestamos, cargando, error, recargar } = useConsulta(obtenerPrestamos, onNoAutorizado)

  return (
    <section>
      <div className="barra">
        <h2>Préstamos</h2>
        <button onClick={recargar} disabled={cargando}>Recargar</button>
      </div>

      <EstadoConsulta
        cargando={cargando}
        error={error}
        vacio={prestamos.length === 0}
        textoVacio="Todavía no se ha registrado ningún préstamo."
        onReintentar={recargar}
      />

      {!cargando && !error && prestamos.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Equipo</th>
              <th>Solicitante</th>
              <th>Préstamo</th>
              <th>Devolución prevista</th>
              <th>Devuelto el</th>
              <th>Estado</th>
            </tr>
          </thead>
          <tbody>
            {prestamos.map((p) => (
              <tr key={p.id}>
                <td>{p.id}</td>
                <td><span className="codigo">{p.equipo.codigoPatrimonial}</span> · {p.equipo.nombre}</td>
                <td>{p.nombreSolicitante}</td>
                <td>{p.fechaPrestamo}</td>
                <td>{p.fechaPrevistaDevolucion}</td>
                <td>{p.fechaDevolucion ?? '—'}</td>
                <td><Etiqueta valor={p.estado} /></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )
}
