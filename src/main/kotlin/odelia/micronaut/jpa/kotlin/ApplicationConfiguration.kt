package odelia.micronaut.jpa.kotlin

import javax.validation.constraints.NotNull

interface ApplicationConfiguration {

    @get:NotNull
    val max: Int?
}