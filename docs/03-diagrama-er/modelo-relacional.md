# Modelo Relacional — BarberShop System

Descripción textual de cada tabla con sus columnas, tipos y relaciones.

---

## Tabla: `roles`

| Columna | Tipo        | Nulo | PK | FK | Descripción     |
| ------- | ----------- | ---- | -- | -- | --------------- |
| role_id | INT         | NO   | ✅  |    | Llave primaria  |
| name    | VARCHAR(50) | NO   |    |    | OWNER, EMPLOYEE |

---

## Tabla: `barbershops`

| Columna       | Tipo         | Nulo | PK | FK | Descripción            |
| ------------- | ------------ | ---- | -- | -- | ---------------------- |
| barbershop_id | INT          | NO   | ✅  |    | Llave primaria         |
| name          | VARCHAR(150) | NO   |    |    | Nombre de la barbería  |
| phone         | VARCHAR(30)  | SÍ   |    |    | Teléfono               |
| email         | VARCHAR(150) | SÍ   |    |    | Correo electrónico     |
| address       | VARCHAR(255) | SÍ   |    |    | Dirección              |
| is_active     | BOOLEAN      | NO   |    |    | default true           |
| created_at    | TIMESTAMP    | NO   |    |    | Fecha de creación      |
| updated_at    | TIMESTAMP    | NO   |    |    | Fecha de actualización |

---

## Tabla: `users`

| Columna       | Tipo         | Nulo | PK | FK                           | Descripción                 |
| ------------- | ------------ | ---- | -- | ---------------------------- | --------------------------- |
| user_id       | INT          | NO   | ✅  |                              | Llave primaria              |
| barbershop_id | INT          | NO   |    | ✅ barbershops(barbershop_id) | Barbería a la que pertenece |
| role_id       | INT          | NO   |    | ✅ roles(role_id)             | Rol asignado                |
| full_name     | VARCHAR(150) | NO   |    |                              | Nombre completo             |
| email         | VARCHAR(150) | NO   |    |                              | Correo de acceso            |
| phone         | VARCHAR(30)  | SÍ   |    |                              | Teléfono                    |
| password_hash | VARCHAR(255) | NO   |    |                              | Contraseña encriptada       |
| is_active     | BOOLEAN      | NO   |    |                              | default true                |
| created_at    | TIMESTAMP    | NO   |    |                              | Fecha de creación           |
| updated_at    | TIMESTAMP    | NO   |    |                              | Fecha de actualización      |

---

## Tabla: `services`

| Columna          | Tipo          | Nulo | PK | FK                           | Descripción            |
| ---------------- | ------------- | ---- | -- | ---------------------------- | ---------------------- |
| service_id       | INT           | NO   | ✅  |                              | Llave primaria         |
| barbershop_id    | INT           | NO   |    | ✅ barbershops(barbershop_id) | Barbería propietaria   |
| name             | VARCHAR(100)  | NO   |    |                              | Nombre del servicio    |
| description      | TEXT          | SÍ   |    |                              | Descripción            |
| price            | DECIMAL(10,2) | NO   |    |                              | Precio                 |
| duration_minutes | INT           | NO   |    |                              | Duración del servicio  |
| is_active        | BOOLEAN       | NO   |    |                              | default true           |
| created_at       | TIMESTAMP     | NO   |    |                              | Fecha de creación      |
| updated_at       | TIMESTAMP     | NO   |    |                              | Fecha de actualización |

---

## Tabla: `customers`

| Columna       | Tipo         | Nulo | PK | FK                           | Descripción          |
| ------------- | ------------ | ---- | -- | ---------------------------- | -------------------- |
| customer_id   | INT          | NO   | ✅  |                              | Llave primaria       |
| barbershop_id | INT          | NO   |    | ✅ barbershops(barbershop_id) | Barbería relacionada |
| full_name     | VARCHAR(150) | NO   |    |                              | Nombre completo      |
| phone         | VARCHAR(30)  | NO   |    |                              | Teléfono             |
| email         | VARCHAR(150) | SÍ   |    |                              | Correo electrónico   |
| created_at    | TIMESTAMP    | NO   |    |                              | Fecha de registro    |

---

## Tabla: `appointments`

| Columna            | Tipo      | Nulo | PK | FK                           | Descripción                                       |
| ------------------ | --------- | ---- | -- | ---------------------------- | ------------------------------------------------- |
| appointment_id     | INT       | NO   | ✅  |                              | Llave primaria                                    |
| barbershop_id      | INT       | NO   |    | ✅ barbershops(barbershop_id) | Barbería                                          |
| customer_id        | INT       | NO   |    | ✅ customers(customer_id)     | Cliente                                           |
| service_id         | INT       | NO   |    | ✅ services(service_id)       | Servicio solicitado                               |
| barber_user_id     | INT       | NO   |    | ✅ users(user_id)             | Barbero asignado                                  |
| created_by_user_id | INT       | SÍ   |    | ✅ users(user_id)             | Usuario creador                                   |
| appointment_date   | DATE      | NO   |    |                              | Fecha de cita                                     |
| start_time         | TIME      | NO   |    |                              | Hora inicio                                       |
| end_time           | TIME      | NO   |    |                              | Hora final                                        |
| status             | ENUM      | NO   |    |                              | PENDING, ACCEPTED, REJECTED, CANCELLED, COMPLETED |
| notes              | TEXT      | SÍ   |    |                              | Observaciones                                     |
| created_at         | TIMESTAMP | NO   |    |                              | Fecha creación                                    |
| updated_at         | TIMESTAMP | NO   |    |                              | Fecha actualización                               |

