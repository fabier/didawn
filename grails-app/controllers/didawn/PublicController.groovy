package didawn

import com.wrapper.spotify.models.Playlist
import org.springframework.security.access.annotation.Secured

@Secured(['permitAll'])
class PublicController {

    static defaultAction = "index"

    SpService spService

    def index() {
        URI spURI = spService.buildAuthorizeUri()
        log.info spURI.toString()

        List<Playlist> playlists = null
        if (session.accessToken) {
            playlists = spService.getPlaylists(session.accessToken)
        }

        render view: "index", model: [uri: spURI.toString(), playlists: playlists]
    }
}
