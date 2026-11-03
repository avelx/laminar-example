import org.scalajs.linker.interface.ModuleSplitStyle

ThisBuild / scalaVersion := "3.3.6"

lazy val root = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "laminar-example",
    scalacOptions ++= Seq("-deprecation", "-feature"),
    // Emit an ES module with a main entry point that Vite can import
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("example")))
    },
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "17.2.1",
      "org.scalameta" %%% "munit" % "1.1.1" % Test
    ),
    // Tests run in Node with a simulated browser DOM (jsdom, from node_modules)
    Test / jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),
    // The jsdom env can't load ES modules, so tests are linked as a plain script
    // and must not run the app's @main entry point
    Test / scalaJSUseMainModuleInitializer := false,
    Test / scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.NoModule)
        .withModuleSplitStyle(ModuleSplitStyle.FewestModules)
    }
  )
