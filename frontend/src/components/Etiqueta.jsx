// Etiqueta de color para DISPONIBLE / PRESTADO / ACTIVO / DEVUELTO.
export default function Etiqueta({ valor }) {
  return <span className={`etiqueta etiqueta-${valor}`}>{valor}</span>
}
