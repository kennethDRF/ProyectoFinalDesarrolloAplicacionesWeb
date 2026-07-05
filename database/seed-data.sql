CREATE DATABASE IF NOT EXISTS barbexappdb;

USE barbexappdb;

DROP TABLE IF EXISTS historial_cita;
DROP TABLE IF EXISTS pago;
DROP TABLE IF EXISTS tiempo_bloqueado;
DROP TABLE IF EXISTS horario_trabajo;
DROP TABLE IF EXISTS cita;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS servicio;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS rol;
DROP TABLE IF EXISTS barberia;

CREATE TABLE barberia (
    barberia_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    correo VARCHAR(120),
    direccion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE rol (
    rol_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(150)
);

CREATE TABLE usuario (
    usuario_id INT AUTO_INCREMENT PRIMARY KEY,
    barberia_id INT NOT NULL,
    rol_id INT NOT NULL,
    nombre_completo VARCHAR(120) NOT NULL,
    correo VARCHAR(120) NOT NULL,
    telefono VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY (barberia_id) REFERENCES barberia(barberia_id),
    FOREIGN KEY (rol_id) REFERENCES rol(rol_id)
);

CREATE TABLE cliente (
    cliente_id INT AUTO_INCREMENT PRIMARY KEY,
    barberia_id INT NOT NULL,
    nombre_completo VARCHAR(120) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    correo VARCHAR(120),

    FOREIGN KEY (barberia_id) REFERENCES barberia(barberia_id)
);

CREATE TABLE servicio (
    servicio_id INT AUTO_INCREMENT PRIMARY KEY,
    barberia_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300),
    precio DECIMAL(10,2) NOT NULL,
    duracion_minutos INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY (barberia_id) REFERENCES barberia(barberia_id)
);

CREATE TABLE cita (
    cita_id INT AUTO_INCREMENT PRIMARY KEY,
    barberia_id INT NOT NULL,
    cliente_id INT NOT NULL,
    servicio_id INT NOT NULL,
    barbero_usuario_id INT NOT NULL,
    fecha_cita DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    observaciones VARCHAR(300),

    FOREIGN KEY (barberia_id) REFERENCES barberia(barberia_id),
    FOREIGN KEY (cliente_id) REFERENCES cliente(cliente_id),
    FOREIGN KEY (servicio_id) REFERENCES servicio(servicio_id),
    FOREIGN KEY (barbero_usuario_id) REFERENCES usuario(usuario_id)
);

CREATE TABLE pago (
    pago_id INT AUTO_INCREMENT PRIMARY KEY,
    cita_id INT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    comprobante_url VARCHAR(500),
    estado VARCHAR(40) NOT NULL DEFAULT 'PENDIENTE DE REVISION',
    revisado_por_usuario_id INT,

    FOREIGN KEY (cita_id) REFERENCES cita(cita_id),
    FOREIGN KEY (revisado_por_usuario_id) REFERENCES usuario(usuario_id)
);

CREATE TABLE horario_trabajo (
    horario_id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY (usuario_id) REFERENCES usuario(usuario_id)
);

CREATE TABLE tiempo_bloqueado (
    tiempo_bloqueado_id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    fecha_bloqueo DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    razon VARCHAR(255),

    FOREIGN KEY (usuario_id) REFERENCES usuario(usuario_id)
);

CREATE TABLE historial_cita (
    historial_id INT AUTO_INCREMENT PRIMARY KEY,
    cita_id INT NOT NULL,
    cambiado_por_usuario_id INT,
    estado_anterior VARCHAR(30),
    estado_nuevo VARCHAR(30) NOT NULL,
    comentario VARCHAR(300),

    FOREIGN KEY (cita_id) REFERENCES cita(cita_id),
    FOREIGN KEY (cambiado_por_usuario_id) REFERENCES usuario(usuario_id)
);



