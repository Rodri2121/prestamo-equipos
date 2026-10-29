import { useCallback, useEffect, useState } from 'react'

// Ejecuta una consulta a la API al montar el componente y expone
// los tres estados de la interfaz: cargando, error y datos.
export function useConsulta(consultar, alNoAutorizado) {
  const [datos, setDatos] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  const recargar = useCallback(async () => {
    setCargando(true)
    setError(null)
    try {
      setDatos(await consultar())
    } catch (e) {
      // Token vencido o inválido: se cierra la sesión.
      if (e.response?.status === 401) {
        alNoAutorizado()
        return
      }
      setError(e)
    } finally {
      setCargando(false)
    }
  }, [consultar, alNoAutorizado])

  useEffect(() => {
    recargar()
  }, [recargar])

  return { datos, cargando, error, recargar }
}
