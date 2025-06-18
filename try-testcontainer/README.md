# Profile API Example Usage

This document provides example usage of the Profile API using `curl` commands. Replace `<BASE_URL>` with your server's base URL (e.g., `http://localhost:8080`).

## Get All Profiles

```
curl -X GET <BASE_URL>/profiles
```

## Get Profile by ID

```
curl -X GET <BASE_URL>/profiles/{id}
```
Replace `{id}` with the profile's ID.

## Create a New Profile

```
curl -X POST <BASE_URL>/profiles \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "email": "jane@example.com"
  }'
```

## Update a Profile

```
curl -X PUT <BASE_URL>/profiles/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane.smith@example.com"
  }'
```
Replace `{id}` with the profile's ID.

## Delete a Profile

```
curl -X DELETE <BASE_URL>/profiles/{id}
```
Replace `{id}` with the profile's ID.

---

For more details, refer to the source code or contact the maintainer.

