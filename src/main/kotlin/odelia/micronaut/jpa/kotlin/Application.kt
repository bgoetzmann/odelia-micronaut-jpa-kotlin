package odelia.micronaut.jpa.kotlin

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("odelia.micronaut.jpa.kotlin")
                .mainClass(Application.javaClass)
                .start()
    }
}