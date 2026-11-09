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

## HU6 (Slice 2) — Restaurant ownership GET + Link-only employee POST

### GET /api/v1/restaurants/{restaurantId}/owner — Restaurant ownership query

- **Use case:** `RestaurantUseCase.findOwnerId`
- **Persistence port:** `IRestaurantPersistencePort.findRestaurantById` → `RestaurantJpaAdapter`
- **Authorization:** any authenticated user (all roles accepted; Spring Security `.authenticated()`).

#### Business rules / invariants
- Returns the `ownerId` and `restaurantId` of the given restaurant.
- The `restaurantId` path variable must reference an existing restaurant; otherwise 404.
- Unauthenticated access → 401 (enforced by `JwtAuthenticationEntryPoint`).

#### Exceptions
| Exception | Message | HTTP |
|-----------|---------|------|
| `RestaurantNotFoundException` | The restaurant does not exist | 404 |
| (unauthenticated request) | Authentication is required | 401 |

---

### POST /api/v1/restaurants/{restaurantId}/employees — Link-only employee registration

> **Transitional note (Slice 2):** Employee account creation has moved to `users-service` (Slice 3).
> This endpoint only registers the `employee_restaurant` link; the caller must have already
> created the employee user via `users-service`.

- **Use case:** `EmployeeUseCase.registerEmployee`
- **Cross-aggregate port:** `IRestaurantPersistencePort.findRestaurantById` (checks existence and ownership).
- **Persistence port:** `IEmployeePersistencePort.saveEmployeeRestaurant` + `existsLink`
  → `EmployeeRestaurantJpaAdapter`.

#### Business rules / invariants
- Only the **OWNER of the target restaurant** may link an employee:
  - `SecurityConfig` gates `POST /api/v1/restaurants/*/employees` to role **OWNER**;
  - the use case additionally checks that the authenticated caller owns that specific restaurant
    (`restaurant.ownerId == requesterOwnerId`), otherwise `NotRestaurantOwnerException` (403).
- Request body: `{ "employeeId": "<uuid>" }`. The `employeeId` must reference an existing user in
  the users-service with the **EMPLOYEE** role (enforced by users-service; not re-validated here).
- The `restaurantId` path variable must reference an existing restaurant; otherwise 404.
- Duplicate links (same `employeeId` and same `restaurantId`) are rejected with 409.

#### Exceptions
| Exception | Message | HTTP |
|-----------|---------|------|
| `RestaurantNotFoundException` | The restaurant does not exist | 404 |
| `NotRestaurantOwnerException` | This action can only be performed by the owner of the restaurant | 403 |
| `EmployeeLinkAlreadyExistsException` | The employee is already linked to this restaurant | 409 |
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

---

## HU11 — Place order

- **Endpoint:** `POST /api/v1/orders`
- **Use case:** `OrderUseCase.placeOrder`
- **Validator:** `OrderValidator.validateNewOrder` (pure: required fields and quantities only)
- **Persistence port:** `IOrderPersistencePort.saveOrder` / `existsActiveOrderByClient` → `OrderJpaAdapter`
  (persists the order with its items via a unidirectional `@OneToMany` on `order_items`; the active
  check uses the derived query `existsByClientIdAndStatusIn`).
- **Cross-aggregate ports:** `IRestaurantPersistencePort.findRestaurantById` (restaurant must exist)
  and `IDishPersistencePort.findDishById` (each dish must exist and belong to the restaurant).

### Business rules / invariants
- An order consists of a **list of dishes of a single restaurant**: the order carries `restaurantId`
  and every dish referenced in its items must belong to that same restaurant; otherwise the order is
  rejected (`DishNotInRestaurantException`, 400).
- Every order must specify the **restaurant**, the **chosen dishes** and the **quantity** of each.
  The item list cannot be empty (`EmptyOrderException`, 400), each item requires a `dishId` and a
  `quantity`, and every `quantity` must be **greater than zero** (`InvalidQuantityException`, 400).
- Each referenced dish must exist; a missing dish is rejected (`DishNotFoundException`, 404).
- Immediately after being received, the order is persisted with status **`PENDING`** (never trusted
  from the request).
- The **client** is taken from the JWT (`@AuthenticationPrincipal`), never from the body.
- A client may place a new order **only if they have no order in progress** — i.e. no order in
  `PENDING`, `IN_PREPARATION` or `READY`; otherwise `ClientHasActiveOrderException` (409).
- Authorization: only the **CLIENT** role may place orders
  (`POST /api/v1/orders` → CLIENT rule in `SecurityConfig`).
- The remaining lifecycle transitions (IN_PREPARATION / READY / DELIVERED / CANCELLED) are **not**
  implemented in HU11 — only creation in `PENDING`.

### Exceptions
| Exception | Message | HTTP |
|-----------|---------|------|
| `RequiredFieldException` | All fields are required | 400 |
| `EmptyOrderException` | An order must contain at least one dish | 400 |
| `InvalidQuantityException` | Each dish quantity must be greater than zero | 400 |
| `DishNotInRestaurantException` | All dishes in an order must belong to the specified restaurant | 400 |
| `RestaurantNotFoundException` | The restaurant does not exist | 404 |
| `DishNotFoundException` | The dish does not exist | 404 |
| `ClientHasActiveOrderException` | The client already has an order in progress | 409 |
| (unauthenticated request) | Authentication is required | 401 |
| (authenticated, non-client role) | (Spring Security forbidden) | 403 |

