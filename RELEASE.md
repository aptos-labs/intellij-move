# Release process

To publish a stable release, create and publish a GitHub release in this repository.

Update `codeVersion` in `build.gradle.kts` to the new version.
The final plugin version is built as `<codeVersion>.<shortPlatformVersion>`.
For example, with `codeVersion = "1.56.0"`, CI publishes `1.56.0.253` and `1.56.0.261`.

Create a new GitHub release from the commit you want to publish. Use the `codeVersion` as the release tag,
for example `v1.56.0`. When the release is published, CI uploads the plugin automatically to JetBrains
Marketplace.

The release workflow is `.github/workflows/publish-stable-on-release.yml`.

## JetBrains approval process

All plugin uploads to the Marketplace are manually approved by JetBrains staff, so expect the release 
to actually go public the next business day.

## Supported platforms

JetBrains releases major versions 3 times a year, denoted as YEAR.NUM. We support latest 2 releases, 
stable and previous stable. Right now it's `2025.3` (`253` in code) and `2026.1` (`261`). 

For every major release, there's a dedicated `gradle-RELEASE.properties` file. It's pretty small, 
new ones are copied from the old one with some intuitive changes.

## Publishing token

The JetBrains Marketplace publishing token is saved as the `JB_PUB_TOKEN` repository secret.
Gradle reads it through the `JB_PUB_TOKEN` environment variable and passes it to the IntelliJ Platform
Gradle plugin.

See https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html for more.
