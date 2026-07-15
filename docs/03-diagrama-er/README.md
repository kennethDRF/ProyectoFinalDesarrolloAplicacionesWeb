# Diagrama Entidad-Relación

Modelo de la base de datos del sistema de gestión de barberías.

## Archivos en esta carpeta

| Archivo | Contenido |
|---|---|
| diagrama-er.png | Diagrama visual del modelo entidad-relación |
| modelo-relacional.md | Descripción textual de tablas y relaciones |

---

## Resumen del modelo

9 tablas:

| # | Tabla | Tipo |
|---|---|---|
| 1 | barberias | Entidad principal |
| 2 | usuarios | Entidad principal (barberos + clientes) |
| 3 | servicios | Entidad principal |
| 4 | barbero_servicios | Asociativa (M:N) |
| 5 | citas | Transaccional |
| 6 | cita_servicios | Asociativa (M:N) |
| 7 | horarios_generales | Operativa |
| 8 | excepciones_horario | Operativa |
| 9 | solicitudes_cambio_cita | Auditoría / Solicitudes |

---

## Relaciones principales

- `barberias` 1:N `usuarios`
- `barberias` 1:N `servicios`
- `barberias` 1:N `citas`
- `usuarios` N:M `servicios` (a través de `barbero_servicios`)
- `usuarios` 1:N `citas` (como barbero o cliente)
- `citas` N:M `servicios` (a través de `cita_servicios`)
- `usuarios` 1:N `horarios_generales`
- `usuarios` 1:N `excepciones_horario`
- `citas` 1:N `solicitudes_cambio_cita`

---

## Entidad: barberias

Representa cada barbería registrada en la plataforma.

### Campos principales

- `id` (PK, BIGINT AUTO_INCREMENT)
- `nombre` (VARCHAR 100, NOT NULL)
- `direccion` (VARCHAR 255)
- `telefono` (VARCHAR 20)
- `codigo_acceso` (VARCHAR 20, UNIQUE)
- `fecha_registro` (DATETIME, NOT NULL)

---

## Entidad: usuarios

Unifica a todos los usuarios del sistema: barberos (`BARBERO`) y clientes (`CLIENTE`).

### Campos principales

- `id` (PK, BIGINT AUTO_INCREMENT)
- `barberia_id` (FK → barberias.id)
- `nombre_completo` (VARCHAR 100, NOT NULL)
- `email` (VARCHAR 100, NOT NULL, UNIQUE)
- `password` (VARCHAR 255, NOT NULL)
- `telefono` (VARCHAR 20)
- `rol` (ENUM: 'BARBERO', 'CLIENTE')
- `activo` (BIT, NOT NULL)

### Relacionada con

- Barbería propietaria
- Citas como barbero o cliente
- Servicios que ofrece (M:N)
- Horarios
- Excepciones de horario

---

## Entidad: servicios

Servicios ofrecidos por cada barbería.

### Campos principales

- `id` (PK, BIGINT AUTO_INCREMENT)
- `barberia_id` (FK → barberias.id)
- `nombre` (VARCHAR 100, NOT NULL)
- `descripcion` (VARCHAR 500)
- `precio` (DECIMAL 10,2, NOT NULL)
- `duracion_minutos` (INT, NOT NULL)
- `monto_adelanto` (DECIMAL 10,2)
- `activo` (BIT, NOT NULL)

### Asignación

Un servicio puede ser ofrecido por múltiples barberos (M:N mediante `barbero_servicios`).

---

## Entidad: barbero_servicios

Tabla asociativa para la relación M:N entre barberos y servicios.

### Campos

- `id` (PK, BIGINT AUTO_INCREMENT)
- `barbero_id` (FK → usuarios.id)
- `servicio_id` (FK → servicios.id)

---

## Entidad: citas

Representa una cita reservada. Los datos de pago (adelanto) están embebidos.

### Campos principales