## HU12 — List orders by status

- **Role:** Employee
- **Endpoint:** `GET /api/v1/orders?status={status}&page={page}&size={size}`
- **Use case:** `OrderUseCase.listOrdersByStatus`
- **Validators:** `PaginationValidator.validate` (page/size) and `OrderValidator.parseStatus`
  (status string → `OrderStatus`, case-insensitive, trimmed).
- **Persistence ports:**
  - `IEmployeePersistencePort.findRestaurantIdByEmployee` → `EmployeeRestaurantJpaAdapter`
    (derived query `findByEmployeeId` on the `employee_restaurant` link created in HU6).
  - `IOrderPersistencePort.findOrdersByRestaurantAndStatus` → `OrderJpaAdapter`
    (derived query `findByRestaurantIdAndStatus` with Spring Data `Pageable`).

### Business rules / invariants
- The service returns the orders of a restaurant **filtered by status** (acceptance criterion 1):
  the `status` query parameter is **required** and must match an `OrderStatus`
  (`PENDING`, `IN_PREPARATION`, `READY`, `DELIVERED`, `CANCELLED`).
- The listing is **paginated** and the page size is caller-specified (criterion 2): `page`
  (zero-based, default `0`) and `size` (default `10`) are validated by `PaginationValidator`.
- Every returned order exposes **all its fields** (criterion 3): id, client id, restaurant id,
  status and the list of items (`OrderResponseDto`).
- An employee can only list the orders of the **restaurant they belong to** (criterion 4): the
  restaurant is resolved from the authenticated employee's `employee_restaurant` link, never taken
  from the request. The `restaurantId` is never a client-supplied parameter.
- The **employee** is taken from the JWT (`@AuthenticationPrincipal`), never from the body/query.
- Authorization: only the **EMPLOYEE** role may list orders
  (`GET /api/v1/orders` → EMPLOYEE rule in `SecurityConfig`).
- Read-only operation: the handler runs in a `@Transactional(readOnly = true)` boundary.

### Exceptions
| Exception | Message | HTTP |
|-----------|---------|------|
| `InvalidOrderStatusException` | The provided order status is invalid | 400 |
| `InvalidPaginationException` | The page number must be zero or greater and the page size must be greater than zero | 400 |
| `EmployeeNotLinkedToRestaurantException` | The employee is not assigned to any restaurant | 404 |
| (unauthenticated request) | Authentication is required | 401 |
| (authenticated, non-employee role) | (Spring Security forbidden) | 403 |

## HU13 — Assign order and move it to IN_PREPARATION

- **Role:** Employee
- **Endpoint:** `PATCH /api/v1/orders/{orderId}/assign`
- **Use case:** `OrderUseCase.assignOrder`
- **Validator:** `OrderValidator.validateAssignable` (pure state-transition rule: the order must be
  in `PENDING`).
- **Persistence ports:**
  - `IOrderPersistencePort.findOrderById` → `OrderJpaAdapter` (`JpaRepository.findById`).
  - `IEmployeePersistencePort.findRestaurantIdByEmployee` → `EmployeeRestaurantJpaAdapter`
    (reuses the `employee_restaurant` link from HU6).
  - `IOrderPersistencePort.saveOrder` → `OrderJpaAdapter` (persists the updated order).

### Business rules / invariants
- Assigning an order means writing the authenticated employee's id into the order's
  **`assignedEmployeeId`** field (acceptance criterion 5). The employee is taken from the JWT
  (`@AuthenticationPrincipal`), never from the body.
- Assigning also performs the lifecycle transition **`PENDING → IN_PREPARATION`**. The order must be
  in `PENDING`; any other status raises `OrderNotAssignableException` (409).
- An employee can only operate on orders of the **restaurant they belong to** (standing rule shared
  with HU12): the order's `restaurantId` must match the restaurant resolved from the employee's
  `employee_restaurant` link, otherwise `OrderNotFromEmployeeRestaurantException` (403).
- If the order does not exist, `OrderNotFoundException` (404).
- Authorization: only the **EMPLOYEE** role may assign orders
  (`PATCH /api/v1/orders/*/assign` → EMPLOYEE rule in `SecurityConfig`).

### Exceptions
| Exception | Message | HTTP |
|-----------|---------|------|
| `OrderNotFoundException` | The order does not exist | 404 |
| `EmployeeNotLinkedToRestaurantException` | The employee is not assigned to any restaurant | 404 |
| `OrderNotFromEmployeeRestaurantException` | The order does not belong to the employee's restaurant | 403 |
| `OrderNotAssignableException` | Only orders in PENDING status can be assigned and moved to preparation | 409 |
| (unauthenticated request) | Authentication is required | 401 |
| (authenticated, non-employee role) | (Spring Security forbidden) | 403 |
