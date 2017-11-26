package didawn

import didawn.gson.Album
import didawn.gson.Artist
import didawn.gson.Track
import org.springframework.security.access.annotation.Secured

@Secured("hasRole('ROLE_ADMIN')")
class ArtistController {

    DiService diService

    def show(String id) {
        Artist artist = diService.getArtistById(id)
        List<Album> albums = diService.getAlbumsByArtistId(id)
        Map<Album, List<Track>> albumAndTracks = albums.collectEntries {
            List<Track> tracks = diService.getTracksByAlbumId(it.id).sort(true) { a, b ->
                a.getTRACK_NUMBER() - b.getTRACK_NUMBER()
            }
            [(it): tracks]
        }
        render view: "show", model: [artist: artist, albums: albums, albumAndTracks: albumAndTracks]
    }
}
