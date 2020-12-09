## Scala Cosmos

An example project to show multiple Scala 3 features in a simple web application.

This project was created to try Scala 3 in a working program, find possible use cases and
meet problems that may appear while creating a "real" web application.

### Structure

The program is a simple web application that allows the user to obtain information about celestial bodies.
It is simplified not to hide Scala 3 features in a bloated code structure.
Only GET HTTP method is used, the responses are encoded into JSON.

### Scala 3 features

The project uses the following Scala 3 features:

1. **Givens** in multiple cases.
2. New **enums**, parametrized and not, also used to define ADT.
3. **Extension functions**.
4. **Opaque types** to keep domain values.
5. **Automatic type class derivation** for response JSON encoding.
6. **Implicit conversions** in a new Scala 3 way.
7. AkkaHttp as a **Scala 2 integration** example.
8. Scala 3 test framework usage with givens profits.
9. The new way to define program entry point with **@main** annotation.
10. **Union** and **intersection** types.

### Usage

This is a normal sbt project, you can compile code with `sbt compile` and run it
with `sbt "run <port number>"`. The unit tests are executed with `sbt test`.

The program accepts one argument - the port to listen. On start example requests are displayed.

For more information on the Dotty and features used in the project, please visit:
[dotty-reference](https://dotty.epfl.ch/docs/reference/overview.html).
 
