# _tudo_

This project is a todo-Webapp with features 
like sharing items and advanced filtering, built for
the course _Webtech2_ (SS2017) at the TU Dortmund.

## Getting started

### Build process

A few steps are required to build tudo and make it run on your machine:
* Install [Apache Maven](https://maven.apache.org/) on your development machine (and configure it to be in your path)
* Make sure you have the [Dart SDK](https://www.dartlang.org/tools/sdk) installed and configure the pom.xml accordingly to point to your installation path
* Check if you have the [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) >1.8 installed
* If you want to test the Webapp on your local machine, you will need a Java EE Server. We used [WildFly](http://www.wildfly.org/) for development, so this one is guaranteed to work
* Configure the paths in the source code to point to your desired server address (default: `http://127.0.0.1:8080/tudo/`, simply search and replace the whole project for this string)

To actually build the project and package it into a .war-file, run `mvn clean package` from the project source directory.
This will generate the .war-file in the target directory, which you can simply deploy to your Java EE-Server.

The build process has not been tested yet on Linux. Please contact us if there are any errors.

### Standalone

For quicker, local development you can compile and deploy _tudo_ to a local standalone WildFly server.
Simply run `mvn clean wildfly:deploy` or `mvn clean wildfly:redeploy` to do so.
You will still need the perquisites from "Building & Deploying".

##### Backend only
If you just want to check out the backend, you cann disable the Maven profile `buildng` (which is activated by default).
This is useful since it drastically reduces the build time.

##### Frontend only
To just make optical changes to the frontend it is easy to run `pub get` and `pub serve` from `tudo/src/main/dart` and view your changes in realtime.

## Built with
* [Java Enterprise Edition 7](https://docs.oracle.com/javaee/7/)
* [WildFly Application Server](http://wildfly.org/)
* [Dart Programming Language](https://www.dartlang.org/)
* [Apache Maven](https://maven.apache.org/)
* [Apache Shiro](https://shiro.apache.org/)
* [H2 Database Engine](http://www.h2database.com/html/main.html)
* [JAX RS](https://github.com/jax-rs)
* [Stanford Javascript Crypto Library](https://crypto.stanford.edu/sjcl/)
* [Linearicons Free](https://linearicons.com/free)
* [Images by Justin Maller](http://www.justinmaller.com/)

## Authors
* Vanessa Speeth
* Merlin Scholz
