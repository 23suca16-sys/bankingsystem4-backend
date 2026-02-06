# Banking-backend

Simple Spring Boot backend for the banking project.

## SonarCloud / SonarQube CI

Add the following repository secrets in GitHub to allow Sonar analysis to run in CI:

- `SONAR_ORGANIZATION`: your SonarCloud organization key
- `SONAR_TOKEN`: a Sonar analysis token (see SonarCloud/SonarQube account settings)

The included workflow ([.github/workflows/build.yml](.github/workflows/build.yml)) reads `SONAR_ORGANIZATION` and passes it to Maven as `-Dsonar.organization`.

If you prefer a local config, set the environment variables and run:

```bash
export SONAR_ORGANIZATION=your_org_key
export SONAR_TOKEN=your_token
mvn -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.projectKey=23suca16-sys_bankingsystem4-backend \
  -Dsonar.organization=$SONAR_ORGANIZATION
```
"# Banking-backend" 
"# banks-backend1" 
