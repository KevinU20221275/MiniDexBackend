# Minidex API

Minidex API está orientada al sector del entretenimiento y coleccionismo digital, permitiendo gestionar entrenadores y sus Pokémon de forma sencilla y segura.

API REST desarrollada con Spring Boot para la gestión de entrenadores y Pokémon.
Esta API permite crear y administrar entrenadores, así como capturar, consultar y eliminar Pokémon de su Pokédex.

Incluye:

- Registro y autenticación de entrenadores con JWT.
- Gestión de información de entrenadores: nombre, username, monedas y nivel.
- Captura, actualización y eliminación de Pokémon asociados a cada entrenador.
- Endpoints REST claros y seguros, con validaciones y manejo de errores consistente.
- Integración con H2 en memoria para pruebas y desarrollo rápido.

Cada entrenador puede capturar múltiples Pokémon, y cada Pokémon puede pertenecer a varios entrenadores, simulando una Pokédex compartida.

Los Pokémon son obtenidos desde la PokeAPI y luego almacenados en la base de datos local.
Al capturarse, el sistema valida si el Pokémon y sus tipos existen previamente; en caso contrario, los crea y asocia correctamente.
De esta forma, se mantiene una base de datos optimizada y coherente, evitando duplicados y gestionando las relaciones many-to-many entre entrenadores y Pokémon.

------------------------------------------------------------------------

## 📂 Arquitectura del Proyecto

``` text
src/
├── main/
│   ├── java/org/kmontano/minidex/
|   |   ├── auth/
│   │   │   └── JwtFilter.java
|   |   ├── config/
|   |   |   ├── CorsConfig.java
|   |   |   ├── SecurityConfig.java
│   │   │   └── SwaggerConfig.java
│   │   ├── controllers/
│   │   │   ├── AuthController.java
│   │   │   ├── PokemonController.java
│   │   │   └── TrainerController.java
│   │   ├── dto/
│   │   │   ├── AuthRequest.java
│   │   │   ├── AuthResponse.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── PokemonDTO.java
│   │   │   ├── TrainerDTO.java
│   │   │   ├── UpdateCoinsRequest.java
│   │   │   └── UpdateNameAndUsernameRequest.java
│   │   ├── exception/
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── models/
│   │   │   ├── Pokemon.java
│   │   │   ├── PokemonType.java
│   │   │   └── Trainer.java
│   │   ├── repositories/
│   │   │   ├── PokemonRepository.java
│   │   │   ├── TrainerRepository.java
│   │   │   └── TypeRepository.java
│   │   ├── services/
│   │   │   ├── PokemonService.java
│   │   │   ├── PokemonServiceImpl.java
│   │   │   ├── TrainerService.java
│   │   │   └── TrainerServiceImpl.java
│   │   ├── utils/
│   │   │   ├── AuthUtils.java
│   │   │   ├── JwtUtil.java
│   │   │   └── PasswordEncoder.java
│   │   └── MinidexApplication.java
│   └── resources/
│       ├── application.properties
└── test/
    └── java/org/kmontano/minidex/
        ├── AuthIntegrationTest.java
        ├── MinidexApplicationTests.java
        ├── PokemonController.java
        └── TrainerController.java
```

------------------------------------------------------------------------

## Modelo de Datos

| Entidad         | Atributo | Tipo          | Descripción                      |
| --------------- | -------- | ------------- | -------------------------------- |
| **Trainer**     | id       | Long          | Identificador único              |
|                 | name     | String        | Nombre del entrenador            |
|                 | username | String        | Nombre de usuario único          |
|                 | password | String        | Contraseña hasheada              |
|                 | coins    | Integer       | Monedas del jugador              |
|                 | level    | Integer       | Nivel del jugador                |
|                 | pokemons | List<Pokemon> | Lista de Pokémon capturados      |
| **Pokemon**     | id       | Long          | Identificador único              |
|                 | name     | String        | Nombre del Pokémon               |
|                 | image    | String        | Imagen del Pokémon               |
|                 | hp       | Integer       | Valor de vida                    |
|                 | defense  | Integer       | Valor de defensa                 |
|                 | attack   | Integer       | Valor de ataque                  |
|                 | speed    | Integer       | Valor de velocidad               |
|                 | leve;    | Integer       | Nivel del Pokemon                |
|                 | type     | List<PokemonType> | Tipo de Pokémon              |
| **PokemonType** | id       | Long          | Identificador del tipo           |
|                 | name     | String        | Tipo (agua, fuego, planta, etc.) |


 

