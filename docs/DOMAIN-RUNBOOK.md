# Domain Runbook — food-court-service

Living reference for the domain behavior of `food-court-service`. Updated with **every HU**:
business rules, invariants, and the custom-exception → message → HTTP-status mapping.

## Conventions
- Validation errors extend `DomainException` → **HTTP 400**.
- Conflicts extend `ConflictException` → **HTTP 409**.
- Not found → `NoDataFoundException` → **HTTP 404**.
- Business rules live in `domain/validation/*Validator`; use cases delegate to them.
- Cross-service checks (e.g. owner role) live in the use case behind a domain SPI port, never in
  the pure validator.
- `ControllerAdvisor` maps every exception family and returns `{ "message": ... }`.
- **Authentication/authorization (HU5)** is an infrastructure concern: a stateless JWT filter and
  role-based rules in `infrastructure/configuration/security`. The domain stays unaware of it.

---

## HU2 — Create restaurant

- **Endpoint:** `POST /api/v1/restaurants`
- **Use case:** `RestaurantUseCase.createRestaurant`
- **Validator:** `RestaurantValidator.validateNewRestaurant`
- **Cross-service port:** `IUserValidationPort.isOwner` → `UserValidationRestClientAdapter`
  (calls `GET /api/v1/users/{id}` on `users-service` via Spring `RestClient`).

### Business rules / invariants
- All fields required: `name`, `nit`, `address`, `phone`, `logoUrl`, `ownerId`.
- `name` cannot be made of numbers only — it must contain at least one letter.
- `nit`: digits only, **between 9 and 10 characters** (Colombian NIT: 9-digit base + verification digit).
- `nit` must be **unique** — two restaurants cannot share the same NIT (checked in the use case and
  backed by a unique column on the `restaurants` table).
- `phone`: digits only, optional leading `+`, max 13 characters.
- `ownerId` must reference a user that exists in `users-service` **and** holds the **OWNER** role
  (role id 2). A missing user or a non-owner role is rejected.
- The admin-only authorization gate (only an Administrator may create a restaurant) is enforced
  with JWT in **HU5**; HU2 validates the owner role only.

### Exceptions
| Exception | Message | HTTP |
|-----------|---------|------|
| `RequiredFieldException` | All fields are required | 400 |
| `InvalidRestaurantNameException` | The restaurant name cannot contain only numbers | 400 |
| `InvalidNitException` | The NIT must be numeric and contain between 9 and 10 digits | 400 |
| `InvalidPhoneFormatException` | The phone must be numeric, may start with '+', and have at most 13 characters | 400 |
| `InvalidOwnerException` | The provided owner id does not belong to a registered owner | 400 |
| `NitAlreadyExistsException` | A restaurant with this NIT already exists | 409 |

---

## HU3 — Create dish

- **Endpoint:** `POST /api/v1/dishes`
- **Use case:** `DishUseCase.createDish`
- **Validator:** `DishValidator.validateNewDish`
- **Cross-aggregate port:** `IRestaurantPersistencePort.findRestaurantById` → `RestaurantJpaAdapter`
  (loads the target restaurant to check existence and ownership).

### Business rules / invariants
- All fields required: `name`, `price`, `description`, `imageUrl`, `category`, `restaurantId`.
- `price` must be a positive integer **greater than zero**.
- `active` is forced to `true` on creation (never trusted from the request).
- The `restaurantId` must reference an existing restaurant; otherwise the dish is rejected.
- A dish can only be created by the **owner of that restaurant** — the requester `ownerId` must
  match the restaurant's `ownerId`.
- The JWT authorization gate (only a logged-in Owner may hit this endpoint) arrives in **HU5**;
  HU3 validates ownership against the restaurant record only.

