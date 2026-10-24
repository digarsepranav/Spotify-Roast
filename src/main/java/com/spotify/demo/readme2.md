# Second 
# Spotify Roast — Spring Boot Backend

A Spring Boot backend that generates humorous Hinglish roasts for songs using an AI API and stores the results in PostgreSQL.

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Groq API
- Maven
- REST APIs

# Features Implemented

## 1. AI-Powered Roast Generation

- Accepts artist and song names through a POST endpoint.
- Uses the Groq API to generate concise Hinglish roasts.
- Returns the generated roast through a `ResponseDTO`.

## 2. PostgreSQL Persistence

Created a `Roast` entity to store generated roasts.

- `id` — auto-generated primary key
- `artistName` — artist name
- `songName` — song name
- `roastText` — generated roast
- `createdAt` — creation timestamp

Configured Spring Data JPA to persist roast records in PostgreSQL.

## 3. Repository Layer

Created `RoastRepository` extending `JpaRepository<Roast, Long>`.

Built-in operations include:

- `save()` — save a roast
- `findAll()` — fetch all records
- `findById()` — find a record by ID
- `findAll(Pageable)` — fetch a page of records

## 4. Roast History API

**Endpoint:** `GET /api/ai/history`

Returns roast history as `ResponseDTO` objects rather than exposing entity objects directly.

## 5. Pagination

Implemented pagination using Spring Data JPA.

- `Pageable` — carries pagination and sorting information
- `PageRequest` — creates a `Pageable` instance
- `Page<Roast>` — contains current-page data and metadata
- `getContent()` — retrieves the current page's records
- Page numbering starts at `0`

**Example request:**

```http
GET /api/ai/history?page=0&size=5
```

## 6. Sorting

Added sorting support through query parameters.

**Example request:**

```http
GET /api/ai/history?page=0&size=5&sort=createdAt,desc
```

Returns up to five records, ordered by creation time with the newest first.

## 7. DTO Mapping with `Page.map()`

Used `Page.map()` to convert each `Roast` entity into a `ResponseDTO`.

- Eliminates the need for a separate DTO list and manual conversion loop.
- Preserves pagination metadata.
- The service returns `Page<ResponseDTO>`.

## Example Response Metadata

The paginated response includes fields such as:

- `content` — current page's roast records
- `totalElements` — total matching records
- `totalPages` — total number of pages
- `number` — current page index
- `size` — page size
- `first` and `last` — page position indicators

# Key Learning Outcomes

- Entity, repository, service, and controller responsibilities
- PostgreSQL persistence using Spring Data JPA
- DTO mapping and response types
- Pagination and sorting in REST APIs
- `Pageable`, `Page`, and `Page.map()`
- Query parameter binding in Spring MVC
- Resolving Java import conflicts

# Next Steps

- Implement Spring Security
- Add user registration and authentication
- Implement JWT-based authorization
- Associate roasts with individual users
- Return user-specific roast history
- Add automated tests
- Improve Docker configuration and deployment

# Project Status

**Implemented and tested:** AI roast generation, PostgreSQL persistence, roast history, pagination, and sorting.
