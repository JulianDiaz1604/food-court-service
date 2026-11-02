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
- `nit`: digits only.
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
| `InvalidNitException` | The NIT must contain digits only | 400 |
| `InvalidPhoneFormatException` | The phone must be numeric, may start with '+', and have at most 13 characters | 400 |
| `InvalidOwnerException` | The provided owner id does not belong to a registered owner | 400 |

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
| `NotRestaurantOwnerException` | The dish can only be created by the owner of the restaurant | 403 |
