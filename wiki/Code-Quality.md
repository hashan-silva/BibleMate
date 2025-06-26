## Code Quality

Pushes and pull requests to the **dev** and **master** branches trigger a [SonarCloud](https://sonarcloud.io) analysis. Configure the `SONAR_TOKEN` secret in your repository to enable the workflow.

The SonarCloud quality gate status is posted back on each pull request.

Android Lint checks run on pull requests targeting the **dev** or **master** branch and comment lint issues with a link to the full report.
