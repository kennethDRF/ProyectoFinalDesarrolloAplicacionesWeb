# Historias de Usuario — BarberPro Manager

> **Proyecto:** BarberPro Manager — Sistema de Gestión para Barberías
> **Cliente potencial:** Barberías pequeñas y medianas, negocios de cuidado personal masculino y administradores de barberías.
> **Total de historias:** 24

## 🧭 Índice

- [HU-01 a HU-04: Módulo de Usuarios](#módulo-de-usuarios)
- [HU-05 a HU-07: Módulo de Clientes](#módulo-de-clientes)
- [HU-08 a HU-10: Módulo de Servicios](#módulo-de-servicios)
- [HU-11 a HU-13: Módulo de Empleados](#módulo-de-empleados)
- [HU-14 a HU-18: Módulo de Citas](#módulo-de-citas)
- [HU-19: Módulo de Pagos](#módulo-de-pagos)
- [HU-20: Dashboard Administrativo](#dashboard-administrativo)
- [HU-21 a HU-23: Gestión de Bajas Lógicas](#gestión-de-bajas-lógicas)
- [HU-24: API REST](#api-rest)

---

# Módulo de Usuarios

### HU-01 — Registro de usuarios

**Como** usuario nuevo,  
**quiero** registrarme en el sistema,  
**para** poder acceder a sus funcionalidades.

**Criterios de aceptación:**

- El sistema muestra el formulario de registro.
- El sistema valida los campos obligatorios.
- El sistema guarda el usuario correctamente.
- El sistema muestra un mensaje de confirmación.

---

### HU-02 — Inicio de sesión

**Como** usuario registrado,  
**quiero** iniciar sesión,  
**para** acceder al sistema según mi rol.

**Criterios de aceptación:**

- El sistema solicita usuario y contraseña.
- El sistema valida las credenciales ingresadas.
- Si las credenciales son correctas, permite el acceso.
- Si son incorrectas, muestra un mensaje de error.

---

### HU-03 — Cierre de sesión

**Como** usuario autenticado,  
**quiero** cerrar sesión,  
**para** proteger mi información.

**Criterios de aceptación:**

- El sistema muestra la opción de cerrar sesión.
- Al seleccionarla, finaliza la sesión activa.
- El sistema redirige a la pantalla de inicio.
- No permite acceder a páginas protegidas después del cierre.

---

### HU-04 — Gestión de roles

**Como** administrador,  
**quiero** asignar roles a los usuarios,  
**para** controlar los permisos de acceso.

**Criterios de aceptación:**

- El sistema muestra los roles disponibles.
- El administrador puede asignar un rol a un usuario.
- El sistema guarda los cambios realizados.
- Los permisos se actualizan según el rol asignado.

---

# Módulo de Clientes

### HU-05 — Registrar clientes

**Como** recepcionista,  
**quiero** registrar clientes,  
**para** almacenar su información personal.

**Criterios de aceptación:**

- El sistema permite ingresar nombre, teléfono, correo y fecha de nacimiento.
- Todos los campos obligatorios son validados.
- El sistema guarda el cliente correctamente.
- El sistema confirma el registro exitoso.

---

### HU-06 — Consultar clientes

**Como** empleado,  
**quiero** consultar la información de los clientes,  
**para** brindar un mejor servicio.

**Criterios de aceptación:**

- El sistema permite buscar clientes.
- La búsqueda puede realizarse por nombre o teléfono.
- El sistema muestra la información registrada.
- La información se visualiza correctamente.

---

### HU-07 — Actualizar clientes

**Como** recepcionista,  
**quiero** actualizar la información de un cliente,  
**para** mantener los datos actualizados.

**Criterios de aceptación:**

- El sistema permite editar los datos del cliente.
- Los cambios son validados antes de guardar.
- El sistema actualiza la información correctamente.
- Se muestra una confirmación de actualización.

---

# Módulo de Servicios

### HU-08 — Registrar servicios

**Como** administrador,  
**quiero** registrar servicios,  
**para** ofrecer distintas opciones a los clientes.

**Criterios de aceptación:**

- El sistema permite ingresar nombre, descripción, duración y precio.
- Todos los campos obligatorios son validados.
- El servicio se almacena correctamente.
- El sistema muestra una confirmación.

---

### HU-09 — Consultar servicios

**Como** cliente,  
**quiero** visualizar los servicios disponibles,  
**para** elegir el que necesito.

**Criterios de aceptación:**

- El sistema muestra el listado de servicios.
- Se visualiza el precio de cada servicio.
- Se muestra la duración estimada.
- La información se encuentra actualizada.

---

### HU-10 — Actualizar servicios

**Como** administrador,  
**quiero** modificar los datos de un servicio,  
**para** mantener la información correcta.

**Criterios de aceptación:**

- El sistema permite editar los datos del servicio.
- Los cambios son validados.
- La información actualizada se guarda correctamente.
- El sistema muestra una confirmación.

---

# Módulo de Empleados

### HU-11 — Registrar empleados

**Como** administrador,  
**quiero** registrar empleados,  
**para** gestionar al personal de la barbería.

**Criterios de aceptación:**

- El sistema permite ingresar nombre, especialidad y horario.
- Los campos obligatorios son validados.
- La información se almacena correctamente.
- El sistema confirma el registro.

---

### HU-12 — Consultar empleados

**Como** administrador,  
**quiero** consultar la información de los empleados,  
**para** gestionar sus actividades.

**Criterios de aceptación:**

- El sistema muestra el listado de empleados.
- Se visualiza la especialidad de cada empleado.
- Se muestra el horario asignado.
- La información se encuentra actualizada.

---

### HU-13 — Actualizar empleados

**Como** administrador,  
**quiero** actualizar los datos de los empleados,  
**para** mantener información precisa.

**Criterios de aceptación:**

- El sistema permite modificar la información del empleado.
- Los cambios son validados antes de guardar.
- El sistema actualiza los datos correctamente.
- Se muestra una confirmación.

---

# Módulo de Citas

### HU-14 — Agendar cita

**Como** cliente,  
**quiero** agendar una cita,  
**para** recibir un servicio en una fecha y hora específica.

**Criterios de aceptación:**

- El sistema permite seleccionar cliente, servicio y barbero.
- El sistema muestra horarios disponibles.
- La cita se guarda correctamente.
- El sistema muestra una confirmación.

---

### HU-15 — Consultar citas

**Como** empleado,  
**quiero** visualizar las citas programadas,  
**para** organizar mi jornada laboral.

**Criterios de aceptación:**

- El sistema muestra las citas registradas.
- Se visualizan fecha y hora de cada cita.
- Se muestra el cliente asociado.
- Se indica el estado actual de la cita.

---

### HU-16 — Confirmar cita

**Como** empleado,  
**quiero** confirmar una cita,  
**para** informar que será atendida según lo programado.

**Criterios de aceptación:**

- El sistema permite cambiar el estado a "Confirmada".
- El sistema registra la actualización.
- El nuevo estado se refleja inmediatamente.
- El sistema muestra un mensaje de confirmación.

---

### HU-17 — Cancelar cita

**Como** cliente,  
**quiero** cancelar una cita,  
**para** liberar el espacio reservado cuando no pueda asistir.

**Criterios de aceptación:**

- El sistema permite cancelar citas pendientes o confirmadas.
- El estado cambia a "Cancelada".
- El sistema registra la fecha de cancelación.
- Se muestra una confirmación al usuario.

---

### HU-18 — Completar cita

**Como** barbero,  
**quiero** marcar una cita como completada,  
**para** registrar la atención brindada.

**Criterios de aceptación:**

- El sistema permite cambiar el estado a "Completada".
- Solo pueden completarse citas confirmadas.
- El sistema registra el cambio correctamente.
- La cita deja de aparecer como pendiente.

---

# Módulo de Pagos

### HU-19 — Registrar pago

**Como** recepcionista,  
**quiero** registrar el pago de una cita,  
**para** llevar el control financiero de la barbería.

**Criterios de aceptación:**

- El sistema permite asociar el pago a una cita.
- Se registra el monto y método de pago.
- El sistema guarda la fecha del pago.
- Se muestra una confirmación del registro.

---

# Dashboard Administrativo

### HU-20 — Visualizar dashboard administrativo

**Como** administrador,  
**quiero** visualizar un dashboard con indicadores clave,  
**para** monitorear el rendimiento de la barbería.

**Criterios de aceptación:**

- El sistema muestra la cantidad de citas realizadas.
- El sistema muestra los ingresos registrados.
- El sistema muestra las citas pendientes y canceladas.
- La información se actualiza automáticamente.

---

# Gestión de Bajas Lógicas

### HU-21 — Desactivar cliente

**Como** recepcionista,  
**quiero** desactivar clientes que ya no utilizan los servicios de la barbería,  
**para** mantener actualizada la base de datos.

**Criterios de aceptación:**

- El sistema permite desactivar clientes registrados.
- El cliente no se elimina físicamente de la base de datos.
- El estado del cliente cambia a "Inactivo".
- El sistema solicita confirmación antes de realizar la acción.
- El sistema muestra un mensaje de confirmación.

---

### HU-22 — Desactivar servicio

**Como** administrador,  
**quiero** desactivar servicios que ya no se ofrecen,  
**para** evitar que puedan ser seleccionados en nuevas citas.

**Criterios de aceptación:**

- El sistema permite desactivar servicios registrados.
- El servicio no se elimina físicamente de la base de datos.
- El servicio deja de aparecer en el catálogo disponible.
- El sistema solicita confirmación antes de la acción.
- El sistema registra la fecha de desactivación.

---

### HU-23 — Desactivar empleado

**Como** administrador,  
**quiero** desactivar empleados que ya no laboran en la barbería,  
**para** conservar el historial de citas asociadas.

**Criterios de aceptación:**

- El sistema permite desactivar empleados registrados.
- El empleado no se elimina físicamente de la base de datos.
- El historial de citas permanece intacto.
- El empleado deja de estar disponible para nuevas citas.
- El sistema muestra una confirmación de la acción.

---

# API REST

### HU-24 — API REST para consultar servicios

**Como** desarrollador de una aplicación externa,  
**quiero** consultar los servicios disponibles mediante una API REST,  
**para** integrarlos con aplicaciones móviles o sitios web.

**Criterios de aceptación:**

- El sistema expone el endpoint `GET /api/servicios`.
- La respuesta se devuelve en formato JSON.
- La información incluye nombre, descripción, duración y precio.
- Solo se muestran servicios activos.
- El sistema devuelve código HTTP `200 OK` cuando la consulta es exitosa.

---

## ✅ Resumen

- **Total:** 24 historias de usuario.
- **Roles cubiertos:** Administrador, Recepcionista, Barbero, Cliente y Desarrollador externo.
- **Módulos:** Usuarios, Clientes, Servicios, Empleados, Citas, Pagos, Dashboard y API REST.
- **CRUD completos:** Clientes, Servicios y Empleados mediante bajas lógicas.
- **Módulo transaccional:** Gestión completa de citas.
- **Integración:** API REST para consulta de servicios.
- **Objetivo:** Digitalizar y optimizar la administración integral de barberías.