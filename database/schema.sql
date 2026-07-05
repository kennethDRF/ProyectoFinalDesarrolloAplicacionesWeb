USE barbexappdb;
INSERT INTO rol (nombre, descripcion) VALUES
('DUEÑO', 'Usuario administrador de la barbería'),
('EMPLEADO', 'Barbero o colaborador de la barbería');

INSERT INTO barberia (barberia_id, nombre, telefono, correo, direccion, activo) VALUES
(1, 'Barbería El Corte Fino', '8888-1111', 'cortefino@gmail.com', 'San José, Costa Rica', TRUE),
(2, 'Barbería Urban Style', '8888-2222', 'urbanstyle@gmail.com', 'Cartago, Costa Rica', TRUE),
(3, 'Barbería Classic Men', '8888-3333', 'classicmen@gmail.com', 'Heredia, Costa Rica', TRUE);

INSERT INTO usuario (usuario_id, barberia_id, rol_id, nombre_completo, correo, telefono, password_hash, activo) VALUES
(1, 1, 1, 'Carlos Ramírez Soto', 'carlos@cortefino.com', '7000-1111', '123456hash', TRUE),
(2, 1, 2, 'Andrés Mora Pérez', 'andres@cortefino.com', '7000-2222', '123456hash', TRUE),
(3, 1, 2, 'Luis Herrera Díaz', 'luis@cortefino.com', '7000-3333', '123456hash', TRUE),

(4, 2, 1, 'Mariana Solano Rojas', 'mariana@urbanstyle.com', '7100-1111', '123456hash', TRUE),
(5, 2, 2, 'Kevin Castro Vega', 'kevin@urbanstyle.com', '7100-2222', '123456hash', TRUE),

(6, 3, 1, 'Roberto Jiménez Arias', 'roberto@classicmen.com', '7200-1111', '123456hash', TRUE),
(7, 3, 2, 'Daniel Gómez Ruiz', 'daniel@classicmen.com', '7200-2222', '123456hash', TRUE);

INSERT INTO cliente (cliente_id, barberia_id, nombre_completo, telefono, correo) VALUES
(1, 1, 'José Pablo Vargas', '8500-1111', 'josevargas@gmail.com'),
(2, 1, 'Miguel Ángel Rojas', '8500-2222', 'miguelrojas@gmail.com'),
(3, 1, 'Sebastián Calderón', '8500-3333', 'sebastianc@gmail.com'),

(4, 2, 'Diego Fernández', '8600-1111', 'diegofernandez@gmail.com'),
(5, 2, 'Esteban Alvarado', '8600-2222', 'estebanalvarado@gmail.com'),

(6, 3, 'Mauricio Salas', '8700-1111', 'mauriciosalas@gmail.com'),
(7, 3, 'Anthony Méndez', '8700-2222', 'anthonymendez@gmail.com');

INSERT INTO servicio (servicio_id, barberia_id, nombre, descripcion, precio, duracion_minutos, activo) VALUES
(1, 1, 'Corte clásico', 'Corte básico para caballero', 5000.00, 30, TRUE),
(2, 1, 'Barba', 'Arreglo y perfilado de barba', 3500.00, 20, TRUE),
(3, 1, 'Corte + Barba', 'Servicio completo de corte y barba', 7500.00, 50, TRUE),

(4, 2, 'Fade', 'Corte moderno estilo fade', 6000.00, 40, TRUE),
(5, 2, 'Diseño de cejas', 'Diseño y limpieza de cejas', 2500.00, 15, TRUE),
(6, 2, 'Corte premium', 'Corte con lavado y acabado', 8500.00, 60, TRUE),

(7, 3, 'Corte tradicional', 'Corte tradicional para caballero', 4500.00, 30, TRUE),
(8, 3, 'Alisado', 'Tratamiento de alisado masculino', 15000.00, 90, TRUE),
(9, 3, 'Barba premium', 'Barba con toalla caliente', 5000.00, 30, TRUE);

