USE barbex_db;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE solicitudes_cambio_cita;
TRUNCATE TABLE cita_servicios;
TRUNCATE TABLE citas;
TRUNCATE TABLE horarios_generales;
TRUNCATE TABLE excepciones_horario;
TRUNCATE TABLE barbero_servicios;
TRUNCATE TABLE servicios;
TRUNCATE TABLE usuarios;
TRUNCATE TABLE barberias;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO barberias (id, nombre, direccion, telefono, codigo_acceso, fecha_registro) VALUES
(1, 'Barberia Centro', 'San Jose, Costa Rica', '2222-1111', 'BAR001', NOW()),
(2, 'Barberia Escazu', 'Escazu, Costa Rica', '2222-2222', 'BAR002', NOW());

INSERT INTO usuarios (id, email, password, nombre_completo, telefono, rol, activo, barberia_id) VALUES
(1, 'barbero1@barbex.com', '123456', 'Carlos Barbero', '8888-1111', 'BARBERO', true, 1);

INSERT INTO usuarios (id, email, password, nombre_completo, telefono, rol, activo, barberia_id) VALUES
(2, 'barbero2@barbex.com', '123456', 'Diego Estilista', '8888-5555', 'BARBERO', true, 2);

INSERT INTO usuarios (id, email, password, nombre_completo, telefono, rol, activo, barberia_id) VALUES
(3, 'cliente1@barbex.com', '123456', 'Ana Lopez', '8888-2222', 'CLIENTE', true, 1),
(4, 'cliente2@barbex.com', '123456', 'Luis Mora', '8888-3333', 'CLIENTE', true, 1),
(5, 'cliente3@barbex.com', '123456', 'Maria Soto', '8888-4444', 'CLIENTE', true, 1);

INSERT INTO usuarios (id, email, password, nombre_completo, telefono, rol, activo, barberia_id) VALUES
(6, 'cliente4@barbex.com', '123456', 'Sofia Rojas', '8888-6666', 'CLIENTE', true, 2),
(7, 'cliente5@barbex.com', '123456', 'Pedro Vega', '8888-7777', 'CLIENTE', true, 2),
(8, 'cliente6@barbex.com', '123456', 'Elena Cruz', '8888-8888', 'CLIENTE', true, 2);

INSERT INTO servicios (id, nombre, descripcion, precio, duracion_minutos, monto_adelanto, activo, barberia_id) VALUES
(1, 'Corte clasico', 'Corte de cabello tradicional con tijera y maquina', 8000.00, 30, 2000.00, true, 1),
(2, 'Corte y barba', 'Corte de cabello mas arreglo de barba', 12000.00, 45, 3000.00, true, 1),
(3, 'Afeitado', 'Afeitado con navaja y toalla caliente', 6000.00, 30, 1500.00, true, 1),
(4, 'Tratamiento capilar', 'Tratamiento hidratante para cabello', 15000.00, 60, 0.00, true, 1),
(5, 'Corte infantil', 'Corte especial para ninos', 5000.00, 30, 0.00, true, 1);

INSERT INTO servicios (id, nombre, descripcion, precio, duracion_minutos, monto_adelanto, activo, barberia_id) VALUES
(6, 'Fade premium', 'Corte fade con acabado premium', 10000.00, 30, 2500.00, true, 2),
(7, 'Rasurado completo', 'Rasurado de cabeza completa con navaja', 8000.00, 45, 2000.00, true, 2),
(8, 'Tinte barba', 'Tinte natural para barba', 20000.00, 60, 0.00, true, 2),
(9, 'Cejas', 'Arreglo de cejas', 4000.00, 15, 0.00, true, 2),
(10, 'Alisado', 'Alisado de cabello masculino', 25000.00, 90, 5000.00, true, 2);

INSERT INTO barbero_servicios (barbero_id, servicio_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
(2, 6), (2, 7), (2, 8), (2, 9), (2, 10);

INSERT INTO horarios_generales (dia_semana, libre, hora_inicio, hora_fin, activo, barbero_id) VALUES
('MONDAY', false, '09:00:00', '19:00:00', true, 1),
('TUESDAY', false, '09:00:00', '19:00:00', true, 1),
('WEDNESDAY', false, '09:00:00', '19:00:00', true, 1),
('THURSDAY', false, '09:00:00', '19:00:00', true, 1),
('FRIDAY', false, '09:00:00', '19:00:00', true, 1),
('SATURDAY', false, '09:00:00', '14:00:00', true, 1),
('SUNDAY', true, NULL, NULL, true, 1),
('MONDAY', false, '09:00:00', '19:00:00', true, 2),
('TUESDAY', false, '09:00:00', '19:00:00', true, 2),
('WEDNESDAY', false, '09:00:00', '19:00:00', true, 2),
('THURSDAY', false, '09:00:00', '19:00:00', true, 2),
('FRIDAY', false, '09:00:00', '19:00:00', true, 2),
('SATURDAY', false, '09:00:00', '14:00:00', true, 2),
('SUNDAY', true, NULL, NULL, true, 2);

ALTER TABLE barberias AUTO_INCREMENT = 3;
ALTER TABLE usuarios AUTO_INCREMENT = 9;
ALTER TABLE servicios AUTO_INCREMENT = 11;
ALTER TABLE barbero_servicios AUTO_INCREMENT = 11;
ALTER TABLE horarios_generales AUTO_INCREMENT = 15;
ALTER TABLE citas AUTO_INCREMENT = 1;
ALTER TABLE cita_servicios AUTO_INCREMENT = 1;
ALTER TABLE solicitudes_cambio_cita AUTO_INCREMENT = 1;
ALTER TABLE excepciones_horario AUTO_INCREMENT = 1;
