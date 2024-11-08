# Remui

A ~~server-side~~ server-managed UI framework for highly interactive JVM applications for cross-platform clients based 
on [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform-intro.html).

## Highlights
 - Fast and reactive applications
 - All logic defined in one place, the server
   - Clients don't make any of the decisions
 - Custom and composable component sets
 - Cross-platform clients
   - Supports interoperation with any platform Kotlin Multiplatform supports
 - Chose your own client renderer(s)
   - We don't care if it is React, Vue, Jetpack Compose, or SwiftUI, or something custom
 

## Why Server-Managed?

Remui is a UI framework for highly interactive applications, think applications where there is no discernible delay 
between user input and the application's response. 

Generally, applications that achieve this are built with client-side UI technologies like React or Vue for the web, or 
SwiftUI for iOS. While the traditional way of using these technologies is great for building highly interactive 
applications, it often comes with a few drawbacks:
 - Duplicated "business" state.
   - When using a client-side UI technology, you often have to fetch and cache some of your application's state on the 
     client and the server. This can lead to bugs where the client and server's state get out of sync.
 - Duplication of domain (business) modeling
   - TODO
 - Increased domain model complexity
   - TODO

Alternatively, traditional server-side UI technologies like JSP, ASP.NET, or PHP have the following drawbacks:
 - Slow UI updates
   - When using server-side UI technologies, the server often has to render most or the entire UI for every user 
     interaction.
   - **Slow UI updates are not suitable for highly interactive applications.**
 - Complex UI reconciliation
   - TODO
 - Heavy network traffic
   - At least until HTTP/3 is widely adopted, TODO

TODO discuss apps that need to run on server

Remui tries something different, **server-managed UI**. Remui continuously, incrementally, and reactively handles all UI 
state processing on the server, not on the client. Remui clients only understand the abstract UI components the server 
commands to be rendered and how to forward user interactions to the server, but clients have no understanding of what an 
application does, or how to handle user interactions themselves.

As a user interacts with the client, the server incrementally informs the client of changes to the UI state as the 
server sees fit. TODO redo this section and provide a better lifecycle

## Using the playground

This is a Kotlin project, while Kotlin does not need to be used with IntelliJ, it is HIGHLY recommended.

### With IntelliJ (recommended)

#### Install the latest version

Make sure you have installed the latest version of IntelliJ for the best experience

#### Clone the project

The project can be cloned through git using:

```bash
git clone git@gitlab.dasorl.com:common-libraries/remui.git
```

or through the IntelliJ [GitLab Plugin](https://plugins.jetbrains.com/plugin/22857-gitlab)

#### Run the playground

- Open IntelliJ's `Run Anything` window by double pressing `Ctrl`
- Search `Playground A` or `Playground B` and press `Enter`
- Open [`http://localhost:8080`](http://localhost:8080) in your browser
  - On first run things might take a little so you might need to refresh a few times

#### Modify the playground

- Open IntelliJ's `Search Everywhere` window by double tapping `Shift`
- Search either `ServerPlaygroundA.kt` or `ServerPlaygroundB.kt` then press `Enter`
- Rerun the corresponding Run Configuration

#### Enable K2 mode (optional)

Will ensure the best experience in IntelliJ. This will not always be optional

- Open IntelliJ's settings window by pressing `Ctrl`+`Alt`+`S` 
- Go to `Languages & Frameworks`/`Kotlin`
- Ensure `Enable K2 mode` is enabled

#### Enable non bundled compiler plugins (extra-optional)

This setting is only need to make sure the IDE is aware of the additional analysis Remui's compiler plugin provides. The
project's compiler plugin will be in use soon... (This setting also won't be needed once K2 support has been stabilized)

- Open IntelliJ's `Search Everywhere` window by double tapping `Shift`
- Type `registry...` into the textbox and press `Enter`
- Start typing `kotlin.k2.only` to search and ensure that the setting `kotlin.k2.only.bundled.compiler.plugins.enabled` is **not** checked
- Press `Close` and restart IntelliJ

### Without IntelliJ (not recommended)

#### Clone the project

Run the following to clone the project:
```bash
git clone git@gitlab.dasorl.com:common-libraries/remui.git
cd remui
```

#### Start the client web server

```bash
./gradlew :playground-js:jsBrowserDevelopmentRun --stacktrace --info --continuous
```
#### Start the application server

In a different terminal session, run the following: 

```bash
./gradlew :playground-js:jvmRun -DmainClass=my.test.ServerPlaygroundAKt --quiet
```
or
```bash
./gradlew :playground-js:jvmRun -DmainClass=my.test.ServerPlaygroundBKt --quiet
```

#### Modify the playground

You'll find the server playground files in `playground-js/src/jvmMain/kotlin/my/test/`. Feel free to modify 
`ServerPlaygroundA.kt` or `ServerPlaygroundB.kt` and rerun the corresponding application server. Have fun.

## Understanding Remui

### Nodes (aka Struct or RStructs)

*Right now in documentation and code the terms Node, Struct, and RStruct are used, this will eventually be unified into
a single term, but please bare with this confusion for now.*

Nodes are at the core of how Remui communicates the structure and contents of a UI. Nodes are instances of certain 
Kotlin classes and each class defines a number of properties that each node will possess a corresponding value for. A
definition of a node's class might look like the following:

```kotlin
class DomTagElementNode: RStruct.Impl() {
    val tagName       : String                  by _prop()
    val eventListeners: List<DomEventListener>  by _prop { listOf() }
    val childNodes    : List<DomTagElementNode> by _prop { listOf() }
    val attributes    : Map<String, String>     by _prop { mapOf() }
}
```

Let's break this down a little:

```kotlin 
class DomTagElementNode: RStruct.Impl()
```

Above we have specified the name of our node type, and the types that our type inherits from. All node types MUST be a
subtype of `RStruct.Impl`, this may be direct or indirect. You also may notice the lack of constructor parameters, nodes
may not have any constructor parameters, this is a restriction dues to the way nodes are instantiated. Type parameters 
may be used though.



### Interactions

TODO

#### Interaction Predictors

TODO

### Component Sets

TODO

### Object Pool & Modules

TODO

### Configs

TODO

### Remui Server

TODO

### Remui Client

TODO

### Serialization

TODO

### Messaging

TODO




