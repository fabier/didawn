package didawn

import didawn.gson.Track
import org.springframework.security.access.annotation.Secured

@Secured("hasRole('ROLE_ADMIN')")
class TrackController {

    SpService spService
    DiService diService

    final String KEY_USER_AGENT = "User-Agent"

    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"

    private final Map<String, String> BROWSER_HEADERS = [
            "User-Agent"      : USER_AGENT,
            "Content-Language": "en-US",
            "Cache-Control"   : "max-age=0",
            "Accept"          : "*/*",
            "Accept-Charset"  : "utf-8,ISO-8859-1;q=0.7,*;q=0.3",
            "Accept-Language" : "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4",
            "Accept-Encoding" : "gzip,deflate,sdch"
    ]

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
        render view: "search", model: [tracks: tracks, headers: BROWSER_HEADERS]
    }
}