- `id` (PK, BIGINT AUTO_INCREMENT)
- `barberia_id` (FK → barberias.id)
- `barbero_id` (FK → usuarios.id)
- `cliente_id` (FK → usuarios.id)
- `fecha` (DATE, NOT NULL)
- `hora_inicio` (TIME, NOT NULL)
- `hora_fin` (TIME, NOT NULL)
- `monto_total` (DECIMAL 10,2, NOT NULL)
- `estado` (ENUM: 'PENDIENTE', 'CONFIRMADA', 'COMPLETADA', 'CANCELADA', 'RECHAZADA')
- `adelanto_pagado` (BIT, NOT NULL)
- `monto_adelanto` (DECIMAL 10,2)
- `comprobante_adelanto` (VARCHAR 100)
- `motivo_cancelacion` (VARCHAR 500)
- `fecha_creacion` (DATETIME, NOT NULL)

### Relacionada con

- Servicios solicitados (M:N mediante `cita_servicios`)
- Solicitudes de cambio/cancelación

---

## Entidad: cita_servicios

Tabla asociativa para la relación M:N entre citas y servicios.

### Campos

- `id` (PK, BIGINT AUTO_INCREMENT)
- `cita_id` (FK → citas.id)
- `servicio_id` (FK → servicios.id)

---

## Entidad: horarios_generales

Define la disponibilidad semanal de cada barbero.

### Campos principales

- `id` (PK, BIGINT AUTO_INCREMENT)
- `barbero_id` (FK → usuarios.id)
- `dia_semana` (ENUM: 'MONDAY'..'SUNDAY')
- `hora_inicio` (TIME)
- `hora_fin` (TIME)
- `libre` (BIT — true = día libre, false = día laboral)
- `activo` (BIT, NOT NULL)

---

## Entidad: excepciones_horario

Permite agregar excepciones puntuales al horario habitual (días no laborales o días Extra trabajados).

### Campos principales

- `id` (PK, BIGINT AUTO_INCREMENT)
- `barbero_id` (FK → usuarios.id)
- `fecha` (DATE, NOT NULL)
- `hora_inicio` (TIME)
- `hora_fin` (TIME)
- `tipo` (ENUM: 'NO_TRABAJA', 'TRABAJA')
- `motivo` (VARCHAR 255)

---

## Entidad: solicitudes_cambio_cita

Gestiona las solicitudes de edición o cancelación de citas hechas por los clientes.

### Campos principales

- `id` (PK, BIGINT AUTO_INCREMENT)
- `cita_id` (FK → citas.id)
- `tipo` (ENUM: 'CANCELACION', 'EDICION')
- `nueva_fecha` (DATE)
- `nueva_hora_inicio` (TIME)
- `estado_solicitud` (ENUM: 'PENDIENTE', 'APROBADA', 'RECHAZADA')
- `motivo_rechazo` (VARCHAR 500)
- `fecha_solicitud` (DATETIME, NOT NULL)
- `fecha_respuesta` (DATETIME)

---

## Cumplimiento de requisitos

- ✅ Más de 8 tablas (9)
- ✅ Relaciones 1:N y M:N
- ✅ Tabla transaccional (`citas`)
- ✅ Múltiples llaves foráneas
- ✅ Integridad referencial
- ✅ Soporte multi-barbería
- ✅ Control de roles (enum)
- ✅ Gestión de citas con estados
- ✅ Gestión de horarios y excepciones
- ✅ Adelantos con comprobante
- ✅ Solicitudes de cambio/cancelación

---

## Objetivo del sistema

El sistema permite administrar una o múltiples barberías de forma independiente, gestionando:

- Barberías con código de acceso único
- Usuarios (barberos y clientes)
- Servicios con precio y adelanto opcional
- Asignación de servicios a barberos
- Citas con múltiples servicios
- Horarios generales y excepciones
- Adelantos vía comprobante
- Solicitudes de edición y cancelación de citas

Garantizando que cada barbería únicamente pueda acceder a su propia información.
