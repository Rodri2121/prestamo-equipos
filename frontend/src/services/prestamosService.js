import { api } from './api'

export async function login(username, password) {
  const { data } = await api.post('/auth/login', { username, password })
  return data
}

export async function obtenerEquipos() {
  const { data } = await api.get('/equipos')
  return data
}

export async function obtenerPrestamos() {
  const { data } = await api.get('/prestamos')
  return data
}
