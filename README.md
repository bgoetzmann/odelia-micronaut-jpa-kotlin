# odelia-micronaut-jpa-kotlin

This [Micronaut](http://micronaut.io/) project is a port in [Kotlin](https://kotlinlang.org/) language of the [Access a database with JPA and Hibernate](http://guides.micronaut.io/micronaut-data-access-jpa-hibernate/guide/index.html) Micronaut guide that, at the time of this writing, only exists for Java language.

This project uses Micronaut version 1.0.0.RC3, and [Spek Framework](https://spekframework.org/) 2 for the tests.

Note that the compiler plugin [kotlin-jpa](https://kotlinlang.org/docs/reference/compiler-plugins.html#jpa-support) is used in order to have zero-parameter constructors for JPA entities at run-time.