### Exceptions
| Exception | Message | HTTP |
|-----------|---------|------|
| `RequiredFieldException` | All fields are required | 400 |
| `InvalidPriceException` | The dish price must be a positive number greater than zero | 400 |
| `RestaurantNotFoundException` | The restaurant does not exist | 404 |
| `NotRestaurantOwnerException` | This action can only be performed by the owner of the restaurant | 403 |

---

## HU4 — Update dish

- **Endpoint:** `PATCH /api/v1/dishes/{dishId}`
- **Use case:** `DishUseCase.updateDish`
- **Validator:** `DishValidator.validateDishUpdate`
- **Ports:** `IDishPersistencePort.findDishById` (loads the dish) and
  `IRestaurantPersistencePort.findRestaurantById` (loads its restaurant to check ownership).

### Business rules / invariants
- **Only `price` and `description` can be modified.** `name`, `imageUrl`, `category`,
  `restaurantId` and `active` are never changed by this operation.
- Both `price` and `description` are required in the update payload; `price` must be a positive
  integer **greater than zero** and `description` must not be blank.
- The `dishId` must reference an existing dish; otherwise the update is rejected.
- A dish can only be updated by the **owner of its restaurant** — the requester `ownerId` must
  match the restaurant's `ownerId`.
- The JWT authorization gate (only a logged-in Owner may hit this endpoint) arrives in **HU5**;
  HU4 validates ownership against the restaurant record only.

### Exceptions
| Exception | Message | HTTP |
|-----------|---------|------|
| `RequiredFieldException` | All fields are required | 400 |
| `InvalidPriceException` | The dish price must be a positive number greater than zero | 400 |
| `DishNotFoundException` | The dish does not exist | 404 |
| `NotRestaurantOwnerException` | This action can only be performed by the owner of the restaurant | 403 |

---

## HU5 — Authentication / JWT (authorization side)

This service does **not** issue tokens; `users-service` does. Here HU5 **verifies** the JWT (shared
HS256 secret, `jwt.secret`) and enforces role-based access. Implemented entirely in
`infrastructure/configuration/security` — the domain and application layers are untouched except that
the requester's identity now comes from the token instead of the request body.

### Components
- `JwtTokenValidator` — verifies the signature/expiry and extracts the user id (subject) and role.
- `JwtAuthenticationFilter` — on each request, sets the authenticated user id (principal) and a
  single `ROLE_<role>` authority.
- `SecurityConfig` — stateless `SecurityFilterChain` with the authorization rules below.
- `JwtAuthenticationEntryPoint` — returns **401** JSON when no valid token is present.

### Endpoint authorization
| Endpoint | Rule |
|----------|------|
| `POST /api/v1/restaurants` | role **ADMINISTRATOR** (the assigned `ownerId` stays in the body) |
| `POST /api/v1/dishes` | role **OWNER**; the owner id is taken from the token, not the body |
| `PATCH /api/v1/dishes/{dishId}` | role **OWNER**; the owner id is taken from the token, not the body |
| Swagger UI / OpenAPI docs | public |
| any other request | authenticated |

### Behavior notes
- `CreateDishRequestDto` / `UpdateDishRequestDto` **no longer carry `ownerId`**; the controller reads
  it from the authenticated principal (`@AuthenticationPrincipal`) and passes it to the use case, so
  the existing "owner owns the restaurant" rule (`NotRestaurantOwnerException`, 403) still applies.
- Unauthenticated access → **401**; authenticated but wrong role → **403** (Spring Security).

---

## HU7 — Enable / disable dish

- **Endpoint:** `PATCH /api/v1/dishes/{dishId}/status`
- **Use case:** `DishUseCase.setDishStatus`
- **Validator:** `DishValidator.validateDishStatus`
- **Ports:** `IDishPersistencePort.findDishById` (loads the dish) and
  `IRestaurantPersistencePort.findRestaurantById` (loads its restaurant to check ownership).

### Business rules / invariants
- **Only the `active` flag is modified.** `name`, `price`, `description`, `imageUrl`, `category`
  and `restaurantId` are never changed by this operation.
