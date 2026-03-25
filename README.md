# Inventario App Backend - Spring Boot

REST API backend for an inventory management system built with Spring Boot 3.

## Tech Stack

- **Java 17** + **Spring Boot 3.1.5**
- **Spring Security 6** with JWT authentication
- **Spring Data JPA** + **H2 / PostgreSQL**
- **Lombok** for boilerplate reduction
- **Bean Validation** for request validation

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL database only if you want to run with `postgres` or `prod`

### Profiles

- **Default / local**: uses **H2 en memoria** so the app can start without PostgreSQL
- **dev**: same local flow with more verbose logs
- **postgres**: uses **PostgreSQL** for local development against a real database
- **prod**: uses **PostgreSQL** for deployment/production with required environment variables
- **test**: uses **H2** for tests

### Configuration

#### Local default
No database variables are required. The application starts with H2 in memory and enables the console at `/h2-console`.

#### Local with PostgreSQL
Use profile `postgres` and set these variables:

| Variable | Example | Description |
|---|---|---|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/inventario_db` | PostgreSQL URL |
| `DATABASE_USERNAME` | `postgres` | DB username |
| `DATABASE_PASSWORD` | `tu_password` | DB password |
| `JPA_DDL_AUTO` | `update` | Hibernate schema mode for local PostgreSQL |
| `JWT_SECRET` | (default key) | JWT signing secret (min 64 chars) |
| `PORT` | `8080` | Server port |
| `CORS_ORIGINS` | `http://localhost:3000,...` | Allowed CORS origins |

#### Supabase example
Known values from your project:

- **Host**: `aws-1-us-east-1.pooler.supabase.com`
- **Database**: `postgres`
- **Project ID**: `mnwzoaqjvvovuziqsqwd`
- **Recommended username format** for Supabase pooler: `postgres.mnwzoaqjvvovuziqsqwd`
- **Recommended JDBC URL**: `jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require`

If Supabase gave you a different port or user in the dashboard, use the exact values shown there.

#### Production / PostgreSQL
Use profile `prod` and define at least:

| Variable | Description |
|---|---|
| `DATABASE_URL` | PostgreSQL URL |
| `DATABASE_USERNAME` | DB username |
| `DATABASE_PASSWORD` | DB password |
| `JWT_SECRET` | JWT signing secret |

### Run

#### Local with H2
```bash
mvn spring-boot:run
```

#### Dev profile
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Local with PostgreSQL
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

#### Production profile with PostgreSQL
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### IntelliJ IDEA
For a local run against PostgreSQL:

1. Open **Run/Debug Configurations**.
2. Select your Spring Boot configuration.
3. Set **Active profiles** to `postgres`.
4. Add environment variables:
   - `DATABASE_URL=jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require`
   - `DATABASE_USERNAME=postgres.mnwzoaqjvvovuziqsqwd`
   - `DATABASE_PASSWORD=tu_password_real_de_supabase`
   - `JPA_DDL_AUTO=update`
5. Run the application.

## API Endpoints

### Auth
| Method | URL | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/auth/login` | Login, returns JWT | Public |
| GET | `/api/v1/auth/me` | Current user info | Required |

### Resources (all follow the same pattern)

All resource endpoints support `?incluir_inactivos=false` query param on GET list.

| Method | URL | Description |
|---|---|---|
| GET | `/api/v1/{resource}` | List active records |
| GET | `/api/v1/{resource}/{id}` | Get by ID |
| POST | `/api/v1/{resource}` | Create |
| PUT | `/api/v1/{resource}/{id}` | Update |
| DELETE | `/api/v1/{resource}/{id}` | Soft delete (estado='I') |

Resources: `usuarios`, `clientes`, `proveedores`, `categorias`, `marcas`, `tipos-producto`, `productos`, `presentaciones`, `compras`, `ventas`

### Utility
| Method | URL | Description | Auth |
|---|---|---|---|
| GET | `/health` | Health check | Public |
| GET | `/` | API info | Public |

## Authentication

Include JWT in the `Authorization` header:
```
Authorization: Bearer <token>
```

## Domain Model

- **TipoUsuario** → **Usuario** (employees/admins)
- **Categoria** / **Marca** / **TipoProducto** → **Producto** (catalog)
- **Producto** → **Presentacion** (packaging variants)
- **Proveedor** + **Usuario** + **EstadoPago** → **Compra** → **DetalleCompra**
- **Cliente** + **Usuario** + **EstadoPago** → **Venta** → **DetalleVenta**

All entities use `estado` field ('A' = active, 'I' = inactive) for soft deletes.
