# Scripts de Base de Datos

Base de datos: **`barbex_db`**

## Archivos

| Archivo | Para qué |
|---|---|
| `schema.sql` | Crear la base de datos y todas las tablas |
| `seed-data.sql` | Insertar datos de prueba |

## Cómo aplicar

### Desde MySQL Workbench

1. File → Open SQL Script → seleccionar `schema.sql` → Ejecutar (rayo).
2. Repetir con `seed-data.sql`.

### Desde terminal

```bash
mysql -u root -p < schema.sql
mysql -u root -p barbex_db < seed-data.sql
```

## Notas

- `schema.sql` crea la base de datos `barbex_db` si no existe.
- `seed-data.sql` llena las tablas.
