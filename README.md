# 🎬 Critflix App

Una aplicación móvil desarrollada en **Android Studio con Kotlin**, respaldada por un backend en **Laravel** desplegado en **DigitalOcean**. Critflix permite descubrir, calificar y debatir sobre películas y series, utilizando datos en tiempo real desde **The Movie Database (TMDb)**.

---

## 📲 Características Principales

### 👤 Perfil de Usuario
- Registro e inicio de sesión con email.
- Personalización de perfil: foto, biografía y géneros favoritos.
- Roles de usuario: Básico, Crítico Verificado.

### ⭐ Valoraciones y Comentarios
- Comentarios y respuestas en hilo para fomentar debates.
- Likes/dislikes en comentarios para destacar los más útiles.

### 📝 Críticas y Recomendaciones
- Críticos verificados con visibilidad destacada.
- Sistema de recomendaciones basado en intereses y valoraciones anteriores.

### 📚 Listas Personalizadas
- Crear y administrar listas como: Favoritos, Por ver, Viendo, etc.
- Listas temáticas creadas por usuarios (ej. “Top Ciencia Ficción”).

### 🔍 Búsqueda y Filtros Avanzados
- Búsqueda por título, género, año, etc.
- Ordenar de manera ascendente o descendente.

### 🎞️ Página de Detalles
- Información completa: sinopsis, elenco, equipo, duración, valoraciones.
- Títulos relacionados basados en género.

### 🔔 Notificaciones
- Recibir notificaciones cuando un comentario recibe un like o dislike

### 🛠️ Panel de Administración
- Moderación de comentarios.
- Gestión de contenido audiovisual.
- Estadísticas de uso y actividad.
- Control de roles de usuarios

### 🌐 API Externa
- Integración con [TMDb API](https://www.themoviedb.org/documentation/api) para datos actualizados.
- Sincronización automática de títulos, descripciones, imágenes y más.

### ⚠️ Sistema de Spoilers
- Comentarios marcados con alerta de spoiler para proteger la experiencia del usuario.

---

## 🔐 Roles de Usuario

| Rol               | Descripción |
|------------------|-------------|
| Usuario Básico   | Puede registrarse, calificar, comentar, personalizar perfil, crear listas y demás... |
| Crítico Verificado | Puede escribir críticas destacadas visibles en los primeros lugares de cada sección de comentarios. |

---

## 🧰 Tecnologías Utilizadas

- **Frontend:** Kotlin + Android Studio
- **Backend:** Laravel
- **Base de Datos:** MySQL (via Laravel)
- **Hosting:** DigitalOcean
- **API de Películas:** [TMDb](https://www.themoviedb.org/)
