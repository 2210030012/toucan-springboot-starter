# Transaction Starter Project

This is the starter project for the Customer Transactions exercise.

## Before you start

The first thing you should do after cloning the repository is:

### Linux / macOS

```bash
./mvnw clean test
```

### Windows

```bat
mvnw.cmd clean test
```

The sample test should pass before you begin implementing the exercise.

## What is already provided

- Java 17
- Spring Boot
- Maven wrapper
- Spring Web
- Spring Data JPA
- H2 embedded database
- JUnit / Spring Boot Test
- A sample REST endpoint: `GET /api/sample`
- A sample test that loads the Spring context


## Exercise

Implement these four operations:

1. Create transaction
2. Get transaction
3. Update transaction status
4. Get all transactions for a customer


You may change the surrounding design if you believe your solution is better.

## Transaction fields

Every transaction contains:

- Transaction ID
- Customer ID
- Amount
- Currency
- Transaction Type
- Transaction Status

### Validation rules

Define what makes a transaction valid. At minimum, consider:

- Transaction ID
- Customer ID
- Amount
- Currency
- Transaction type
- Initial status

Also explain any business validation you add beyond the annotations already supplied.

## API Endpoints

### Create Transaction

`POST /api/transactions`

Creates a new transaction.

A new transaction must have `PENDING` status.

Example request:

```json
{
  "transactionId": "TXN001",
  "customerId": "CUST001",
  "amount": 1500.00,
  "currency": "INR",
  "transactionType": "PAYMENT",
  "status": "PENDING"
}
```

### Get Transaction

`GET /api/transactions/{transactionId}`

Retrieves a transaction using its transaction ID.

If the requested transaction does not exist, the service returns an appropriate error message.

Example:

`GET /api/transactions/TXN999`

For a non-existent transaction, the service throws an `IllegalArgumentException` with the message:

```text
Transaction with ID TXN999 not found
```

### Update Transaction Status

`PUT /api/transactions/{transactionId}/status`

Updates the status of an existing transaction.

Only transactions currently in `PENDING` status can be updated.

A `PENDING` transaction can be changed to:

- `COMPLETED`
- `FAILED`
- `CANCELLED`

Example:

`PUT /api/transactions/TXN001/status`

Request body:

```json
{
  "status": "COMPLETED"
}
```

### Get All Transactions for a Customer

`GET /api/transactions/customer/{customerId}`

Retrieves all transactions belonging to a specific customer.

Example:

`GET /api/transactions/customer/CUST001`

The endpoint returns all transactions associated with the specified customer.

## Implemented Validation Rules

- Transaction ID is required and must not be blank.
- Customer ID is required and must not be blank.
- Amount is required and must be greater than zero.
- Currency is required and must not be blank.
- Transaction type is required.
- Transaction status is required.
- A new transaction must start with PENDING status.
- Transaction ID must be unique.
- Currency is stored in uppercase for consistency.

### Business Validation

In addition to the validation annotations on the Transaction entity,
the service layer performs business validation.

A transaction cannot be created if its transaction ID already exists.

A new transaction can only be created with PENDING status.

## Status Transition Rules

A new transaction must start with PENDING status.

Only a transaction currently in PENDING status can have its status updated.

A PENDING transaction can move to:

- COMPLETED
- FAILED
- CANCELLED

Transactions that are already COMPLETED, FAILED, or CANCELLED
cannot be changed to another status.

## Error Handling

A global exception handler is provided using `@RestControllerAdvice`.

- `IllegalArgumentException` returns HTTP 400 BAD REQUEST.
- `IllegalStateException` returns HTTP 409 CONFLICT.

The response includes an error type and an explanatory message.

## Testing

The project contains 8 meaningful transaction service tests covering:

- Successful transaction creation
- Successful transaction retrieval
- Successful status update
- Customer transaction lookup
- Duplicate transaction ID rejection
- Invalid amount rejection
- Rejection of status updates from completed transactions
- Rejection when retrieving a non-existent transaction

The complete test suite currently contains 9 tests, and all 9 tests pass.

## Known Limitations

- The application uses the embedded H2 database, so data is not persisted across application restarts.
- The current test suite focuses mainly on the service layer and application context rather than full controller-level integration testing.
- API documentation such as OpenAPI/Swagger has not been added because it was not required for the exercise.

## What I Would Improve With More Time

- Add controller-level integration tests for all API endpoints.
- Add more detailed validation for fields such as currency and transaction type based on business requirements.
- Improve API documentation with OpenAPI/Swagger.
- Add more comprehensive error-response tests.

## AI Assistance Disclosure

AI assistance was used during development to support implementation,
debugging, test creation, documentation, and understanding the project
requirements.

The final implementation was reviewed and tested by the developer, who is
responsible for the submitted code and its behavior.

