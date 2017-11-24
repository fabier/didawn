package didawn

import didawn.gson.Track
import org.springframework.security.access.annotation.Secured

@Secured("hasRole('ROLE_ADMIN')")
class TrackController {

    SpService spService
    DiService diService

    def show(String id) {
        com.wrapper.spotify.models.Track track = spService.getTrack(session.accessToken, id)
        render view: "show", model: [track: track]
    }

    def search() {
        String artist = params.artist
        String title = params.title
        List<Track> tracks = diService.searchTracksByArtistAndTitle(artist, title)
        tracks.each {
            it.setData(diService.getDownloadUrlEnd(it))
        }
        render view: "search", model: [tracks: tracks]
    }
}