Relación:\
`Trainer (N) ---- (N) Pokemon`
`Pokemon (N) ---- (N) PokemonType`

------------------------------------------------------------------------



## ⚙️ Endpoints

### 🔹 Auth Controller

#### Registra un nuevo entrenador y devuelve un token JWT junto con los datos del usuario.

**POST** `/auth/register`\
**Body:**

``` json
{
  "name": "Ash Ketchum",
  "username": "ash",
  "password": "pikachu123"
}
```

**Respuesta:**
``` json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhc2giLCJpYXQiOjE3NjAwNjIzNzIsImV4cCI6MTc2MDE0ODc3Mn0.hSKFBzHzJM2CJYCV_Gu0BmPkMyqGSti9Fgnic0Zj4to",
    "trainerDTO": {
        "id": 1,
        "name": "Ash Ketchum",
        "username": "ash",
        "level": 1,
        "coins": 250,
        "pokedex": []
    }
}
```
#### Códigos de respuesta:
`201 Created`: Entrenador creado exitosamente.\
`409 Conflict`: El nombre de usuario ya existe.

![POST](./docs/auth_register.png)

### Login de usuario (Entrenadores)

**POST** `/auth/login`\
**Body:**

``` json
{
  "username": "ash",
  "password": "pikachu123"
}
```

**Respuesta:**
``` json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhc2giLCJpYXQiOjE3NjAwNjI1NjgsImV4cCI6MTc2MDE0ODk2OH0.khkOIHdggb_AG0rUwPOwOQPkhINHvoVCr_2UOWQ2Mgo",
    "trainerDTO": {
        "id": 1,
        "name": "Ash Ketchum",
        "username": "ash",
        "level": 1,
        "coins": 250,
        "pokedex": []
    }
}
```
#### Códigos de respuesta:
`200 OK`: Entrenador creado exitosamente.\
`401 Unauthorized`: El nombre de usuario ya existe.

![POST](./docs/auth_login.png)


------------------------------------------------------------------------

### 🔹 Trainer Controller


#### Obtener la data del entrenador

**GET** `/trainers/me`

**Respuesta:**

``` json
{
    "id": 1,
    "name": "Ash Ketchum",
    "username": "ash",
    "level": 1,
    "coins": 250,
    "pokedex": []
}
```
#### Códigos de respuesta:
`200 OK`: Entrenador encontrado.\
`401 Unauthorized`: No esta autorizado.\
`404 Not Found`: Entrenador no encontrado.

#### Requiere el token
![GET](./docs/get_trainer.png)

#### Capturar un Pokémon

**POST** `/trainers/me/pokedex`\
**Body:**
``` json
{
  "name": "articuno",
  "image": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/144.png",
  "hp": 90,
  "attack": 85,
  "defense": 100,
  "speed": 125,
  "level" : 1,
  "types": [
        {
            "name" : "ice"
        },
        {
            "name" : "flying"
        }
    ]
}
```

**Respuesta:**

``` json
{
    "id": 1,
    "name": "articuno",
    "image": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/144.png",
    "hp": 90,
    "attack": 85,
    "defense": 100,
    "speed": 125,
    "types": [
        "ice",
        "flying"
    ]
}
```
#### Códigos de respuesta:
`200 OK`: Pokemon Agregado a la pokedex.\
`401 Unauthorized`: No esta authorizado.\
`404 Not Found`: Entrenador no encontrado.

#### Requiere el token
![POST](./docs/post_trainer_addPokemon.png)


#### obtener la pokedex del entrenador

**GET** `/trainers/me/pokedex`

**Respuesta:**

``` json
[
    {
        "id": 1,
        "name": "articuno",
        "image": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/144.png",
        "hp": 90,
        "attack": 85,
        "defense": 100,
        "speed": 125,
        "types": [
            "ice",
            "flying"
        ]
    }
]
```


#### Actualizar name o username

**PUT** `/trainers/me`\
**Body:**
``` json
{
    "name" :"Ash Ketchum",
    "username" : "ash97"
}
```