- The `active` flag is **required** in the payload (`true` to enable, `false` to disable); a missing
  flag is rejected.
- The `dishId` must reference an existing dish; otherwise the operation is rejected.
- A dish can only be enabled/disabled by the **owner of its restaurant** — the requester `ownerId`
  (taken from the JWT) must match the restaurant's `ownerId`. This enforces both acceptance
  criteria: only the owner may toggle a dish, and dishes of other restaurants cannot be touched.
- The JWT authorization gate (role **OWNER**) is applied in `SecurityConfig` via the existing
  `PATCH /api/v1/dishes/**` → OWNER rule; no new security rule is required.

### Exceptions
| Exception | Message | HTTP |
|-----------|---------|------|
| `RequiredFieldException` | All fields are required | 400 |
| `DishNotFoundException` | The dish does not exist | 404 |
| `NotRestaurantOwnerException` | This action can only be performed by the owner of the restaurant | 403 |
| (unauthenticated request) | Authentication is required | 401 |

---

## HU6 — Create employee

- **Endpoint:** `POST /api/v1/restaurants/{restaurantId}/employees`
- **Use case:** `EmployeeUseCase.createEmployee`
- **Validator:** `EmployeeValidator.validateNewEmployee`
- **Cross-service port (create):** `IUserCreationPort.createEmployee` → `UserCreationRestClientAdapter`
  (calls `POST /api/v1/users/employee` on `users-service`, which creates the user with the EMPLOYEE role).
- **Cross-aggregate port:** `IRestaurantPersistencePort.findRestaurantById` (loads the target restaurant
  to check existence and ownership).
- **Persistence port:** `IEmployeePersistencePort.saveEmployeeRestaurant` → `EmployeeRestaurantJpaAdapter`
  (stores the employee↔restaurant link in the `employee_restaurant` table).

### Business rules / invariants
- Only the **OWNER of the target restaurant** may create an employee there:
  - `SecurityConfig` gates `POST /api/v1/restaurants/*/employees` to role **OWNER**;
  - the use case additionally checks that the authenticated caller owns that specific restaurant
    (`restaurant.ownerId == requesterOwnerId`), otherwise `NotRestaurantOwnerException` (403).
- Required fields: `name`, `lastName`, `documentNumber`, `phone`, `email`, `password`, `restaurantId`.
  No birth date / adult-age rule applies to employees (per HU6 acceptance criteria).
- `email` valid format; `phone` digits only, optional `+`, max 13; `documentNumber` digits only.
- The `restaurantId` (path variable) must reference an existing restaurant; otherwise 404.
- The employee is created in `users-service` with the **EMPLOYEE** role (role forced there);
  `food-court-service` persists the `employee_restaurant` link and never stores credentials.
- The owner's JWT is forwarded to `users-service` (`AuthorizationForwardingInterceptor`) so the
  downstream endpoint authorizes the same OWNER caller.
- Uniqueness of email/document lives in `users-service`; a 409 from it is surfaced as
  `EmployeeAlreadyExistsException` (409).
- The response never includes the password.

### Exceptions
| Exception | Message | HTTP |
|-----------|---------|------|
| `RequiredFieldException` | All fields are required | 400 |
| `InvalidEmailFormatException` | The email format is invalid | 400 |
| `InvalidPhoneFormatException` | The phone must be numeric, may start with '+', and have at most 13 characters | 400 |
| `InvalidDocumentNumberException` | The document number must contain digits only | 400 |
| `RestaurantNotFoundException` | The restaurant does not exist | 404 |
| `NotRestaurantOwnerException` | This action can only be performed by the owner of the restaurant | 403 |
| `EmployeeAlreadyExistsException` | An employee with this email or document number already exists | 409 |
| (unauthenticated request) | Authentication is required | 401 |

---

## HU9 — List restaurants

