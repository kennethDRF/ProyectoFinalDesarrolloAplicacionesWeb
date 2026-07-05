# 🗃️ Scripts de Base de Datos

## 📄 Archivos

| Archivo | Para qué |
|---|---|
| `schema.sql` | Crear todas las tablas (DDL) |
| `seed-data.sql` | Insertar datos de prueba |

## Cómo aplicar

### Desde MySQL Workbench

1. File → Open SQL Script → seleccionar `schema.sql` → Ejecutar (rayo).
2. Repetir con `seed-data.sql`.

### Desde terminal

```bash
mysql -u root -p < schema.sql
mysql -u root -p vetcaredb < seed-data.sql
```

## Hibernate vs `schema.sql`

Hibernate también crea las tablas a partir de las `@Entity` con `ddl-auto=update`. El `schema.sql` es la **referencia oficial** del esquema y es lo que se aplica en producción.