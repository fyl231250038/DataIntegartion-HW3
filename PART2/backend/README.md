# Department System (Group 2)

This module hosts the A/B/C department systems. One codebase, three profiles.

## Profiles

- A: SQL Server
- B: Oracle
- C: MySQL

## Run (examples)

```powershell
# A
mvn spring-boot:run -Dspring-boot.run.profiles=A

# B
mvn spring-boot:run -Dspring-boot.run.profiles=B

# C
mvn spring-boot:run -Dspring-boot.run.profiles=C
```