INSERT INTO cita (cita_id, barberia_id, cliente_id, servicio_id, barbero_usuario_id, fecha_cita, hora_inicio, hora_fin, estado, observaciones) VALUES
(1, 1, 1, 1, 2, '2026-07-10', '09:00:00', '09:30:00', 'PENDIENTE', 'Cliente solicita corte bajo'),
(2, 1, 2, 3, 3, '2026-07-10', '10:00:00', '10:50:00', 'ACEPTADO', 'Corte completo'),
(3, 1, 3, 2, 2, '2026-07-11', '14:00:00', '14:20:00', 'TERMINADO', 'Solo arreglo de barba'),

(4, 2, 4, 4, 5, '2026-07-12', '11:00:00', '11:40:00', 'PENDIENTE', 'Fade medio'),
(5, 2, 5, 6, 5, '2026-07-12', '15:00:00', '16:00:00', 'ACEPTADO', 'Corte premium'),

(6, 3, 6, 7, 7, '2026-07-13', '08:30:00', '09:00:00', 'PENDIENTE', 'Corte tradicional'),
(7, 3, 7, 8, 7, '2026-07-14', '13:00:00', '14:30:00', 'CANCELADO', 'Cliente canceló'),
(8, 3, 6, 9, 7, '2026-07-15', '16:00:00', '16:30:00', 'ACEPTADO', 'Barba premium');

INSERT INTO pago (pago_id, cita_id, monto, comprobante_url, estado, revisado_por_usuario_id) VALUES
(1, 1, 2500.00, 'https://imagenes.com/comprobante1.jpg', 'PENDIENTE DE REVISION', NULL),
(2, 2, 4000.00, 'https://imagenes.com/comprobante2.jpg', 'APROBADO', 1),
(3, 4, 3000.00, 'https://imagenes.com/comprobante3.jpg', 'PENDIENTE DE REVISION', NULL),
(4, 5, 5000.00, 'https://imagenes.com/comprobante4.jpg', 'APROBADO', 4),
(5, 8, 2500.00, 'https://imagenes.com/comprobante5.jpg', 'RECHAZADO', 6);

INSERT INTO horario_trabajo (horario_id, usuario_id, dia_semana, hora_inicio, hora_fin, activo) VALUES
(1, 2, 'LUNES', '08:00:00', '17:00:00', TRUE),
(2, 2, 'MARTES', '08:00:00', '17:00:00', TRUE),
(3, 3, 'MIERCOLES', '09:00:00', '18:00:00', TRUE),
(4, 3, 'JUEVES', '09:00:00', '18:00:00', TRUE),

(5, 5, 'LUNES', '10:00:00', '19:00:00', TRUE),
(6, 5, 'VIERNES', '10:00:00', '19:00:00', TRUE),

(7, 7, 'MARTES', '08:00:00', '16:00:00', TRUE),
(8, 7, 'SABADO', '09:00:00', '15:00:00', TRUE);

INSERT INTO tiempo_bloqueado (tiempo_bloqueado_id, usuario_id, fecha_bloqueo, hora_inicio, hora_fin, razon) VALUES
(1, 2, '2026-07-10', '12:00:00', '13:00:00', 'Almuerzo'),
(2, 3, '2026-07-11', '15:00:00', '16:00:00', 'Permiso personal'),
(3, 5, '2026-07-12', '13:00:00', '14:00:00', 'Reunión interna'),
(4, 7, '2026-07-14', '10:00:00', '11:00:00', 'Ausencia programada');

INSERT INTO historial_cita (historial_id, cita_id, cambiado_por_usuario_id, estado_anterior, estado_nuevo, comentario) VALUES
(1, 1, 2, NULL, 'PENDIENTE', 'Cita creada por el cliente'),
(2, 2, 3, 'PENDIENTE', 'ACEPTADO', 'Cita aceptada por el barbero'),
(3, 3, 2, 'ACEPTADO', 'TERMINADO', 'Servicio finalizado'),
(4, 4, 5, NULL, 'PENDIENTE', 'Cita registrada'),
(5, 5, 5, 'PENDIENTE', 'ACEPTADO', 'Cita confirmada'),
(6, 7, 7, 'PENDIENTE', 'CANCELADO', 'Cliente solicitó cancelar la cita'),
(7, 8, 7, 'PENDIENTE', 'ACEPTADO', 'Cita aceptada correctamente');