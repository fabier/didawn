package didawn

import org.springframework.security.access.annotation.Secured

@Secured("hasRole('ROLE_ADMIN')")
class ArtistController {

    DiService diService

    def show(String id) {
        diService.getArtistTracks(id)
    }
}