- **Endpoint:** `GET /api/v1/restaurants?page={page}&size={size}`
- **Use case:** `RestaurantUseCase.listRestaurants`
- **Validator:** `PaginationValidator.validate` (pure, reusable across paginated listings)
- **Persistence port:** `IRestaurantPersistencePort.findAllOrderedByName` → `RestaurantJpaAdapter`
  (Spring Data `PageRequest` with a case-insensitive ascending sort on `name`).

### Business rules / invariants
- Returns **all restaurants ordered alphabetically by name**, case-insensitive ascending.
- **Paginated**: `page` is zero-based (default `0`) and `size` sets the elements per page
  (default `10`). `page` must be `>= 0` and `size` must be `>= 1`; otherwise the request is rejected.
- Each item exposes **only** the fields HU9 requires: `name` and `logoUrl`
  (`RestaurantListItemResponseDto`). The full restaurant record is never leaked here.
- The response is wrapped in a generic `PageResponseDto` carrying `content`, `page`, `size`,
  `totalElements`, `totalPages`.
- Authorization: only the **CLIENT** role may list restaurants
  (`GET /api/v1/restaurants` → CLIENT rule in `SecurityConfig`).
- Pagination metadata is kept framework-free in the domain via `PagedResult<T>`, so the use case
  never depends on Spring Data.

### Exceptions
| Exception | Message | HTTP |
|-----------|---------|------|
| `InvalidPaginationException` | The page number must be zero or greater and the page size must be greater than zero | 400 |
| (unauthenticated request) | Authentication is required | 401 |
| (authenticated, non-client role) | (Spring Security forbidden) | 403 |

---

## HU10 — List dishes of a restaurant

- **Endpoint:** `GET /api/v1/dishes?restaurantId={id}&category={category}&page={page}&size={size}`
- **Use case:** `DishUseCase.listDishesByRestaurant`
- **Validator:** `PaginationValidator.validate` (same pure validator reused from HU9).
- **Persistence port:** `IDishPersistencePort.findActiveDishesByRestaurant` → `DishJpaAdapter`
  (Spring Data `PageRequest` with a case-insensitive ascending sort on `name`; derived queries
  `findByRestaurantIdAndActiveTrue` / `findByRestaurantIdAndCategoryIgnoreCaseAndActiveTrue`).

### Business rules / invariants
- Lists the dishes of a **single restaurant** (`restaurantId` is required). The restaurant must
  exist; otherwise the request is rejected with `RestaurantNotFoundException` (404).
- Only **active dishes** are returned: a disabled dish (HU7) is removed from the client menu, so it
  never appears in this listing.
- **Optional category filter**: when `category` is omitted/blank, all active dishes are returned;
  when present it is trimmed and matched **case-insensitively**.
- **Paginated**: `page` is zero-based (default `0`) and `size` sets the elements per page
  (default `10`). `page` must be `>= 0` and `size` must be `>= 1`; otherwise rejected.
- Dishes are ordered alphabetically by `name`, case-insensitive ascending.
- Each item exposes **only** the client-facing menu fields (`name`, `price`, `description`,
  `imageUrl`, `category`) via `DishListItemResponseDto`; internal `id`, `restaurantId` and the
  `active` flag are never leaked. Response wrapped in the generic `PageResponseDto`.
- Authorization: only the **CLIENT** role may list dishes
  (`GET /api/v1/dishes` → CLIENT rule in `SecurityConfig`).
- Pagination stays framework-free in the domain via `PagedResult<T>`.

### Exceptions
| Exception | Message | HTTP |
|-----------|---------|------|
| `InvalidPaginationException` | The page number must be zero or greater and the page size must be greater than zero | 400 |
| `RestaurantNotFoundException` | The restaurant does not exist | 404 |
| (unauthenticated request) | Authentication is required | 401 |
| (authenticated, non-client role) | (Spring Security forbidden) | 403 |