**Respuesta:**
``` json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhc2g5NyIsImlhdCI6MTc2MDA2NDg5NywiZXhwIjoxNzYwMTUxMjk3fQ.wbxJjFyAAGEhL-aO8qnbI8uA1TflGXtK_QHTzzPN9Mc",
    "trainerDTO": {
        "id": 1,
        "name": "Ash Ketchum",
        "username": "ash97",
        "level": 1,
        "coins": 250,
        "pokedex": [
            {
                "id": 1,
                "name": "articuno",
                "image": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/144.png",
                "hp": 90,
                "attack": 85,
                "defense": 100,
                "speed": 125,
                "types": [
                    "ice",
                    "flying"
                ]
            }
        ]
    }
}
```
#### Códigos de respuesta:
`200 OK`: Datos Actualizados conexito.\
`401 Unauthorized`: No esta authorizado.\
`404 Not Found`: Entrenador no encontrado.

#### Requiere token
### Nota: Copie el nuevo token para realizar las demas peticiones 
![PUT](./docs/put_trainer_nameAndUsername.png)



#### Actualizar coins del trainer action puede ser "add" o "subtract" si es add suma las monedas si es subtract las resta
**PUT** `/trainers/me/coins`\
**Body:**
``` json
{
  "action": "add",
  "coins": 100
}
```

**Respuesta:**
``` json
{
    "id": 1,
    "name": "Ash Ketchum",
    "username": "ash97",
    "level": 2,
    "coins": 400,
    "pokedex": [
        {
            "id": 1,
            "name": "articuno",
            "image": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/144.png",
            "hp": 90,
            "attack": 85,
            "defense": 100,
            "speed": 125,
            "types": [
                "ice",
                "flying"
            ]
        }
    ]
}
```
#### Códigos de respuesta:
`200 Ok`: Monedas actualizadas correctamente.\
`401 Unauthorized`: No esta authorizado.\
`403 Bad Request`: No tiene monedas suficientes (para comprar) o Accion no encontrada

#### Requiere token
![PATCH](./docs/patch_trainer_coins.png)



**DELETE** `/trainers/me/pokedex/{pokemonId}`\
``` text
/trainers/me/pokedex/1
```

#### Códigos de respuesta:
`204 No Content`: El pokemon se elimino correctamente de la pokedex \
`401 Unauthorized`: No esta authorizado.\
`404 Not Found`: Entrenador no encontrado | El pokemon no esta en la Pokedex del entrenador.

**Respuesta:**
![DELETE](./docs/delete_trainer_pokemon.png)



------------------------------------------------------------------------

### 🔹 Pokémon Controller

#### Obtener todos los Pokémon

**GET** `/pokemons`\
**Respuesta:**

``` json
[
    {
        "id": 1,
        "name": "articuno",
        "image": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/144.png",
        "hp": 90,
        "attack": 85,
        "defense": 100,
        "speed": 125,
        "types": [
            "ice",
            "flying"
        ]
    }
]
```
#### Códigos de respuesta:
`200 OK`: Exito listando los pokemons \
`401 Unauthorized`: No esta authorizado.

#### Requiere el token
![GET](./docs/get_pokemons.png)

#### Crear un Pokémon

**POST** `/pokemons`\
**Body:**

``` json
{
  "name": "lugia",
  "image": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/249.png",
  "hp": 106,
  "attack": 90,
  "defense": 130,
  "speed": 110,
  "types": [
        {
            "name" : "fire"
        },
        {
            "name" : "bug"
        },
        {
            "name" : "flying"
        }
    ]
}
```

**Respuesta:**

``` json
{
    "id": 2,
    "name": "lugia",
    "image": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/home/249.png",
    "hp": 106,
    "attack": 90,
    "defense": 130,
    "speed": 110,
    "types": [
        "fire",
        "bug",
        "flying"
    ]
}
```
#### Códigos de respuesta:
`201 Created`: pokemons creado con exito \
`403 Bad Request`: No se pudo crear el pokemon\
`401 Unauthorized`: No esta authorizado.

#### Requiere token
![POST](./docs/post_pokemon.png)

------------------------------------------------------------------------

## Configuración de Base de Datos (H2)

Archivo `application.properties`:

``` properties
spring.application.name=minidex

frontend.url=http://localhost:4321

spring.datasource.url=jdbc:h2:mem:minidexdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

Acceso a consola H2:\
👉 <http://localhost:8080/h2-console>

------------------------------------------------------------------------

## 🚀 Ejecución

### Instrucciones para probar el proyecto

- Contenido del paquete
    - El archivo comprimido contiene:
        - Backend (H2 / Java): la API del proyecto con todas las rutas y configuraciones necesarias.
        - Frontend (Astro / React): aplicación web para interactuar con la API.
    - Se incluyen los archivos necesarios para correr cada parte de forma independiente.

- Requisitos previos
    - Java 17+
    - Node.js 18+
    - Navegador web moderno (Chrome, Edge, Firefox)
    - Herramienta opcional: Postman para probar la API directamente

- Configuración del backend
    - Descomprimir el paquete del backend.
    - Abrir un terminal en la carpeta del proyecto.
    - Ejecutar el comando:

```bash
./mvnw spring-boot:run
```
`O puede habrirlo y ejecutarlo en un IDE como IntelliJ IDEA`\

Esto iniciará la API en http://localhost:8080

- La base de datos H2 corre en memoria. Para acceder a la consola web:
    - URL: http://localhost:8080/h2-console
    - JDBC URL: jdbc:h2:mem:minidexdb
    - Usuario: sa
    - Contraseña: password

- Probar la API
    - Opción A: Usando Postman
        - Abrir Postman y crear una nueva colección.
        - Usar las rutas disponibles
Asegurarse de que la API esté corriendo antes de hacer cualquier petición.

    - Opción B: Usando el frontend (Astro / React)
        - Descomprimir el paquete del frontend.
        - Abrir un terminal en la carpeta del proyecto.
    - Instalar dependencias:
```bash
npm install
```

- Iniciar el servidor de desarrollo:

```bash
npm run dev
```
    
- Abrir la URL indicada en la terminal (por defecto http://localhost:3000) para interactuar con la API.


## Desde Github
### Backend (API)
- Clonar el proyecto backend:
```bash
git clone https://github.com/KevinU20221275/MiniDexBackend.git
cd MiniDexBackend
```
- Compilar y ejecutar con Maven:
```bash
mvn spring-boot:run
```
La API estará disponible en:
👉 http://localhost:8080

### Frontend (Astro + React)
- Clonar el proyecto frontend
```bash
git clone https://github.com/KevinU20221275/MiniDexFrontEnd.git
cd MiniDexFrontEnd
```
- instalar dependencias
```bash
npm install
```

- iniciar el servidor de desarrollo
```bash
npm run dev
```
Abrir la URL indicada en la terminal (por defecto):
👉 http://localhost:4321

## Notas importantes
- Todas las rutas de la API funcionan tanto desde Postman como desde la interfaz web.
- La base de datos H2 es temporal (en memoria). Cada vez que se reinicie la aplicación, se perderán los datos creados en la sesión anterior.
- Todos los endpoint a excepcion de `auth/register`, `auth/login` requieren el token de acceso
- El token de acceso se proporciona en los 2 endpoints antes mensionados

------------------------------------------------------------------------

## 🧪 Tests

Este proyecto incluye tests automatizados para asegurar que las funcionalidades principales funcionan correctamente.

### Controladores testeados:

- TrainerController: pruebas de agregar un pokemon a la pokedex, agregar monedas, remover pokemon de la pokedex.
- PokemonController: pruebas de endpoints relacionados con los Pokémon (crear, listar).
- AuthController: pruebas de autenticación, registro de usuarios y registro de un usuario duplicado.

### Tipos de tests incluidos:

- Unitarios: verifican la lógica de los servicios y métodos de cada controlador.
- De integración: prueban la interacción completa con el contexto de Spring Boot y la base de datos en memoria (H2).

### Frameworks utilizados:

- JUnit 5
- Spring Boot Test
- Mockito

### Ejecutar los tests:
```bash
# Con Maven
./mvnw test
```

![TEST](./docs/tests.png)

-----------------------------------------------------------------------

## Tecnologías Usadas

-   Java 17\
-   Spring Boot 3\
-   Maven\
-   H2 Database\

------------------------------------------------------------------------

## 👨‍💻 Autor

Desarrollado por **Kevin Antonio Montano Aquino** ⚡\
📅 *Proyecto académico en Kodigo Academina JD22 - 2025*