# GitHub Actions Setup Guide

This guide will help you configure GitHub Actions for your Weather Platform project.

## Prerequisites

- GitHub repository with this project
- Docker Hub account (for image publishing)
- GitHub account with admin access to the repository

## Step 1: Configure Repository Secrets

Navigate to your repository settings: `Settings` → `Secrets and variables` → `Actions`

### Required Secrets

#### For Docker Hub Publishing (ci.yml)

1. **DOCKER_USERNAME**
   - Your Docker Hub username
   - Example: `johndoe`

2. **DOCKER_PASSWORD**
   - Docker Hub password or Personal Access Token (PAT)
   - Recommended: Use PAT for better security
   - Create PAT at: https://hub.docker.com/settings/security
   - Permissions needed: Read, Write, Delete

### Optional Secrets

#### For SonarCloud (code quality)

3. **SONAR_TOKEN**
   - SonarCloud authentication token
   - Get from: https://sonarcloud.io/account/security
   - Currently disabled in workflow (set `if: true` to enable)

#### For Codecov (coverage)

4. **CODECOV_TOKEN**
   - Codecov upload token
   - Get from: https://codecov.io/
   - Optional: Can work without token for public repos

## Step 2: Update Workflow Files

Replace `YOUR_USERNAME` in the following files with your GitHub username:

### Files to update:

1. **README.md** (line 12-14)
```markdown
[![CI/CD Pipeline](https://github.com/YOUR_USERNAME/weather/actions/workflows/ci.yml/badge.svg)]
```

2. **.github/workflows/ci.yml** (line 155-156, 167-168)
```yaml
${{ secrets.DOCKER_USERNAME }}/weather-backend:latest
${{ secrets.DOCKER_USERNAME }}/weather-frontend:latest
```

### Quick find and replace:

```bash
# Linux/Mac
find .github README.md -type f -exec sed -i 's/YOUR_USERNAME/your-actual-username/g' {} +

# Windows PowerShell
Get-ChildItem -Path .github,README.md -Recurse -File | ForEach-Object {
    (Get-Content $_.FullName) -replace 'YOUR_USERNAME', 'your-actual-username' | Set-Content $_.FullName
}
```

## Step 3: Enable GitHub Actions

1. Go to your repository
2. Click on `Actions` tab
3. If Actions are disabled, click `I understand my workflows, go ahead and enable them`

## Step 4: Test the Setup

### Test with a Pull Request

1. Create a new branch:
```bash
git checkout -b test-ci
```

2. Make a small change (e.g., update README):
```bash
echo "# Test CI" >> TEST.md
git add TEST.md
git commit -m "Test: CI workflow"
git push origin test-ci
```

3. Create a Pull Request on GitHub
4. Check that `pr-checks.yml` workflow runs
5. Verify all checks pass

### Test Main CI Pipeline

1. Merge the PR to `main` or `develop`
2. Check that `ci.yml` workflow runs
3. Verify Docker images are built (on `main` only)

## Step 5: Configure Branch Protection (Recommended)

Protect your `main` and `develop` branches:

1. Go to `Settings` → `Branches`
2. Add branch protection rule for `main`:
   - ✅ Require a pull request before merging
   - ✅ Require status checks to pass before merging
   - Select checks: `Backend Tests (PR)`, `Frontend Tests`, `Code Style`
   - ✅ Require branches to be up to date before merging
   - ✅ Do not allow bypassing the above settings

3. Repeat for `develop` branch

## Step 6: Configure Notifications (Optional)

### Slack Integration

1. Install GitHub app in Slack workspace
2. Subscribe to repository: `/github subscribe owner/repo`
3. Customize notifications: `/github subscribe owner/repo workflows:{event:"push","pull_request"}`

### Email Notifications

1. Go to your GitHub settings
2. Navigate to `Notifications`
3. Configure notification preferences for Actions

## Workflow Behavior

### When workflows run:

| Event | ci.yml | pr-checks.yml |
|-------|--------|---------------|
| Push to main | ✅ (full + Docker) | ❌ |
| Push to develop | ✅ (full) | ❌ |
| Pull Request to main | ✅ (build only) | ✅ (full checks) |
| Pull Request to develop | ✅ (build only) | ✅ (full checks) |

