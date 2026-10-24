# Spotify Roast — Spring Boot Learning Notes

## 1. Project Overview

A Spring Boot REST API that generates short, witty Hinglish roasts using the Groq AI API.

- **Framework:** Spring Boot 4.1.1
- **Java:** 21
- **Package:** `com.spotify.demo`
- **Endpoint:** `POST /api/roast`
- **AI Provider:** Groq API
- **Database:** PostgreSQL configured; roast history persistence is the next feature.

## 2. Project Architecture

```text
Client / Postman
       |
       v
RoastController
       |
       v
RoastService
       |
       v
AiClient ------> Groq API
       |
       v
Response DTO
       |
       v
HTTP Response
```

### Responsibilities

- **Controller:** Handles incoming HTTP requests and responses.
- **DTO:** Defines request and response data structures.
- **Service:** Contains business logic.
- **AiClient:** Communicates with the external Groq API.
- **RestClientConfig:** Configures the reusable `RestClient` bean.
- **GlobalExceptionHandler:** Handles exceptions centrally.

## 3. Groq API Integration

### Configuration

Configuration is stored in `src/main/resources/application.properties`.

```properties
spring.application.name=demo
ai.base-url=https://api.groq.com/openai/v1
ai.model=openai/gpt-oss-20b
ai.api-key=${GROQ_API_KEY}
```

- `ai.base-url`: Base URL of the Groq API.
- `ai.model`: AI model to use.
- `ai.api-key`: Reads the API key from the `GROQ_API_KEY` environment variable.
- Never hard-code API keys or commit secrets to Git.

### RestClient

`RestClient` is Spring's HTTP client for communicating with external APIs.

```java
restClient.post()
    .uri("/chat/completions")
    .contentType(MediaType.APPLICATION_JSON)
    .body(requestBody)
    .retrieve()
    .body(GroqResponse.class);
```

- `post()` selects the HTTP method.
- `uri()` specifies the endpoint.
- `contentType()` sets the request content type.
- `body()` supplies request data or reads the response.
- `retrieve()` executes the request and prepares response handling.
- `body(GroqResponse.class)` converts response JSON into a Java object.

### Request and Response Records

Java records are concise data carriers.

```java
public record Message(String role, String content) {}

public record GroqRequest(String model, List<Message> messages) {}
```

The Java record component names should match the JSON field names unless explicit JSON mapping is configured.

Groq's response contains:

```text
choices[0].message.content
```

This is where the generated roast text is located.

### Keeping AI Responses Short

- Give the model a specific prompt.
- Explicitly request concise Hinglish output.
- Specify a word limit in the prompt.
- Use an output-token limit where supported by the selected model/API.

## 4. Input Validation

Input validation checks whether incoming data follows the API contract before business logic executes.

### Validation Levels

1. **Structural validation:** Required fields, blank values, data types and length.
2. **Business validation:** Application-specific rules.
3. **Resource and security controls:** Request-size limits, rate limiting and bounded AI usage.

Server-side validation is essential because clients can send arbitrary requests.

### Jakarta Bean Validation

Spring Boot commonly uses `spring-boot-starter-validation`.

| Annotation | Purpose |
|---|---|
| `@NotNull` | Rejects `null`; empty strings are allowed. |
| `@NotEmpty` | Rejects `null` and empty values, but whitespace-only strings can pass. |
| `@NotBlank` | Rejects `null`, empty and whitespace-only strings. |
| `@Size` | Limits string length or collection size. |
| `@Pattern` | Validates a value against a pattern. |
| `@Min` / `@Max` | Enforces numeric bounds. |
| `@Valid` | Triggers validation of the DTO's declared constraints. |

For `artistName` and `songName`, `@NotBlank` is a useful starting point.

### Validation Flow

```text
Client sends JSON
       |
       v
Spring converts JSON to DTO
       |
       v
@Valid triggers validation
       |
       +---- Valid ----> Controller -> Service -> Groq
       |
       +---- Invalid --> MethodArgumentNotValidException
                                 |
                                 v
                        GlobalExceptionHandler
                                 |
                                 v
                           HTTP 400
```

Malformed JSON or incorrect JSON data types can fail during deserialization before DTO validation.

## 5. Global Exception Handling

### Important Annotations

- `@RestControllerAdvice`: Centralizes exception handling across controllers.
- `@ExceptionHandler`: Specifies which exception a handler method handles.
- `@Valid`: Triggers DTO validation at the controller boundary.

### Important Methods

- `getBindingResult()`: Retrieves validation results and error details.
- `getFieldErrors()`: Returns field-level validation errors.
- `getField()`: Returns the name of the field that failed validation.
- `getDefaultMessage()`: Returns the custom validation message defined in the DTO.

### Exception Handling Flow

```text
Validation fails
       |
       v
MethodArgumentNotValidException
       |
       v
getBindingResult()
       |
       v
getFieldErrors()
       |
       v
Extract field names and messages
       |
       v
Build response body
       |
       v
Return HTTP 400
```

### Structured Validation Response

A structured JSON response is better than converting all errors into one string.

```json
{
  "errors": {
    "artistName": "Artist name cannot be blank",
    "songName": "Song name cannot be blank"
  }
}
```

Return the map directly instead of using `errors.toString()`.

- `errors` is a `Map<String, String>`.
- `errors.toString()` converts the map into a single string.
- Returning the map directly allows Spring to serialize it as structured JSON.

The handler's return type must allow a map response, such as `ResponseEntity<Map<String, Object>>`.

### Handling Multiple Field Errors

