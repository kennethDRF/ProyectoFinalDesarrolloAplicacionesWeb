# Modelo Relacional — Barbex

Descripción textual de cada tabla con sus columnas, tipos y relaciones.
Base de datos: `barbex_db`

---

## Tabla: `barberias`

| Columna | Tipo | Nulo | PK | FK | Descripción |
|---|---|---|---|---|---|
| id | BIGINT | NO | ✅ | | Llave primaria |
| nombre | VARCHAR(100) | NO | | | Nombre de la barbería |
| direccion | VARCHAR(255) | SÍ | | | Dirección |
| telefono | VARCHAR(20) | SÍ | | | Teléfono |
| codigo_acceso | VARCHAR(20) | SÍ | | | Código único de acceso (UNIQUE) |
| fecha_registro | DATETIME(6) | NO | | | Fecha de creación |

---

## Tabla: `usuarios`

| Columna | Tipo | Nulo | PK | FK | Descripción |
|---|---|---|---|---|---|
| id | BIGINT | NO | ✅ | | Llave primaria |
| barberia_id | BIGINT | NO | | ✅ barberias(id) | Barbería a la que pertenece |
| nombre_completo | VARCHAR(100) | NO | | | Nombre completo |
| email | VARCHAR(100) | NO | | | Correo de acceso (UNIQUE) |
| password | VARCHAR(255) | NO | | | Contraseña |
| telefono | VARCHAR(20) | SÍ | | | Teléfono |
| rol | ENUM('BARBERO','CLIENTE') | NO | | | Rol del usuario |
| activo | BIT | NO | | | Indica si está activo |

---

## Tabla: `servicios`

| Columna | Tipo | Nulo | PK | FK | Descripción |
|---|---|---|---|---|---|
| id | BIGINT | NO | ✅ | | Llave primaria |
| barberia_id | BIGINT | NO | | ✅ barberias(id) | Barbería propietaria |
| nombre | VARCHAR(100) | NO | | | Nombre del servicio |
| descripcion | VARCHAR(500) | SÍ | | | Descripción |
| precio | DECIMAL(10,2) | NO | | | Precio |
| duracion_minutos | INT | NO | | | Duración en minutos |
| monto_adelanto | DECIMAL(10,2) | SÍ | | | Monto de adelanto requerido |
| activo | BIT | NO | | | Indica si está activo |

---

## Tabla: `barbero_servicios`

Tabla asociativa para relación M:N entre barberos y servicios.

| Columna | Tipo | Nulo | PK | FK | Descripción |
|---|---|---|---|---|---|
| id | BIGINT | NO | ✅ | | Llave primaria |
| barbero_id | BIGINT | NO | | ✅ usuarios(id) | Barbero |
| servicio_id | BIGINT | NO | | ✅ servicios(id) | Servicio que ofrece |

---

## Tabla: `citas`

| Columna | Tipo | Nulo | PK | FK | Descripción |
|---|---|---|---|---|---|
| id | BIGINT | NO | ✅ | | Llave primaria |
| barberia_id | BIGINT | NO | | ✅ barberias(id) | Barbería |
| barbero_id | BIGINT | NO | | ✅ usuarios(id) | Barbero asignado |
| cliente_id | BIGINT | NO | | ✅ usuarios(id) | Cliente |
| fecha | DATE | NO | | | Fecha de la cita |
| hora_inicio | TIME(6) | NO | | | Hora inicio |
| hora_fin | TIME(6) | NO | | | Hora fin |
| monto_total | DECIMAL(10,2) | NO | | | Monto total |
| monto_adelanto | DECIMAL(10,2) | SÍ | | | Adelanto requerido |
| adelanto_pagado | BIT | NO | | | Indica si el adelanto fue pagado |
| comprobante_adelanto | VARCHAR(100) | SÍ | | | Ruta del comprobante |
| estado | ENUM('PENDIENTE','CONFIRMADA','COMPLETADA','CANCELADA','RECHAZADA') | NO | | | Estado de la cita |
| motivo_cancelacion | VARCHAR(500) | SÍ | | | Motivo de cancelación |
| fecha_creacion | DATETIME(6) | NO | | | Fecha de creación |

---

## Tabla: `cita_servicios`

Tabla asociativa para relación M:N entre citas y servicios.

| Columna | Tipo | Nulo | PK | FK | Descripción |
|---|---|---|---|---|---|
| id | BIGINT | NO | ✅ | | Llave primaria |
| cita_id | BIGINT | NO | | ✅ citas(id) | Cita |
| servicio_id | BIGINT | NO | | ✅ servicios(id) | Servicio solicitado |

---

## Tabla: `horarios_generales`

| Columna | Tipo | Nulo | PK | FK | Descripción |
|---|---|---|---|---|---|
| id | BIGINT | NO | ✅ | | Llave primaria |
| barbero_id | BIGINT | NO | | ✅ usuarios(id) | Barbero |
| dia_semana | ENUM('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY') | NO | | | Día de la semana |
| hora_inicio | TIME(6) | SÍ | | | Hora de inicio (null si libre) |
| hora_fin | TIME(6) | SÍ | | | Hora de fin (null si libre) |
| libre | BIT | NO | | | true = día libre, false = día laboral |
| activo | BIT | NO | | | Indica si está activo |

---

## Tabla: `excepciones_horario`

| Columna | Tipo | Nulo | PK | FK | Descripción |
|---|---|---|---|---|---|
| id | BIGINT | NO | ✅ | | Llave primaria |
| barbero_id | BIGINT | NO | | ✅ usuarios(id) | Barbero |
| fecha | DATE | NO | | | Fecha de la excepción |
| hora_inicio | TIME(6) | SÍ | | | Hora inicio (si aplica) |
| hora_fin | TIME(6) | SÍ | | | Hora fin (si aplica) |
| tipo | ENUM('NO_TRABAJA','TRABAJA') | NO | | | Tipo de excepción |
| motivo | VARCHAR(255) | SÍ | | | Motivo |

---

## Tabla: `solicitudes_cambio_cita`

| Columna | Tipo | Nulo | PK | FK | Descripción |
|---|---|---|---|---|---|
| id | BIGINT | NO | ✅ | | Llave primaria |
| cita_id | BIGINT | NO | | ✅ citas(id) | Cita asociada |
| tipo | ENUM('CANCELACION','EDICION') | NO | | | Tipo de solicitud |
| nueva_fecha | DATE | SÍ | | | Nueva fecha (si aplica) |
| nueva_hora_inicio | TIME(6) | SÍ | | | Nueva hora inicio (si aplica) |
| estado_solicitud | ENUM('PENDIENTE','APROBADA','RECHAZADA') | NO | | | Estado de la solicitud |
| motivo_rechazo | VARCHAR(500) | SÍ | | | Motivo de rechazo |
| fecha_solicitud | DATETIME(6) | NO | | | Fecha de la solicitud |
| fecha_respuesta | DATETIME(6) | SÍ | | | Fecha de respuesta |

---

## Relaciones

- Una barbería puede tener muchos usuarios.
- Una barbería puede tener muchos servicios.
- Una barbería puede tener muchas citas.
- Un barbero puede ofrecer muchos servicios (M:N mediante `barbero_servicios`).
- Un servicio puede ser ofrecido por muchos barberos (M:N mediante `barbero_servicios`).
- Una cita pertenece a un barbero y a un cliente (ambos son usuarios).
- Una cita puede incluir muchos servicios (M:N mediante `cita_servicios`).
- Un barbero puede tener muchos horarios generales.
- Un barbero puede tener muchas excepciones de horario.
- Una cita puede tener muchas solicitudes de cambio.
