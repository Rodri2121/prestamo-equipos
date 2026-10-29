-- Datos mínimos para la demostración. Se ejecuta en cada arranque sin duplicar.

-- Cuentas de prueba (contraseñas guardadas como hash BCrypt):
--   admin      / admin123  -> rol ADMIN
--   instructor / user123   -> rol USER
INSERT INTO usuarios (username, password, rol) VALUES
    ('admin',      '$2a$10$PRGzxIBq9LSA3rf5l6MWc.ZOa95G1khWASNVCAEQaVmQx15iuYf/6', 'ADMIN'),
    ('instructor', '$2a$10$J9KI9Pr50lI0GaBWMgJD7.sp73Md.EOeunCpY2JVrrMnUmrPnP5xu', 'USER')
ON CONFLICT (username) DO NOTHING;

-- Inventario inicial: solo se carga si la tabla de equipos está vacía.
INSERT INTO equipos (codigo_patrimonial, nombre, tipo, estado)
SELECT v.codigo, v.nombre, v.tipo, 'DISPONIBLE'
FROM (VALUES
    ('UAM-LAP-001', 'Laptop Lenovo ThinkPad E14',  'PORTATIL'),
    ('UAM-LAP-002', 'Laptop HP ProBook 440',       'PORTATIL'),
    ('UAM-PRY-001', 'Proyector Epson PowerLite',   'PROYECTOR'),
    ('UAM-PRY-002', 'Proyector BenQ MS560',        'PROYECTOR'),
    ('UAM-TAB-001', 'Tableta Samsung Galaxy Tab',  'TABLETA')
) AS v (codigo, nombre, tipo)
WHERE NOT EXISTS (SELECT 1 FROM equipos);
