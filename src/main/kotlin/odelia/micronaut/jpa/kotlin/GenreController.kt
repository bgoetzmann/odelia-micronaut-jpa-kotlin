package odelia.micronaut.jpa.kotlin

import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import odelia.micronaut.jpa.kotlin.domain.Genre
import java.net.URI
import javax.validation.Valid

@Validated
@Controller("/genres")
class GenreController(protected val genreRepository: GenreRepository) {

    @Get("/{id}")
    fun show(id: Long): Genre? = genreRepository.findById(id)

    @Put("/")
    fun update(@Body @Valid command: GenreUpdateCommand): HttpResponse<Any> {
        val numberOfEntitiesUpdated = genreRepository.update(command.id, command.name)

        return HttpResponse
                .noContent<Any>()
                .header(HttpHeaders.LOCATION, location(command.id).path)
    }

    @Get("/list{?args*}")
    fun list(@Valid args: SortingAndOrderArguments): List<Genre> {
        return genreRepository.findAll(args)
    }

    @Post("/")
    fun save(@Body @Valid cmd: GenreSaveCommand): HttpResponse<Genre> {
        val genre = genreRepository.save(cmd.name)

        return HttpResponse
                .created(genre)
                .headers { headers -> headers.location(location(genre.id)) }
    }

    @Delete("/{id}")
    fun delete(id: Long): HttpResponse<Any> {
        genreRepository.deleteById(id)
        return HttpResponse.noContent()
    }

    protected fun location(id: Long?): URI {
        return URI.create("/genres/" + id!!)
    }

    protected fun location(genre: Genre): URI {
        return location(genre.id)
    }
}