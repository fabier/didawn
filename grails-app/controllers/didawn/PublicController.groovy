package didawn

import com.wrapper.spotify.models.Playlist
import org.springframework.security.access.annotation.Secured

@Secured(['permitAll'])
class PublicController {

    static defaultAction = "index"

    SpService spService

    def index() {
        List<Playlist> playlists = null
        def accessToken = session.accessToken
        if (accessToken) {
            playlists = spService.getPlaylists(accessToken)
            playlists.sort(true) { a, b ->
                a.name.compareTo(b.name)
            }
        }
        render view: "index", model: [playlists: playlists]
    }

    def logout() {
        session.removeAttribute("accessToken")
        session.removeAttribute("me")
        redirect action: "index"
    }
}
