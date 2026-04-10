# Database Migrations — Flyway Guide

## Overview

This project uses **Flyway** to manage all database schema changes. Flyway runs automatically every time the backend starts and applies any migration scripts that have not yet been applied to the database.

You never need to run SQL manually against the database. All schema changes go through versioned migration files.

---

## How Flyway Works

1. On startup, Flyway checks the `flyway_schema_history` table in the database.
2. It compares the table against the migration files in `backend/src/main/resources/db/migration/`.
3. Any migration that has not yet been applied is executed in version order.
4. The result is recorded in `flyway_schema_history` so it is never re-applied.

---

## Migration File Naming Convention

All migration files follow this exact pattern:

```
V{version}__{description}.sql
```

| Part | Rule | Example |
|------|------|---------|
| `V` | Uppercase letter V — mandatory prefix | `V` |
| `{version}` | Integer, increments by 1 each time | `4` |
| `__` | **Two** underscores — mandatory separator | `__` |
| `{description}` | Lowercase words separated by underscores | `add_notifications_table` |
| `.sql` | Lowercase `.sql` extension | `.sql` |

**Example filename:** `V4__add_notifications_table.sql`

---

## Location

All migration files live in:

```
backend/src/main/resources/db/migration/
```

---

## Existing Migrations

| File | Description |
|------|-------------|
| `V1__init_schema.sql` | Creates all 10 initial tables (users, cars, repair_shops, etc.) |
| `V2__create_password_reset_tokens.sql` | Adds the `password_reset_tokens` table |
| `V3__seed_admin.sql` | Inserts the initial admin user |

---

## How to Add a New Migration

When you change the data model (add a table, add a column, rename a column, add an index, etc.), do the following:

### 1. Find the next version number

Check which migration files already exist:

```bash
ls backend/src/main/resources/db/migration/
```

If the highest version is `V3`, the next one is `V4`.

### 2. Create the migration file

Create a new `.sql` file following the naming convention. For example, to add a `notifications` table:

```
backend/src/main/resources/db/migration/V4__add_notifications_table.sql
```

Write only the SQL changes needed — do **not** copy the entire schema:

```sql
CREATE TABLE notifications (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message    TEXT    NOT NULL,
    read       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW()
);
```

### 3. Update the JPA entity

Create or update the corresponding `@Entity` class in:

```
backend/src/main/java/com/kapotiabi/entity/
```

### 4. Start the backend

Flyway picks up and applies the new migration automatically on the next startup:

```bash
cd backend
./mvnw spring-boot:run
```

You will see a log line similar to:

```
Migrating schema "public" to version 4 - add notifications table
```

---

## Column Changes

| Change type | How to do it |
|-------------|--------------|
| Add a nullable column | `ALTER TABLE ... ADD COLUMN ...` |
| Add a NOT NULL column | `ALTER TABLE ... ADD COLUMN ... NOT NULL DEFAULT ...` then `ALTER TABLE ... ALTER COLUMN ... DROP DEFAULT` if the default is temporary |
| Rename a column | `ALTER TABLE ... RENAME COLUMN old TO new;` |
| Drop a column | `ALTER TABLE ... DROP COLUMN ...;` |
| Add an index | `CREATE INDEX idx_... ON ...(...);` |

Each of these changes must live in its own new versioned migration file.

---

## Rules — What You Must Never Do

> **Never modify an existing migration file.**

Once a migration has been applied to any database (local, staging, or production) its checksum is recorded by Flyway. If you edit the file, Flyway will detect the checksum mismatch on the next startup and **refuse to start**, throwing:

```
FlywayException: Validate failed: Migration checksum mismatch for migration version N
```

If you need to change something that was already migrated, write a new migration that applies the corrective change.

---

## Tests

Tests use an **in-memory H2 database** with `ddl-auto: create-drop`, so Flyway is **disabled** in the test configuration (`backend/src/test/resources/application.yaml`). You do not need to create migration files for tests — JPA auto-generates the schema from entity classes at test startup.

When you add a new entity, tests will pick it up automatically without any migration file.

---

## Troubleshooting

### Flyway refuses to start (checksum mismatch)

You edited an already-applied migration file. Restore the original content, then create a new migration for the intended change.

### Flyway refuses to start (pending migrations in non-sequential order)

A file was named with a version that was skipped. Rename it to fill the gap, or set `spring.flyway.out-of-order=true` in `application.yaml` (not recommended for production).

### How to reset the local database

Only do this on your **local** machine — never on staging or production:

```bash
# Drop and re-create the database, then restart the backend
psql -U kapotiabi -c "DROP DATABASE kapotiabi;"
psql -U kapotiabi -c "CREATE DATABASE kapotiabi;"
cd backend
./mvnw spring-boot:run   # Flyway re-applies all migrations from V1
```

Or with Docker Compose (`docker compose` / `docker-compose`):

```bash
docker-compose down -v   # -v removes named volumes including postgres_data
docker-compose up
```

---

## Quick Reference

```bash
# List applied migrations (connect to the running DB)
psql -U kapotiabi -d kapotiabi -c "SELECT version, description, installed_on, success FROM flyway_schema_history ORDER BY installed_rank;"

# Run the backend (migrations apply on startup)
cd backend && ./mvnw spring-boot:run

# Run tests (Flyway disabled, H2 in-memory)
cd backend && ./mvnw test
```