---

## Tabla: `payments`

| Columna             | Tipo          | Nulo | PK | FK                             | Descripción                        |
| ------------------- | ------------- | ---- | -- | ------------------------------ | ---------------------------------- |
| payment_id          | INT           | NO   | ✅  |                                | Llave primaria                     |
| appointment_id      | INT           | NO   |    | ✅ appointments(appointment_id) | Cita relacionada                   |
| amount              | DECIMAL(10,2) | NO   |    |                                | Monto pagado                       |
| payment_method      | ENUM          | NO   |    |                                | SINPE_MOVIL                        |
| receipt_image_url   | VARCHAR(255)  | NO   |    |                                | Ruta de la imagen                  |
| status              | ENUM          | NO   |    |                                | PENDING_REVIEW, APPROVED, REJECTED |
| reviewed_by_user_id | INT           | SÍ   |    | ✅ users(user_id)               | Usuario revisor                    |
| reviewed_at         | DATETIME      | SÍ   |    |                                | Fecha de revisión                  |
| created_at          | TIMESTAMP     | NO   |    |                                | Fecha de creación                  |

---

## Tabla: `work_schedules`

| Columna       | Tipo    | Nulo | PK | FK                           | Descripción    |
| ------------- | ------- | ---- | -- | ---------------------------- | -------------- |
| schedule_id   | INT     | NO   | ✅  |                              | Llave primaria |
| barbershop_id | INT     | NO   |    | ✅ barbershops(barbershop_id) | Barbería       |
| user_id       | INT     | NO   |    | ✅ users(user_id)             | Barbero        |
| day_of_week   | ENUM    | NO   |    |                              | Día laboral    |
| start_time    | TIME    | NO   |    |                              | Hora inicio    |
| end_time      | TIME    | NO   |    |                              | Hora final     |
| is_active     | BOOLEAN | NO   |    |                              | default true   |

---

## Tabla: `blocked_times`

| Columna         | Tipo         | Nulo | PK | FK                           | Descripción        |
| --------------- | ------------ | ---- | -- | ---------------------------- | ------------------ |
| blocked_time_id | INT          | NO   | ✅  |                              | Llave primaria     |
| barbershop_id   | INT          | NO   |    | ✅ barbershops(barbershop_id) | Barbería           |
| user_id         | INT          | NO   |    | ✅ users(user_id)             | Barbero            |
| block_date      | DATE         | NO   |    |                              | Fecha bloqueada    |
| start_time      | TIME         | NO   |    |                              | Hora inicio        |
| end_time        | TIME         | NO   |    |                              | Hora final         |
| reason          | VARCHAR(255) | SÍ   |    |                              | Motivo del bloqueo |
| created_at      | TIMESTAMP    | NO   |    |                              | Fecha de creación  |

---

## Tabla: `appointment_history`

| Columna            | Tipo        | Nulo | PK | FK                             | Descripción                   |
| ------------------ | ----------- | ---- | -- | ------------------------------ | ----------------------------- |
| history_id         | INT         | NO   | ✅  |                                | Llave primaria                |
| appointment_id     | INT         | NO   |    | ✅ appointments(appointment_id) | Cita modificada               |
| changed_by_user_id | INT         | SÍ   |    | ✅ users(user_id)               | Usuario que realizó el cambio |
| old_status         | VARCHAR(50) | SÍ   |    |                                | Estado anterior               |
| new_status         | VARCHAR(50) | SÍ   |    |                                | Estado nuevo                  |
| comment            | TEXT        | SÍ   |    |                                | Comentario                    |
| created_at         | TIMESTAMP   | NO   |    |                                | Fecha del cambio              |

---

# Relaciones

* Una barbería puede tener muchos usuarios.
* Una barbería puede tener muchos servicios.
* Una barbería puede tener muchos clientes.
* Una barbería puede tener muchas citas.
* Un rol puede estar asignado a muchos usuarios.
* Un cliente puede tener muchas citas.
* Un servicio puede ser solicitado en muchas citas.
* Un usuario (barbero) puede atender muchas citas.
* Una cita puede tener un pago asociado.
* Una cita puede registrar múltiples cambios en el historial.
* Un usuario puede tener múltiples horarios laborales.
* Un usuario puede tener múltiples bloqueos de horario.
