package didawn

import didawn.gson.Album
import didawn.gson.Artist
import didawn.gson.Track
import org.springframework.security.access.annotation.Secured

@Secured(['permitAll'])
class SearchController {

    DiService diService
    Mp3Service mp3Service

    def search() {
        String searchTerm = params.value

        List<Artist> artists = diService.searchArtists(searchTerm, 6)
        List<Album> albums = diService.searchAlbums(searchTerm, 12)
        List<Track> tracks = diService.searchTracks(searchTerm)

        tracks.each {
            it.setData(diService.getDownloadUrlEnd(it))
        }

        SortedMap<String, List<Track>> tracksMap = new TreeMap<>(tracks.groupBy {
            "${it.artist.id} ${it.album.title}"
        }.each {
            it.value.sort(true, { it.rank }).reverse(true)
        })

        tracksMap.sort {
            it.value.max { it.rank }
        }

        render view: "index", model: [
                searchTerm: params.value,
                artists   : artists,
                albums    : albums,
                tracksMap : tracksMap
        ]
    }

    def download(String id) {
        def byteArrayOutputStream = new ByteArrayOutputStream()
        diService.downloadFromData(id, params.data, byteArrayOutputStream)
        response.setHeader "Content-disposition", "attachment; filename=${params.filename}"
        response.contentType = 'application/octet-stream'
        response.outputStream << byteArrayOutputStream.toByteArray()
        response.outputStream.flush()
    }

    def dl(String id) {
        response.setHeader "Content-disposition", "attachment; filename=${params.filename}"
        response.contentType = 'application/octet-stream'
        File tempFile = File.createTempFile("data-", ".mp3")
        tempFile.withOutputStream {
            diService.downloadFromData(id, params.data, it)
        }
        Track track = diService.getTrackById(id)
        mp3Service.addTags(tempFile, track)
        tempFile.withInputStream {
            response.outputStream << it
        }
        response.outputStream.flush()
        tempFile.delete()
    }
}
