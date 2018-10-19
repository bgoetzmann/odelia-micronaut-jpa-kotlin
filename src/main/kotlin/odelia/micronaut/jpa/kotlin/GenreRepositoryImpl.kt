package odelia.micronaut.jpa.kotlin

import javax.persistence.EntityManager
import io.micronaut.configuration.hibernate.jpa.scope.CurrentSession
import io.micronaut.spring.tx.annotation.Transactional
import odelia.micronaut.jpa.kotlin.domain.Genre
import java.util.*
import javax.inject.Singleton
import javax.persistence.PersistenceContext
import javax.validation.constraints.NotBlank

@Singleton
open class GenreRepositoryImpl(@param:CurrentSession @field:PersistenceContext
                          private val entityManager: EntityManager,
                          private val applicationConfiguration: ApplicationConfiguration) : GenreRepository {

    @Transactional(readOnly = true)
    override fun findById(id: Long): Genre? {
        return entityManager.find(Genre::class.java, id)
    }

    @Transactional
    override fun save(name: String): Genre {
        val genre = Genre(name)
        entityManager.persist(genre)
        return genre
    }

    @Transactional
    override fun deleteById(id: Long) {
        val genre = findById(id)
        if (genre != null) entityManager.remove(genre)
    }

    @Transactional(readOnly = true)
    override fun findAll(args: SortingAndOrderArguments): List<Genre> {
        var qlString = "SELECT g FROM Genre as g"
        if (args.order != null && args.sort != null && VALID_PROPERTY_NAMES.contains(args.sort)) {
            qlString += " ORDER BY g." + args.sort + " " + args.order.toLowerCase()
        }
        val query = entityManager.createQuery(qlString, Genre::class.java)
        query.maxResults = if (args.max != null) args.max else applicationConfiguration.max!!
        if (args.offset != null)
            query.firstResult = args.offset

        return query.resultList
    }

    @Transactional
    override fun update(id: Long, @NotBlank name: String): Int {
        return entityManager.createQuery("UPDATE Genre SET name = :name where id = :id")
                .setParameter("name", name)
                .setParameter("id", id)
                .executeUpdate()
    }

    companion object {
        private val VALID_PROPERTY_NAMES = Arrays.asList("id", "name")
    }
}