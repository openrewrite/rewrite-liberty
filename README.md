<p align="center">
  <a href="https://docs.openrewrite.org">
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="https://github.com/openrewrite/rewrite/raw/main/doc/logo-oss-dark.svg">
      <source media="(prefers-color-scheme: light)" srcset="https://github.com/openrewrite/rewrite/raw/main/doc/logo-oss-light.svg">
      <img alt="OpenRewrite Logo" src="https://github.com/openrewrite/rewrite/raw/main/doc/logo-oss-light.svg" width='600px'>
    </picture>
  </a>
</p>

<div align="center">
  <h1>rewrite-liberty</h1>
</div>

<div align="center">

<!-- Keep the gap above this line, otherwise they won't render correctly! -->
[![ci](https://github.com/openrewrite/rewrite-liberty/actions/workflows/ci.yml/badge.svg)](https://github.com/openrewrite/rewrite-liberty/actions/workflows/ci.yml)
[![Apache 2.0](https://img.shields.io/github/license/openrewrite/rewrite-liberty.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/org.openrewrite.recipe/rewrite-liberty.svg)](https://mvnrepository.com/artifact/org.openrewrite.recipe/rewrite-liberty)
[![Revved up by Develocity](https://img.shields.io/badge/Revved%20up%20by-Develocity-06A0CE?logo=Gradle&labelColor=02303A)](https://ge.openrewrite.org/scans)
[![Contributing Guide](https://img.shields.io/badge/Contributing-Guide-informational)](https://github.com/openrewrite/.github/blob/main/CONTRIBUTING.md)
</div>

### What is this?

This project implements a [Rewrite module](https://github.com/openrewrite/rewrite) that performs common tasks when migrating to new version of [Open Liberty](https://openliberty.io/).

Browse [a selection of recipes available through this module in the recipe catalog](https://docs.openrewrite.org/recipes/java/liberty-1).

## How to use?

To get a curated list of recipes based on the migration issues present in your applications, consider downloading the [Migration Toolkit for Application Binaries](https://www.ibm.com/support/pages/node/6250913) and then running an analysis scan against your applications. Documentation on this scan and how to run it using the Migration Toolkit for Application Binaries can be found [here](https://www.ibm.com/docs/en/wamt?topic=binaries-detailed-migration-analysis-report). The report generated will contain the rewrite config necessary for your applications and their detected issues.

For more info on using OpenRewrite, see the full documentation at [docs.openrewrite.org](https://docs.openrewrite.org/).

## Contributing

We appreciate all types of contributions. See the [contributing guide](https://github.com/openrewrite/.github/blob/main/CONTRIBUTING.md) for detailed instructions on how to get started.
