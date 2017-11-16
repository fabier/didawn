package didawn

import com.wrapper.spotify.models.Playlist
import com.wrapper.spotify.models.PlaylistTrack
import com.wrapper.spotify.models.Track
import org.springframework.security.access.annotation.Secured

@Secured(['permitAll'])
class PlaylistController {

    SpService spService

    def show(String id) {
        Playlist playlist = spService.getPlaylist(session.accessToken, params.userId, id)
        List<PlaylistTrack> tracks = spService.getTracks(session.accessToken, params.userId, id)
        render view: "show", model: [playlist: playlist, tracks: tracks]
    }
}
