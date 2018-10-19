package odelia.micronaut.jpa.kotlin

import io.micronaut.context.ApplicationContext
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification. describe
import kotlin.test.assertEquals
import odelia.micronaut.jpa.kotlin.domain.Genre
import kotlin.test.assertFailsWith

fun entityId(response: HttpResponse<*>): Long? {
    val path = "/genres/"
    val value = response.header(HttpHeaders.LOCATION) ?: return null
    val index = value.indexOf(path)
    return if (index != -1) {
        java.lang.Long.valueOf(value.substring(index + path.length))
    } else null
}

object GenreControllerSpec: Spek({
    describe("GenreController Suite") {
        val embeddedServer = ApplicationContext.run(EmbeddedServer::class.java)
        val client = HttpClient.create(embeddedServer.url)

        it("supply an invalid order triggers validation failure") {
            val exception = assertFailsWith(HttpClientResponseException::class) {
                client.toBlocking().exchange<Any,Any>(HttpRequest.GET<Any>("/genres/list?order=foo"))
            }
            assertEquals(HttpStatus.BAD_REQUEST, exception.response.status)
        }

        it("find non existing genre returns 404") {
            val exception = assertFailsWith(HttpClientResponseException::class) {
                client.toBlocking().exchange<Any,Any>(HttpRequest.GET<Any>("/genres/99"))
            }
            assertEquals(HttpStatus.NOT_FOUND, exception.response.status)
        }

        it("CRUD operations") {
            val genreIds = ArrayList<Long?>()

            var request: HttpRequest<Any> = HttpRequest.POST("/genres", GenreSaveCommand("DevOps"))
            var response: HttpResponse<Any> = client.toBlocking().exchange(request)
            genreIds.add(entityId(response))

            assertEquals(HttpStatus.CREATED, response.status)

            request = HttpRequest.POST("/genres", GenreSaveCommand("Microservices"))
            response = client.toBlocking().exchange(request)

            assertEquals(HttpStatus.CREATED, response.status)

            val id = entityId(response)!!
            genreIds.add(id)
            request = HttpRequest.GET("/genres/$id")

            val (_, name) = client.toBlocking().retrieve(request, Genre::class.java)

            assertEquals("Microservices", name)

            request = HttpRequest.PUT("/genres", GenreUpdateCommand(id, "Micro-services"))
            response = client.toBlocking().exchange(request)

            assertEquals(HttpStatus.NO_CONTENT, response.status)

            request = HttpRequest.GET("/genres/$id")
            val (_, name1) = client.toBlocking().retrieve(request, Genre::class.java)

            assertEquals("Micro-services", name1)

            request = HttpRequest.GET("/genres/list")
            @Suppress("UNCHECKED_CAST")
            var genres = client.toBlocking().retrieve(request, Argument.of(List::class.java, Genre::class.java)) as List<Genre>

            assertEquals(2, genres.size)

            request = HttpRequest.GET("/genres/list?max=1")
            @Suppress("UNCHECKED_CAST")
            genres = client.toBlocking().retrieve(request, Argument.of(List::class.java, Genre::class.java)) as List<Genre>

            assertEquals(1, genres.size)
            assertEquals("DevOps", genres[0].name)

            request = HttpRequest.GET("/genres/list?max=1&order=desc&sort=name")
            @Suppress("UNCHECKED_CAST")
            genres = client.toBlocking().retrieve(request, Argument.of(List::class.java, Genre::class.java)) as List<Genre>

            assertEquals(1, genres.size)
            assertEquals("Micro-services", genres[0].name)

            request = HttpRequest.GET("/genres/list?max=1&offset=10")
            @Suppress("UNCHECKED_CAST")
            genres = client.toBlocking().retrieve(request, Argument.of(List::class.java, Genre::class.java)) as List<Genre>

            assertEquals(0, genres.size)

            // cleanup:
            for (genreId in genreIds) {
                request = HttpRequest.DELETE("/genres/" + genreId!!)
                response = client.toBlocking().exchange(request)
                assertEquals(HttpStatus.NO_CONTENT, response.status)
            }
        }

        afterGroup {
            client.close()
            embeddedServer.close()
        }
    }
})