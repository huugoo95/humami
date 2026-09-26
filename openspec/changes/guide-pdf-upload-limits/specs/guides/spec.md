# guides

## ADDED Requirements

### Requirement: Guide document uploads support the documented asset size

The system SHALL accept authenticated guide document uploads up to 10 MB through the production HTTPS proxy and backend multipart handling. The multipart request/proxy limit SHALL include safe overhead beyond the file limit.

#### Scenario: Private guide PDF below limit

- **WHEN** an editor uploads a valid PDF smaller than 10 MB to a guide document endpoint
- **THEN** Nginx forwards the request and Spring Boot processes it without a size-limit 413 response
