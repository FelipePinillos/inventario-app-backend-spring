# Solución del Error ExceptionInInitializerError

## Problema
Error: `java.lang.ExceptionInInitializerError com.sun.tools.javac.code.TypeTag :: UNKNOWN`

Este error ocurre debido a una incompatibilidad entre Lombok y la versión de Java/compilador.

## Solución Aplicada

Se han realizado las siguientes actualizaciones en `pom.xml`:

1. **Agregada versión específica de Lombok (1.18.30)** en las properties
2. **Configurado el annotation processor** en maven-compiler-plugin
3. **Especificada versión explícita** de Lombok en las dependencias

## Pasos para Resolver en IntelliJ IDEA

### Opción 1: Recargar proyecto Maven (Recomendado)
1. En IntelliJ IDEA, haz clic derecho en el archivo `pom.xml`
2. Selecciona **Maven** → **Reload project**
3. Espera a que IntelliJ descargue las dependencias actualizadas

### Opción 2: Desde el panel Maven
1. Abre el panel **Maven** (View → Tool Windows → Maven)
2. Haz clic en el ícono de **Reload All Maven Projects** (circular con flechas)

### Opción 3: Invalidar caché
Si las opciones anteriores no funcionan:
1. Ve a **File** → **Invalidate Caches...**
2. Marca **Clear file system cache and Local History**
3. Marca **Clear downloaded shared indexes**
4. Haz clic en **Invalidate and Restart**

### Opción 4: Limpiar y recompilar
1. Ve a **Build** → **Clean Project**
2. Luego **Build** → **Rebuild Project**

## Verificación

Después de aplicar cualquiera de las opciones, el proyecto debería compilar sin errores.

## Cambios Realizados en pom.xml

```xml
<properties>
    ...
    <lombok.version>1.18.30</lombok.version>
</properties>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>${lombok.version}</version>
    <optional>true</optional>
</dependency>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <source>17</source>
        <target>17</target>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

# Solución de errores de compilación y arranque

## 1. Error de compilación con `TypeTag :: UNKNOWN`

### Problema
`java.lang.ExceptionInInitializerError com.sun.tools.javac.code.TypeTag :: UNKNOWN`

### Causa
IntelliJ estaba compilando con JDK 25 mientras el proyecto está configurado para Java 17 y usa Lombok.

### Solución aplicada
- El SDK del proyecto en `.idea/misc.xml` quedó alineado a **Java 17**.
- `pom.xml` mantiene Java 17 y Lombok explícito.

## 2. Error de arranque con PostgreSQL

### Problema
`FATAL: la autenticación password falló para el usuario 'postgres'`

### Causa
La configuración base usaba PostgreSQL por defecto para cualquier arranque local.

### Solución aplicada
- `application.yml` ahora usa **H2 en memoria** por defecto.
- `application-prod.yml` conserva **PostgreSQL** para producción.

## 3. Error de arranque con H2

### Problema
`Cannot load driver class: org.h2.Driver`

### Causa probable
IntelliJ está ejecutando con un classpath Maven viejo y todavía no tomó la dependencia `com.h2database:h2` agregada en `pom.xml`.

### Solución aplicada en el repo
- `pom.xml` incluye `com.h2database:h2` en `runtime`.
- `application.yml` ya no fuerza `driver-class-name`, para que Spring autodetecte el driver.

## Pasos exactos en IntelliJ IDEA

### Paso 1: Recargar Maven
1. Abrir el panel **Maven**.
2. Pulsar **Reload All Maven Projects**.

### Paso 2: Borrar salida vieja
1. Cerrar la app si está corriendo.
2. Eliminar la carpeta `target` del proyecto.
3. Hacer **Build > Rebuild Project**.

### Paso 3: Verificar el SDK
1. Ir a **File > Project Structure > Project**.
2. Confirmar:
   - **Project SDK = 17**
   - **Project language level = 17**

### Paso 4: Revisar la configuración de ejecución
Si ejecutas desde una configuración antigua de tipo `Application`, elimínala y crea una nueva de tipo **Spring Boot** o arranca desde Maven.

### Paso 5: Invalidar cachés si sigue igual
1. **File > Invalidate Caches...**
2. Elegir **Invalidate and Restart**

## Forma recomendada de arranque

### Local
Usa H2 por defecto.

### Producción
Usa el perfil `prod` con variables `DATABASE_URL`, `DATABASE_USERNAME` y `DATABASE_PASSWORD` válidas.

## Qué error debería desaparecer
Después del resync de Maven, debe desaparecer:
- `Cannot load driver class: org.h2.Driver`
- y también el fallo anterior de autenticación a PostgreSQL al arrancar en local.
