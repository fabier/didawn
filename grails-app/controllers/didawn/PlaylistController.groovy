package didawn

import com.wrapper.spotify.models.Playlist
import com.wrapper.spotify.models.PlaylistTrack
import org.springframework.security.access.annotation.Secured

@Secured("hasRole('ROLE_ADMIN')")
class PlaylistController {

    SpService spService

    def show(String id) {
        Playlist playlist = spService.getPlaylist(session.accessToken, params.userId, id)
        List<PlaylistTrack> tracks = spService.getTracks(session.accessToken, params.userId, id)
        tracks.sort(true) { a, b ->
            a.track.artists.first().name.compareTo(b.track.artists.first().name)
        }
        render view: "show", model: [playlist: playlist, tracks: tracks]
    }
}
