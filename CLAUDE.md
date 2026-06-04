# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

Rhea is a **codbex "Edition"** — a thin distribution that packages [Eclipse Dirigible](https://github.com/eclipse/dirigible) into a runnable Spring Boot application. It contains almost no business logic of its own; it assembles Dirigible components (entity/forms modeling, IDE, engines, editors) via Maven dependencies and applies codbex branding plus a few UI overrides. Rhea's focus is **Entities, Forms and Reports modeling** for Model Driven Architecture development.

Rhea is one of a family of codbex Editions ([codbex.com/products](https://www.codbex.com/products/)), each a different curated slice of the same platform — e.g. **Atlas** (all-in-one), **Helios** (API development), **Hades** (database management), **Oceanus** (CMS/document management), **Hyperion** (BPM), **Iapetus** (integration/ETL), **Kronos** (XS/ABAP compatibility), **Phoebe** (Airflow-based data workflows). They differ almost entirely in *which* components their `application/pom.xml` includes; the structure and build conventions described below are shared across the family.

Inheritance chain: `codbex-platform-parent` (the `com.codbex.platform` parent POM, currently 12.89.0) → `codbex-rhea-parent` (root `pom.xml`) → modules. Most build behavior, Maven profiles, plugin config, and dependency versions live in the **parent POM, not this repo** — when a build profile or plugin isn't defined here, it is inherited from the platform parent.

## Build & run

All commands run from the repo root (`$GIT_REPO_FOLDER`).

```shell
# Fast build (skips heavy phases) — produces application/target/codbex-rhea-*.jar
mvn -T 1C clean install -P quick-build

# Run the standalone jar (the --add-opens flags are required on JDK 21)
java \
    --add-opens=java.base/java.lang=ALL-UNNAMED \
    --add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
    --add-opens=java.base/java.nio=ALL-UNNAMED \
    -jar application/target/codbex-rhea-*.jar
# debug: add -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000
```

App serves on **port 80**. UI at http://localhost:80 (login `admin` / `admin`), REST/Swagger at http://localhost/swagger-ui/index.html.

### Tests

```shell
mvn clean install -P unit-tests          # unit tests only
mvn clean install -P integration-tests   # Selenium/browser integration tests
mvn clean install -P tests               # all tests
```

Run a single test with the standard Surefire/Failsafe selector, e.g.:
```shell
mvn test -P unit-tests -pl application -Dtest=RheaApplicationTest
mvn verify -P integration-tests -pl integration-tests -Dit.test=HomePageIT
```

### Formatting

```shell
mvn verify -P format     # formats Java (and other) sources; run before committing
```

### Spring profiles

The app activates `common,app-default` by default (`application.properties`). To enable a specific Dirigible deployment profile you must add `common` and `app-default` explicitly, e.g. `SPRING_PROFILES_ACTIVE=common,snowflake,app-default`.

## Module layout

- **`application/`** — the deliverable. `RheaApplication.java` is the Spring Boot entrypoint; it scans `org.eclipse.dirigible` and disables the default JDBC/JPA autoconfiguration (Dirigible manages its own datasources). The bulk of `application/pom.xml` is a curated list of `dirigible-components-*` dependencies that selects exactly which engines, editors, IDE views, menus, perspectives, and templates ship in this edition. **Adding/removing a platform feature = adding/removing a dependency here**, often paired with an `<exclusion>` to swap a Dirigible default for a codbex override (see below).
- **`branding/`** — codbex branding resources (logo, favicon) served as a Dirigible project under `rhea-branding`. Branding values are wired in `application/src/main/resources/dirigible.properties`.
- **`components/ui/`** — local overrides of stock Dirigible UI components, currently `menu-help` and `view-welcome`. The pattern: the application POM **excludes** the upstream component (e.g. `dirigible-components-ui-menu-help`, `dirigible-components-ui-view-welcome`) and depends on the codbex-rhea replacement instead. These are Dirigible "projects" (resources under `META-INF/dirigible/<guid>/`) using `.extension` files that hook into platform extension points like `platform-menus`.
- **`integration-tests/`** — browser-based integration tests extending `UserInterfaceIntegrationTest` from `dirigible-tests-framework`. Test classes end in `IT` and drive the running IDE via the `ide`/`browser` helpers.
- **`helm/otc/`** — Helm chart for Kubernetes deployment (versioned separately from the app).

## How customization works (the mental model)

There is no application Java code to edit for most feature work. To change what the platform does, you operate at the **packaging layer**:

1. **Include/exclude Dirigible components** in `application/pom.xml`.
2. **Override a stock UI component** by excluding the upstream artifact and adding a replacement module under `components/ui/` that ships Dirigible resources (`project.json`, `.extension`, JS/HTML) under `META-INF/dirigible/`.
3. **Configure runtime behavior** via `dirigible.properties` (branding, multi-tenancy, product metadata — values like `${project.version}` are filtered at build time) and the `application*.properties` files.

When you need to understand actual feature behavior (an editor, an engine, the entity modeler), the source lives in the **Dirigible dependencies**, not this repo.

## Docker

```shell
cd application && docker build . --tag ghcr.io/codbex/codbex-rhea:latest
```
The image (`application/Dockerfile`) is based on `amazoncorretto:21-alpine` and bundles `nodejs`/`npm`, `esbuild`, and `typescript` because the TypeScript engine compiles project sources at runtime.
