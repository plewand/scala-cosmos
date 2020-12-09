package org.cosmos.scala

// A new way to define the program entry point. Note the automatic argument extraction.
@main def startApp(port: Int) = {
  Server.run(port)
}
