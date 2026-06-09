# 💈 Diagrama Entidad-Relación

Modelo de la base de datos del sistema de gestión de barberías.

## 📄 Archivos en esta carpeta

| Archivo | Contenido |
|----------|------------|
| diagrama-er.png | Diagrama visual del modelo entidad-relación |
| modelo-relacional.md | Descripción textual de tablas y relaciones |

---

## 📊 Resumen del modelo

10 tablas (cumple y supera el mínimo requerido):

| # | Tabla | Tipo |
|---|--------|--------|
| 1 | barbershop | Entidad principal |
| 2 | role | Catálogo |
| 3 | user | Entidad principal |
| 4 | customer | Entidad principal |
| 5 | service | Entidad principal |
| 6 | appointment | Transaccional |
| 7 | payment | Transaccional |
| 8 | work_schedule | Operativa |
| 9 | blocked_time | Operativa |
| 10 | appointment_history | Auditoría |

> En el repositorio se incluye `diagrama-er.png` para la representación visual.

---

## 🔗 Relaciones principales

- `barbershop` 1:N `user`
- `barbershop` 1:N `service`
- `barbershop` 1:N `customer`
- `barbershop` 1:N `appointment`
- `role` 1:N `user`
- `customer` 1:N `appointment`
- `service` 1:N `appointment`
- `user` 1:N `appointment` (barbero asignado)
- `appointment` 1:0..1 `payment`
- `appointment` 1:N `appointment_history`
- `user` 1:N `work_schedule`
- `user` 1:N `blocked_time`

---

## 🏢 Entidad: Barbershop

Representa cada barbería registrada en la plataforma.

### Campos principales

- `barbershop_id` (PK)
- `name`
- `phone`
- `email`
- `address`
- `is_active`

### Relacionada con

- Usuarios
- Servicios
- Clientes
- Citas
- Horarios
- Bloqueos

---

## 👤 Entidad: User

Representa dueños y empleados de una barbería.

### Campos principales

- `user_id` (PK)
- `barbershop_id` (FK)
- `role_id` (FK)
- `full_name`
- `email`
- `phone`
- `password_hash`

### Relacionada con

- Role
- Appointment
- Work Schedule
- Blocked Time
- Appointment History

---

## 🔑 Entidad: Role

Define los permisos del usuario.

### Valores

- OWNER
- EMPLOYEE

---

## ✂️ Entidad: Service

Servicios ofrecidos por la barbería.

### Campos principales

- `service_id` (PK)
- `barbershop_id` (FK)
- `name`
- `description`
- `price`
- `duration_minutes`

### Ejemplos

- Corte
- Barba
- Corte + Barba
- Alisado

---

## 🙋 Entidad: Customer

Clientes que solicitan citas.

> No requieren inicio de sesión.

### Campos principales

- `customer_id` (PK)
- `barbershop_id` (FK)
- `full_name`
- `phone`
- `email`

---

## 📅 Entidad: Appointment

Representa una cita reservada.

### Campos principales

- `appointment_id` (PK)
- `customer_id` (FK)
- `service_id` (FK)
- `barber_user_id` (FK)
- `appointment_date`
- `start_time`
- `end_time`
- `status`

### Estados

- PENDING
- ACCEPTED
- REJECTED
- CANCELLED
- COMPLETED

---

## 💳 Entidad: Payment

Almacena los adelantos realizados por SINPE Móvil.

### Campos principales

- `payment_id` (PK)
- `appointment_id` (FK)
- `amount`
- `receipt_image_url`
- `status`

### Estados

- PENDING_REVIEW
- APPROVED
- REJECTED

---

## 🕒 Entidad: Work Schedule

Define la disponibilidad semanal de cada barbero.

### Campos principales

- `schedule_id` (PK)
- `user_id` (FK)
- `day_of_week`
- `start_time`
- `end_time`

---

## 🚫 Entidad: Blocked Time

Permite bloquear horarios específicos.

### Casos de uso

- Vacaciones
- Permisos
- Almuerzo
- Ausencias

### Campos principales

- `blocked_time_id` (PK)
- `user_id` (FK)
- `block_date`
- `start_time`
- `end_time`
- `reason`

---

## 📝 Entidad: Appointment History

Historial de cambios de una cita.

### Ejemplos

- Cita aceptada
- Cita rechazada
- Cambio de estado
- Modificación manual

### Campos principales

- `history_id` (PK)
- `appointment_id` (FK)
- `changed_by_user_id` (FK)
- `old_status`
- `new_status`
- `comment`

---

## ✅ Cumplimiento de requisitos

- ✅ Más de 8 tablas
- ✅ Relaciones 1:N
- ✅ Tabla transaccional (`appointment`)
- ✅ Tabla derivada de pagos (`payment`)
- ✅ Múltiples llaves foráneas
- ✅ Integridad referencial
- ✅ Soporte multi-barbería
- ✅ Control de roles y permisos
- ✅ Gestión de citas
- ✅ Gestión de horarios
- ✅ Gestión de pagos con comprobante
- ✅ Historial de cambios

---

## 🎯 Objetivo del sistema

El sistema permite administrar una o múltiples barberías de forma independiente, gestionando:

- Usuarios (dueños y empleados)
- Servicios
- Clientes
- Citas
- Horarios
- Pagos mediante SINPE Móvil
- Historial de cambios

Garantizando que cada barbería únicamente pueda acceder a su propia información.