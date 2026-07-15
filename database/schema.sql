-- Estructura de la base de datos Barbex ejecutar antes de seed-datos.sql

DROP DATABASE IF EXISTS barbex_db;
CREATE DATABASE barbex_db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE barbex_db;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `barberias`;

CREATE TABLE `barberias` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `codigo_acceso` varchar(20) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `fecha_registro` datetime(6) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKd15had9up1m415xlbnywseh79` (`codigo_acceso`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `usuarios`;

CREATE TABLE `usuarios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `email` varchar(100) NOT NULL,
  `nombre_completo` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `rol` enum('BARBERO','CLIENTE') NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `barberia_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkfsp0s1tflm1cwlj8idhqsad0` (`email`),
  KEY `FK462ius3asmam1b9xfj1he04fn` (`barberia_id`),
  CONSTRAINT `FK462ius3asmam1b9xfj1he04fn` FOREIGN KEY (`barberia_id`) REFERENCES `barberias` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `servicios`;

CREATE TABLE `servicios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `descripcion` varchar(500) DEFAULT NULL,
  `duracion_minutos` int NOT NULL,
  `monto_adelanto` decimal(10,2) DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `barberia_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3muqnkhkfdlkgdmvfbt88ydlj` (`barberia_id`),
  CONSTRAINT `FK3muqnkhkfdlkgdmvfbt88ydlj` FOREIGN KEY (`barberia_id`) REFERENCES `barberias` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `barbero_servicios`;

CREATE TABLE `barbero_servicios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `barbero_id` bigint NOT NULL,
  `servicio_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKns9fdvcv9xdlweomv3ev1r6wu` (`barbero_id`),
  KEY `FKmd50wbficx5lv7fyj71tmmr0y` (`servicio_id`),
  CONSTRAINT `FKmd50wbficx5lv7fyj71tmmr0y` FOREIGN KEY (`servicio_id`) REFERENCES `servicios` (`id`),
  CONSTRAINT `FKns9fdvcv9xdlweomv3ev1r6wu` FOREIGN KEY (`barbero_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `citas`;

CREATE TABLE `citas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `adelanto_pagado` bit(1) NOT NULL,
  `comprobante_adelanto` varchar(100) DEFAULT NULL,
  `estado` enum('CANCELADA','COMPLETADA','CONFIRMADA','PENDIENTE','RECHAZADA') NOT NULL,
  `fecha` date NOT NULL,
  `fecha_creacion` datetime(6) NOT NULL,
  `hora_fin` time(6) NOT NULL,
  `hora_inicio` time(6) NOT NULL,
  `monto_adelanto` decimal(10,2) DEFAULT NULL,
  `monto_total` decimal(10,2) NOT NULL,
  `motivo_cancelacion` varchar(500) DEFAULT NULL,
  `barberia_id` bigint NOT NULL,
  `barbero_id` bigint NOT NULL,
  `cliente_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK33xxvwvo497ou8vultr351mvg` (`barberia_id`),
  KEY `FKpuiuwn6ymwdm1tw8lcy0rbyrd` (`barbero_id`),
  KEY `FKcitrr49x1d6tcxpajkj7yj1mm` (`cliente_id`),
  CONSTRAINT `FK33xxvwvo497ou8vultr351mvg` FOREIGN KEY (`barberia_id`) REFERENCES `barberias` (`id`),
  CONSTRAINT `FKcitrr49x1d6tcxpajkj7yj1mm` FOREIGN KEY (`cliente_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `FKpuiuwn6ymwdm1tw8lcy0rbyrd` FOREIGN KEY (`barbero_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `cita_servicios`;

CREATE TABLE `cita_servicios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cita_id` bigint NOT NULL,
  `servicio_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1bopji12796dyd9egdylg828i` (`cita_id`),
  KEY `FK5lpsx6uey11rebxy3ghf7m9os` (`servicio_id`),
  CONSTRAINT `FK1bopji12796dyd9egdylg828i` FOREIGN KEY (`cita_id`) REFERENCES `citas` (`id`),
  CONSTRAINT `FK5lpsx6uey11rebxy3ghf7m9os` FOREIGN KEY (`servicio_id`) REFERENCES `servicios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `horarios_generales`;

CREATE TABLE `horarios_generales` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activo` bit(1) NOT NULL,
  `dia_semana` enum('FRIDAY','MONDAY','SATURDAY','SUNDAY','THURSDAY','TUESDAY','WEDNESDAY') NOT NULL,
  `hora_fin` time(6) DEFAULT NULL,
  `hora_inicio` time(6) DEFAULT NULL,
  `libre` bit(1) NOT NULL,
  `barbero_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKo9uuh018ojl8yoxe2m5krwo5k` (`barbero_id`),
  CONSTRAINT `FKo9uuh018ojl8yoxe2m5krwo5k` FOREIGN KEY (`barbero_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `excepciones_horario`;

CREATE TABLE `excepciones_horario` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `hora_fin` time(6) DEFAULT NULL,
  `hora_inicio` time(6) DEFAULT NULL,
  `motivo` varchar(255) DEFAULT NULL,
  `tipo` enum('NO_TRABAJA','TRABAJA') NOT NULL,
  `barbero_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjptboxx7tt6ehn5ii4fokde92` (`barbero_id`),
  CONSTRAINT `FKjptboxx7tt6ehn5ii4fokde92` FOREIGN KEY (`barbero_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `solicitudes_cambio_cita`;

CREATE TABLE `solicitudes_cambio_cita` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `estado_solicitud` enum('APROBADA','PENDIENTE','RECHAZADA') NOT NULL,
  `fecha_respuesta` datetime(6) DEFAULT NULL,
  `fecha_solicitud` datetime(6) NOT NULL,
  `motivo_rechazo` varchar(500) DEFAULT NULL,
  `nueva_fecha` date DEFAULT NULL,
  `nueva_hora_inicio` time(6) DEFAULT NULL,
  `tipo` enum('CANCELACION','EDICION') NOT NULL,
  `cita_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4y44w2l26it1n1cgp6bvenmlh` (`cita_id`),
  CONSTRAINT `FK4y44w2l26it1n1cgp6bvenmlh` FOREIGN KEY (`cita_id`) REFERENCES `citas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
