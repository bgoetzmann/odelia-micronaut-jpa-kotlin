package odelia.micronaut.jpa.kotlin.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
@Table(name = "genre")
data class Genre(
        @Id
        @GeneratedValue
        val id: Long,

        @Column(name = "name", nullable = false, unique = true)
        val name: String,

        @JsonIgnore
        @OneToMany(mappedBy = "genre")
        val books: Set<Book> = emptySet()
) {
        constructor(name: String) : this(0, name)
}