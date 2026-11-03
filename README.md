# Laminar example

A minimal Scala 3 + Scala.js + Laminar app, bundled with Vite.

## Prerequisites

- JDK 11+ (17 or 21 recommended)
- sbt (`brew install sbt`, or via Coursier: `cs setup`)
- Node.js 18+

## Run

```sh
npm install
npm run dev        # starts Vite; it calls sbt to compile Scala.js automatically
```

Open http://localhost:5173. For faster reloads, keep `sbt ~fastLinkJS` running in a second terminal.

## Production build

```sh
npm run build      # uses fullLinkJS, output in dist/
npm run preview
```

## Layout

- `build.sbt`, `project/` – sbt build with the Scala.js plugin (ES module output)
- `src/main/scala/example/Main.scala` – entry point and root `App` component
- `src/main/scala/example/ShoppingList.scala` – the shopping list component and `Item` model
- `index.html`, `main.js`, `style.css` – Vite entry point
- `vite.config.js` – wires Scala.js output into Vite via `@scala-js/vite-plugin-scalajs`

## Versions

Scala 3.3.6 (LTS) · Scala.js 1.19.0 · Laminar 17.2.1 · sbt 1.10.11 · Vite 7
