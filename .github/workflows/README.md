# GitHub Actions Workflows

This directory contains CI/CD workflows for the Weather Platform project.

## Workflows

### 1. CI/CD Pipeline (`ci.yml`)

Main continuous integration and deployment pipeline that runs on pushes and pull requests to `main` and `develop` branches.

**Jobs:**
- **backend-build**: Builds Java backend, runs tests (unit + integration), generates coverage reports
- **frontend-build**: Builds React frontend, runs tests and linter
- **docker-build**: Builds and pushes Docker images (only on `main` branch)
- **code-quality**: Runs security scans and quality checks
- **summary**: Generates build summary

**Badges:**
```markdown
![CI/CD Pipeline](https://github.com/YOUR_USERNAME/weather/actions/workflows/ci.yml/badge.svg)
```

### 2. Pull Request Checks (`pr-checks.yml`)

Detailed checks that run on pull requests with additional validations.

**Jobs:**
- **pr-info**: Displays PR information
- **backend-tests**: Runs all backend tests including integration tests
- **frontend-tests**: Runs frontend tests with coverage
- **code-style**: Checks code formatting and style
- **dependency-review**: Reviews dependency changes for security issues
- **pr-size-check**: Checks PR size and provides feedback
- **pr-summary**: Generates comprehensive PR check summary

**Features:**
- Automatic PR comments with test results
- Code coverage reports on PRs
- PR size analysis
- Dependency security review

**Badges:**
```markdown
![PR Checks](https://github.com/YOUR_USERNAME/weather/actions/workflows/pr-checks.yml/badge.svg)
```

## Secrets Required

Configure these secrets in your GitHub repository settings:

### Docker Hub (for `ci.yml`)
- `DOCKER_USERNAME`: Your Docker Hub username
- `DOCKER_PASSWORD`: Your Docker Hub password or access token

### SonarCloud (optional, for `ci.yml`)
- `SONAR_TOKEN`: SonarCloud authentication token

## Test Reports

### Backend Tests
- **Location**: `backend/build/reports/tests/test/`
- **Coverage**: `backend/build/reports/jacoco/test/html/`
- **Format**: HTML + XML (JUnit & JaCoCo)

### Frontend Tests
- **Location**: `frontend/coverage/`
- **Format**: Istanbul/NYC coverage reports

## Integration Tests

Integration tests are automatically run with Testcontainers, which provides:
- PostgreSQL 16 (Alpine)
- Redis 7 (Alpine)

Tests covered:
- ✅ WeatherController (5 tests)
- ✅ ForecastController (9 tests)
- ✅ HistoryController (8 tests)
- ✅ SubscriptionController (10 tests)
- ✅ AllControllers (6 tests)

**Total: 40 integration tests**

## Workflow Triggers

### `ci.yml` triggers on:
```yaml
push:
  branches: [ main, develop ]
pull_request:
  branches: [ main, develop ]
```

### `pr-checks.yml` triggers on:
```yaml
pull_request:
  branches: [ main, develop ]
  types: [ opened, synchronize, reopened ]
```

## Build Matrix

| Component | Runtime | Version | Tests |
|-----------|---------|---------|-------|
| Backend   | Java    | 21      | JUnit 5 + Testcontainers |
| Frontend  | Node.js | 20      | Vitest |
| Database  | PostgreSQL | 16   | Alpine |
| Cache     | Redis   | 7       | Alpine |

## Artifacts

Artifacts are retained for 7-30 days depending on type:

| Artifact | Retention | Description |
|----------|-----------|-------------|
| backend-jar | 7 days | Compiled JAR file |
| frontend-dist | 7 days | Built frontend assets |
| backend-test-results | 30 days | Test reports (HTML) |
| backend-coverage-report | 30 days | Coverage reports (HTML) |
| frontend-coverage | 7 days | Frontend coverage |

## Docker Images

Images are built and pushed to Docker Hub on successful builds to `main`:

- `YOUR_USERNAME/weather-backend:latest`
- `YOUR_USERNAME/weather-backend:${git-sha}`
- `YOUR_USERNAME/weather-frontend:latest`
- `YOUR_USERNAME/weather-frontend:${git-sha}`

## Local Testing

To test workflows locally, use [act](https://github.com/nektos/act):

```bash
# Install act
# macOS
brew install act

# Linux
curl https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash

# Run workflow
act -j backend-build
act -j frontend-build

# Run specific event
act pull_request
```

## Performance

Typical build times:
- Backend build & test: ~3-5 minutes
- Frontend build & test: ~2-3 minutes
- Docker build: ~3-4 minutes
- **Total CI time**: ~8-12 minutes

## Troubleshooting

### Build fails with "Permission denied" on gradlew
The workflow includes `chmod +x gradlew` but if it still fails, ensure the file has execute permissions in git:
```bash
git update-index --chmod=+x backend/gradlew
```

### Testcontainers timeout
Increase timeout in test configuration or use GitHub's larger runners.

### Docker build fails
Check that Docker Hub credentials are correctly set in repository secrets.

## Contributing

When adding new workflows:
1. Test locally with `act` if possible
2. Use descriptive job names
3. Add appropriate badges to README
4. Document any new secrets required
5. Update this README with workflow details
