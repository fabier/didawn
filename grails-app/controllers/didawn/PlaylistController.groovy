package didawn

import com.wrapper.spotify.models.Playlist
import com.wrapper.spotify.models.PlaylistTrack
import didawn.gson.Track
import org.springframework.security.access.annotation.Secured

@Secured("hasRole('ROLE_ADMIN')")
class PlaylistController {

    SpService spService
    DiService diService

    def show(String id) {
        Playlist playlist = spService.getPlaylist(session.accessToken, params.userId, id)
        List<PlaylistTrack> tracks = spService.getTracks(session.accessToken, params.userId, id)
        tracks.sort(true) { a, b ->
            int compare = a.track.artists.first().name.compareTo(b.track.artists.first().name)
            compare == 0 ? a.track.name.compareTo(b.track.name) : compare
        }
        render view: "show", model: [playlist: playlist, tracks: tracks]
    }

    def ajaxTrack() {
        String artist = params.artist
        String title = params.title
        List<Track> tracks = diService.searchTracksByArtistAndTitle(artist, title)
        Track track = tracks.size() == 1 ? tracks.first() : tracks.max { it.rank }
        if (track) {
            String data = diService.getDownloadUrlEnd(track)
            render(contentType: "application/json") {
                [id: track.getSNG_ID(), data: data]
            }
        } else {
            render ""
        }
    }
}
