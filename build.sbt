import org.scalajs.linker.interface.ModuleSplitStyle

val scala3Version = "3.4.3"
val circeVersion  = "0.14.10"

lazy val magicvar = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin) // Enable the Scala.js plugin in this project
  .settings(
    scalaVersion := scala3Version,
    scalacOptions ++= Seq(
      "-language:strictEquality",
      "-source:future",
      "-feature",
      "-deprecation",
      "-Ykind-projector:underscores",
      "-Ysafe-init",
      "-Xmax-inlines:256",
      "-language:implicitConversions",
      "-Wunused:all",
      "-Wvalue-discard"
    ),

    // Tell Scala.js that this is an application with a main method
    scalaJSUseMainModuleInitializer := true,

    /* Configure Scala.js to emit modules in the optimal way to
     * connect to Vite's incremental reload.
     * - emit ECMAScript modules
     * - emit as many small modules as possible for classes in the "livechart" package
     * - emit as few (large) modules as possible for all other classes
     *   (in particular, for the standard library)
     */
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("magicvar")))
    },

    /* Depend on the scalajs-dom library.
     * It provides static types for the browser DOM APIs.
     */
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "2.8.0",
      "com.raquo"    %%% "laminar"     % "17.1.0"
    ),
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core",
      "io.circe" %%% "circe-generic",
      "io.circe" %%% "circe-parser"
    ).map(_ % circeVersion)
  )
