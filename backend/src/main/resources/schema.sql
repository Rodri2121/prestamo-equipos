-- Esquema de la base prestamos_equipos (PostgreSQL).
-- Se ejecuta en cada arranque; IF NOT EXISTS evita errores si ya existe.

CREATE TABLE IF NOT EXISTS usuarios (
    id        BIGSERIAL PRIMARY KEY,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL,
    rol       VARCHAR(10)  NOT NULL CHECK (rol IN ('USER', 'ADMIN'))
);

CREATE TABLE IF NOT EXISTS equipos (
    id                   BIGSERIAL PRIMARY KEY,
    codigo_patrimonial   VARCHAR(30)  NOT NULL UNIQUE,
    nombre               VARCHAR(100) NOT NULL,
    tipo                 VARCHAR(20)  NOT NULL CHECK (tipo IN ('PORTATIL', 'PROYECTOR', 'TABLETA', 'OTRO')),
    estado               VARCHAR(15)  NOT NULL CHECK (estado IN ('DISPONIBLE', 'PRESTADO'))
);

-- Relación: un equipo tiene muchos préstamos (N:1 desde prestamos).
CREATE TABLE IF NOT EXISTS prestamos (
    id                         BIGSERIAL PRIMARY KEY,
    equipo_id                  BIGINT       NOT NULL REFERENCES equipos (id),
    nombre_solicitante         VARCHAR(100) NOT NULL,
    fecha_prestamo             DATE         NOT NULL,
    fecha_prevista_devolucion  DATE         NOT NULL,
    fecha_devolucion           DATE,
    estado                     VARCHAR(10)  NOT NULL CHECK (estado IN ('ACTIVO', 'DEVUELTO')),
    CHECK (fecha_prevista_devolucion >= fecha_prestamo)
);

-- Garantía a nivel de BD: un equipo no puede tener dos préstamos ACTIVOS a la vez.
CREATE UNIQUE INDEX IF NOT EXISTS ux_prestamo_activo_por_equipo
    ON prestamos (equipo_id) WHERE estado = 'ACTIVO';