```java
Map<String, String> errors = ex.getBindingResult()
    .getFieldErrors()
    .stream()
    .collect(Collectors.toMap(
        error -> error.getField(),
        error -> error.getDefaultMessage(),
        (existing, replacement) -> existing
    ));
```

- `stream()` processes the list elements through a pipeline.
- `error -> error.getField()` extracts the field name.
- `error -> error.getDefaultMessage()` extracts the validation message.
- `Collectors.toMap()` creates a map from the extracted keys and values.
- `(existing, replacement) -> existing` keeps the first value if a duplicate key occurs.

This is a simple duplicate-handling strategy. A production API may instead return a list of messages per field so that no validation errors are lost.

### HTTP Status Codes

| Status | Meaning |
|---|---|
| `400 Bad Request` | Invalid request or input |
| `401 Unauthorized` | Authentication is missing or invalid |
| `403 Forbidden` | Caller lacks permission |
| `404 Not Found` | Resource does not exist |
| `429 Too Many Requests` | Rate limit exceeded |
| `500 Internal Server Error` | Unexpected server failure |
| `502 Bad Gateway` | Gateway received an invalid upstream response |

## 6. Java Collections

### List

An ordered collection that can contain duplicate elements.

### Map

Stores key-value pairs. Keys are unique.

### HashMap

A common mutable implementation of `Map`.

### put(key, value)

Adds an entry or replaces the existing value when the key already exists.

```java
errors.put("songName", "Song name is required");
```

If `songName` already exists, its value is replaced.

### Map.of(key, value)

Creates a small, unmodifiable map.

```java
Map.of("message", "Song name is required");
```

Unlike `put()`, `Map.of()` creates a map instead of modifying an existing map.

### Enhanced For-Each Loop

Processes each element in a collection.

```java
for (FieldError error : fieldErrors) {
    String field = error.getField();
    String message = error.getDefaultMessage();
    errors.put(field, message);
}
```

Here, `error` is the loop variable representing the current `FieldError`.

## 7. Java Streams

### stream()

Creates a Stream from a collection so its elements can be processed in a pipeline. It does not itself create a map or store the final result.

### Lambda Expression

A concise function that accepts input and performs an operation.

```java
error -> error.getField()
```

Here, `error` represents one `FieldError` element supplied by the Stream.

### collect()

A terminal operation that gathers processed elements into a result.

### Collectors.toList()

Collects Stream elements into a List.

### Collectors.toMap()

Creates a Map using two functions:

1. **Key mapper:** Determines the key.
2. **Value mapper:** Determines the value.

```java
Collectors.toMap(
    error -> error.getField(),
    error -> error.getDefaultMessage()
)
```

A third merge function can handle duplicate keys. Without one, duplicate keys can cause an exception.

### Stream vs. For-Each

Streams are useful, but they are not mandatory. A regular for-each loop and `Map.put()` can achieve the same result and may be easier to understand while learning.

## 8. ResponseEntity

`ResponseEntity` lets the application control the HTTP status and response body.

- `ResponseEntity.badRequest()` returns HTTP 400.
- `.status(HttpStatus.BAD_REQUEST)` explicitly selects HTTP 400.
- `.body(...)` sets the response body.
- `ResponseEntity<T>` uses a generic type that determines which body types are accepted.

## 9. JDK 21 and IntelliJ Troubleshooting

An earlier compilation error occurred because the project targeted JVM 21 while IntelliJ was configured to use JDK 17.

### Fix

1. Open **File → Project Structure**.
2. Set the Project SDK to JDK 21.
3. Check the module SDK and language level.
4. For Maven projects, verify the Java version in `pom.xml`.
5. Ensure Maven importer and runner use a compatible JDK.
6. Rebuild the project.

Changing the JDK normally does not delete `application.properties`. If configuration appears missing, verify the correct project and file are open, then check version-control changes before restoring anything.

## 10. Spotify API Decision

Spotify's current Development Mode requirements blocked the Web API option in the user's setup because the account did not have Premium.

The Spotify API integration has therefore been deferred.

For now, the API accepts artist and song names directly and sends them to Groq for roast generation.

A redirect URL is used in OAuth flows to send a user back to an application after authorization. Registering a URL does not automatically create a callback endpoint. The Client Credentials flow for public API access does not use a user redirect URL.

## 11. Next Feature: Roast History

The next feature is to persist generated roasts in PostgreSQL.

### Implementation Plan

1. Confirm the PostgreSQL connection.
2. Create a JPA `Roast` entity.
3. Add fields such as ID, artist name, song name, roast text and creation timestamp.
4. Create a Spring Data JPA repository.
5. Save each generated roast after successful generation.
6. Add `GET /api/roasts` to retrieve history.
7. Add `GET /api/roasts/{id}` to retrieve one roast.
8. Add `DELETE /api/roasts/{id}` to delete a roast.
9. Return `404 Not Found` when the requested roast does not exist.
10. Add service and controller tests.

### Responsibilities

- **Entity:** Represents persisted database data.
- **Repository:** Provides database access.
- **Service:** Handles business logic.
- **DTO:** Defines API request and response structures.
- **Controller:** Handles HTTP requests and responses.

## 12. Learning Preferences

- Learn step by step, starting with theory and architecture.
- Explain each new Java/Spring concept before using it.
- No MCQs or checkpoint questions.
- Keep explanations concise and use Hinglish where helpful.
- Do not provide code unless requested; first explain the architecture and steps.
- For debugging, identify the problematic lines and explain the cause instead of rewriting the entire solution.