### What each workflow does:

**ci.yml** (CI/CD Pipeline)
- Builds backend with Gradle
- Runs all tests (unit + integration)
- Builds frontend with Vite
- Generates coverage reports
- Builds Docker images (main branch only)
- Uploads artifacts

**pr-checks.yml** (Pull Request Checks)
- Runs all backend tests
- Runs frontend tests with coverage
- Checks code style
- Reviews dependencies
- Comments test results on PR
- Checks PR size

## Troubleshooting

### "Workflow run failed: no such file or directory gradlew"

**Solution**: Ensure gradlew has execute permissions:
```bash
git update-index --chmod=+x backend/gradlew
git commit -m "Fix: Add execute permission to gradlew"
git push
```

### "Docker login failed: unauthorized"

**Solution**: Check Docker Hub credentials:
1. Verify `DOCKER_USERNAME` is correct
2. Verify `DOCKER_PASSWORD` is a valid token
3. Try logging in manually: `docker login`

### "Tests failed: Could not connect to PostgreSQL"

**Solution**: This shouldn't happen as Testcontainers manages databases. If it does:
1. Check if Docker is available in GitHub Actions (it should be)
2. Increase timeout in test configuration
3. Check Testcontainers logs in workflow output

### "Coverage report not generated"

**Solution**:
1. Ensure JaCoCo plugin is in `build.gradle`
2. Run `./gradlew jacocoTestReport` locally to verify
3. Check that tests actually run before report generation

### "Docker Hub rate limit exceeded"

**Solution**:
1. Authenticate before pulling images (already done in workflow)
2. Use Docker Hub Pro account for higher limits
3. Use GitHub Container Registry instead

## Monitoring

### View Workflow Runs

1. Go to `Actions` tab in repository
2. Click on workflow name (e.g., "CI/CD Pipeline")
3. View run history, logs, and artifacts

### Download Artifacts

1. Go to completed workflow run
2. Scroll to bottom - "Artifacts" section
3. Download:
   - `backend-jar` - Built JAR file
   - `frontend-dist` - Built frontend
   - `backend-test-results` - Test reports
   - `backend-coverage-report` - Coverage reports

### Check Docker Images

```bash
# Pull latest image
docker pull YOUR_USERNAME/weather-backend:latest
docker pull YOUR_USERNAME/weather-frontend:latest

# Check image tags
curl https://hub.docker.com/v2/repositories/YOUR_USERNAME/weather-backend/tags/
```

## Performance Optimization

### Speed up builds:

1. **Enable caching** (already configured):
   - Gradle dependencies cached
   - npm dependencies cached
   - Docker layers cached

2. **Use matrix builds** for parallel execution:
```yaml
strategy:
  matrix:
    test-type: [unit, integration]
```

3. **Use self-hosted runners** for faster builds:
   - Set up: https://docs.github.com/en/actions/hosting-your-own-runners

## Cost Management

GitHub Actions is free for public repositories with these limits:
- ✅ Unlimited minutes
- ✅ Unlimited concurrent jobs (2,000)
- ✅ Unlimited storage for artifacts (90 days retention)

For private repositories:
- 2,000 minutes/month (free tier)
- $0.008/minute for additional usage
- Artifacts: 500MB storage included

## Next Steps

1. ✅ Configure all secrets
2. ✅ Update YOUR_USERNAME placeholders
3. ✅ Enable Actions
4. ✅ Test with a PR
5. ✅ Configure branch protection
6. ✅ Set up notifications
7. 🎉 Start developing with automated CI/CD!

## Additional Resources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Testcontainers Documentation](https://www.testcontainers.org/)
- [Docker Hub Documentation](https://docs.docker.com/docker-hub/)
- [JaCoCo Coverage](https://www.jacoco.org/jacoco/)

## Support

For issues related to:
- **Workflows**: Check `.github/workflows/README.md`
- **Tests**: Check `backend/TEST_COVERAGE.md`
- **General setup**: Create an issue in the repository
