package didawn

import didawn.gson.Track
import org.springframework.security.access.annotation.Secured

@Secured(['permitAll'])
class SearchController {

    DiService diService

    def search() {
        String searchTerm = params.value

        List<didawn.gson.Album> albums = diService.searchAlbums(searchTerm, 12)
        List<Track> tracks = diService.searchTracks(searchTerm)

        tracks.each {
            it.setData(diService.getDownloadUrlEnd(it))
        }

        render view: "index", model: [
                searchTerm: params.value,
                albums    : albums,
                tracks    : tracks
        ]
    }

    def download(String id) {
        def byteArrayOutputStream = new ByteArrayOutputStream()
        diService.downloadFromData(id, params.data, byteArrayOutputStream)
        response.setHeader "Content-disposition", "attachment; filename=${params.filename}"
        response.contentType = 'audio/mp3'
        response.outputStream << byteArrayOutputStream.toByteArray()
        response.outputStream.flush()
    }
}
