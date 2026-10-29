import axios from 'axios'

const CLAVE_SESION = 'prestamos.sesion'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api',
  timeout: 8000
})

export function leerSesion() {
  try {
    return JSON.parse(localStorage.getItem(CLAVE_SESION))
  } catch {
    return null
  }
}

export function guardarSesion(sesion) {
  localStorage.setItem(CLAVE_SESION, JSON.stringify(sesion))
}

export function borrarSesion() {
  localStorage.removeItem(CLAVE_SESION)
}

// Interceptor: a cada petición le agrega "Authorization: Bearer <token>".
api.interceptors.request.use((config) => {
  const sesion = leerSesion()
  if (sesion?.token) {
    config.headers.Authorization = `Bearer ${sesion.token}`
  }
  return config
})

// Traduce el error de Axios a un mensaje legible para la interfaz.
export function mensajeDeError(error) {
  if (error.response) {
    const { status, data } = error.response
    const detalle = data?.errores?.length ? `: ${data.errores.join(' ')}` : ''
    return `${status} · ${data?.mensaje ?? 'Error del servidor'}${detalle}`
  }
  if (error.code === 'ECONNABORTED') return 'La API tardó demasiado en responder.'
  return 'No se pudo conectar con la API. ¿Está encendido el backend en el puerto 8080?'
}
