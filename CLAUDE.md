# CLAUDE.md

Context for Claude (and humans) working on this repo. Keep it current: update
the **Current state** section whenever a PR lands.

## Project

A shopping list web app: Scala 3 compiled to JavaScript with Scala.js, UI in
Laminar, bundled with Vite. Long-term goal (from the "ShoppingListV2" project):
a Progressive Web App — a simple list of products to buy while shopping.

- Repo: https://github.com/avelx/laminar-example (default branch `main`)
- Local checkout: `~/devcore/Claude/training`

## Stack and versions

| Component | Version | Where |
|---|---|---|
| Scala | 3.3.6 (LTS) | `build.sbt` |
| sbt | 1.10.11 | `project/build.properties` |
| Scala.js | 1.19.0 | `project/plugins.sbt` |
| Laminar | 17.2.1 | `build.sbt` |
| munit (tests) | 1.1.1 | `build.sbt` |
| scalajs-env-jsdom-nodejs | 1.1.0 | `project/plugins.sbt` |
| Vite | 7.3.x | `package.json` |
| @scala-js/vite-plugin-scalajs | 1.1.x | `package.json` |
| jsdom | 26.1.x | `package.json` |

Version constraints:
- The Vite plugin declares support for Vite 4.1.4–7 only, so don't upgrade to Vite 8.
- jsdom 26 works with Node 18 and newer; jsdom 30 needs Node 22 or newer.

## Layout

```
build.sbt                 sbt build; main = ES module, tests = plain script on jsdom
project/                  sbt version + Scala.js plugin + jsdom test env
src/main/scala/example/
  Main.scala              @main entry point; App root component
  ShoppingList.scala      ShoppingList component + Item model
src/test/scala/example/
  ShoppingListSpec.scala  munit integration test, mounts ShoppingList in jsdom
index.html, main.js       Vite entry; main.js imports "scalajs:main.js"
style.css                 all styles
vite.config.js            wires Scala.js output into Vite
```

## Commands

```sh
npm install          # JS deps (Vite, plugin, jsdom); needed before tests too
npm run dev          # dev server on http://localhost:5173 (Vite calls sbt itself)
sbt ~fastLinkJS      # optional, in a second terminal, for faster reloads
sbt test             # or: npm test
npm run build        # production build (fullLinkJS) into dist/
```

## Architecture notes

- Components are `object X: def apply(): HtmlElement`. State lives in `Var`s
  created inside `apply()`, so each mounted instance is independent. Tests rely
  on this to get a fresh list per test.
- `ShoppingList` keeps `itemsVar: Var[List[Item]]` and `inputVar: Var[String]`.
  The input is `controlled(...)`. The list renders with `split(_.id)`, keyed by
  `Item.id`.
- There is no persistence yet, so the list resets on page reload.

## Testing notes

- Tests use `JSDOMNodeJSEnv`. The jsdom env cannot load ES modules, so `build.sbt`
  overrides the Test settings:
  - `ModuleKind.NoModule`
  - `ModuleSplitStyle.FewestModules`
  - `scalaJSUseMainModuleInitializer := false`, so `@main` doesn't run during tests.
  Keep these overrides when touching the linker config.
- `jsdom` must be in `node_modules` (`npm install`) or the tests can't start.
- Drive the UI through real DOM events:
  - Set `input.value` and dispatch an `input` event.
  - Dispatch `submit` on the form.
  - Call `.click()` on buttons.
  Then assert on the rendered DOM. Airstream updates are synchronous, so no waiting is needed.

## Laminar gotchas

- `<section>` is `sectionTag(...)` in Laminar, not `section(...)`. Other tags
  whose names clash with attributes or Scala names may follow the same
  `xxxTag` pattern. If you get a "Not found" error, look up the name in Laminar's API.
- Import with `import com.raquo.laminar.api.L.{*, given}`.

## Conventions

- One branch per change (`refactor/...`, `test/...`, `docs/...`, `feat/...`),
  merged into `main` through a PR.
- Commit messages use a conventional prefix: `feat:`, `refactor:`, `test:`, `docs:`, `fix:`.
- After changing dependencies in `package.json`, commit the updated `package-lock.json`.
- `node_modules` must be installed on the Mac itself. A copy installed on Linux
  carries the wrong native binaries.

## Working with Claude (cloud sessions)

- Claude sessions can't reach Maven Central (`repo1.maven.org`), so Claude
  can't run sbt and cannot compile or test there. Changes are checked by running
  `sbt test` / `npm run dev` on the Mac. npm and GitHub do work.
- Claude pushes branches to `avelx/laminar-example` and the owner merges the PRs.

## Current state (2026-11-04)

- `main` @ `ec47aa5`: PRs #1 and #2 are merged.
  - #1 moved `ShoppingList` + `Item` into `ShoppingList.scala`.
  - #2 added `ShoppingListSpec`.
- Build and all 5 tests pass locally. Last run was `sbt clean compile test` on
  2026-11-03, on macOS with JDK 25 and sbt from MacPorts.
- The app has a single "Shopping list" page: add an item (trimmed, blanks
  ignored), remove an item, and see a count summary.
- Not done yet:
  - PWA features (manifest, service worker, offline support)
  - Persisting items (e.g. localStorage)
  - Editing an item, quantities, marking an item as bought
