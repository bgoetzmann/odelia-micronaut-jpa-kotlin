package odelia.micronaut.jpa.kotlin

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("application")
class ApplicationConfigurationProperties : ApplicationConfiguration {

    protected val DEFAULT_MAX = 10

    override var max: Int? = DEFAULT_MAX
        set(max) {
            if (max != null) {
                field = max
            }
        }
}