# 🔀 Flujo de Navegación

Diagrama de cómo se conectan las pantallas del sistema.

```text
                           ┌─────────────────┐
                           │    HOME (/)     │
                           │ Página pública  │
                           └────────┬────────┘
                                    │
                     ┌──────────────┼──────────────┐
                     ▼              ▼              ▼
              ┌────────────┐ ┌────────────┐ ┌──────────────┐
              │ /agendar   │ │/servicios  │ │ /login       │
              │ cita       │ │ públicos   │ │ administración│
              └─────┬──────┘ └────────────┘ └──────┬───────┘
                    │                               │
                    ▼                               ▼
           ┌─────────────────┐         ┌────────────────────┐
           │ Formulario      │         │ /dashboard         │
           │ de reserva      │         │ (según rol)        │
           └──────┬──────────┘         └─────────┬──────────┘
                  │                              │
                  ▼                              │
          ┌─────────────────┐                    │
          │ Solicitud       │                    │
          │ registrada      │                    │
          └──────┬──────────┘                    │
                 │                               │
                 ▼                               ▼
          ┌─────────────────┐      ┌──────────┬──────────┬──────────┬──────────┐
          │ Correo de       │      ▼          ▼          ▼          ▼          ▼
          │ confirmación    │ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌──────────┐
          └─────────────────┘ │Calend. │ │ Citas  │ │ Pagos  │ │Servic. │ │Barberos  │
                              └────┬───┘ └────┬───┘ └────┬───┘ └────┬───┘ └────┬─────┘
                                   │          │          │          │          │
                                   ▼          ▼          ▼          ▼          ▼
                              ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
                              │Detalle │ │Detalle │ │Detalle │ │ CRUD   │ │ CRUD   │
                              │del día │ │de cita │ │de pago │ │servicio│ │barbero │
                              └────────┘ └────────┘ └────────┘ └────────┘ └────────┘
```

## Casos de uso principales

### 1. Cliente agenda una cita

```text
HOME →
Agendar Cita →
Formulario de Reserva →
(Selecciona servicio, fecha, hora y datos personales) →
Solicitud Registrada →
Correo de Confirmación
```

### 2. Cliente envía comprobante de pago

```text
Correo de Confirmación →
Cliente responde correo →
Adjunta comprobante →
Administrador revisa pago →
Pago aprobado →
Cita confirmada
```

### 3. Administrador gestiona una cita

```text
HOME →
/login →
/dashboard →
/citas →
/citas/{id} →
(asignar barbero, modificar fecha, cambiar estado) →
Guardar cambios
```

### 4. Administrador revisa pagos

```text
HOME →
/login →
/dashboard →
/pagos →
/pagos/{id} →
(ver comprobante) →
Aprobar o Rechazar pago
```

### 5. Administrador administra servicios

```text
HOME →
/login →
/dashboard →
/servicios →
Crear / Editar / Eliminar servicio →
Guardar cambios
```

### 6. Administrador administra barberos

```text
HOME →
/login →
/dashboard →
/barberos →
Crear / Editar / Desactivar barbero →
Guardar cambios
```

### 7. Administrador consulta agenda del día

```text
HOME →
/login →
/dashboard →
/calendario →
Seleccionar fecha →
Ver citas programadas
```

## Estados de una cita

```text
      ┌─────────────┐
      │ PENDIENTE   │
      │ (se crea)   │
      └──────┬──────┘
             │
             ▼
   ┌────────────────────┐
   │ ESPERANDO PAGO     │
   │ correo enviado     │
   └─────────┬──────────┘
             │
             ▼
      ┌─────────────┐
      │ CONFIRMADA  │
      │ pago válido │
      └──────┬──────┘
             │
             ▼
      ┌─────────────┐
      │ EN PROCESO  │
      │ cita activa │
      └──────┬──────┘
             │
             ▼
      ┌─────────────┐
      │ COMPLETADA  │
      │ servicio    │
      │ realizado   │
      └─────────────┘


      ┌─────────────┐
      │ CANCELADA   │
      │ cliente o   │
      │ administrador│
      └─────────────┘


      ┌─────────────┐
      │ PAGO        │
      │ RECHAZADO   │
      └─────────────┘
```
