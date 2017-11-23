package didawn

import com.wrapper.spotify.models.Track
import org.springframework.security.access.annotation.Secured

@Secured("hasRole('ROLE_ADMIN')")
class TrackController {

    SpService spService

    def show(String id) {
        Track track = spService.getTrack(session.accessToken, id)
        render view: "show", model: [track:track]
    }
}
