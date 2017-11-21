package didawn

import com.google.gson.Gson
import didawn.gson.TrackExtra
import didawn.json.*
import didawn.models.ArtistList
import didawn.models.Song
import grails.converters.JSON
import grails.transaction.Transactional
import grails.util.Environment
import org.apache.commons.io.IOUtils
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicHeader
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

import java.nio.ByteBuffer
import java.util.regex.Matcher
import java.util.regex.Pattern

import static java.lang.Long.parseLong
import static java.lang.String.format
import static java.lang.Thread.sleep
import static java.net.URLEncoder.encode
import static java.util.Arrays.asList
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import static org.apache.commons.lang3.StringUtils.substringBetween
import static org.apache.http.util.EntityUtils.consume

@Transactional
class DiService {

    final String KEY_USER_AGENT = "User-Agent"

    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36"

    private final Header[] BROWSER_HEADERS = [
            new BasicHeader("User-Agent", USER_AGENT),
            new BasicHeader("Content-Language", "en-US"),
            new BasicHeader("Cache-Control", "max-age=0"),
            new BasicHeader("Accept", "*/*"),
            new BasicHeader("Accept-Charset", "utf-8,ISO-8859-1;q=0.7,*;q=0.3"),
            new BasicHeader("Accept-Language", "de-DE,de;q=0.8,en-US;q=0.6,en;q=0.4"),
            new BasicHeader("Accept-Encoding", "gzip,deflate,sdch")
    ]

    private final WAIT_TIME = 1000

    private final String UTF8 = "UTF-8"

    private final HttpClient httpClient = HttpClientBuilder.create().build()

    private String apiToken

    def CryptoService cryptoService
    def grailsApplication

    String wwwUrl() {
        return grailsApplication.config.di.wwwUrl
    }

    String apiUrl() {
        return grailsApplication.config.di.apiUrl
    }

    String dawn() {
        return grailsApplication.config.di.dawn
    }

    String dawnDotCom() {
        return grailsApplication.config.di.dawndotcom
    }

    String get(String url, List<Header> headers) {
        log.info "GET : ${url}"

        String responseContent = null
        HttpEntity httpEntity = null

        try {
            HttpGet httpGet = new HttpGet(url)
            httpGet.setHeader(KEY_USER_AGENT, USER_AGENT)
            if (headers != null) {
                httpGet.setHeaders(headers.toArray(new Header[0]))
            }

            HttpResponse httpResponse = httpClient.execute(httpGet)
            ByteArrayOutputStream baos = new ByteArrayOutputStream()
            httpEntity = httpResponse.getEntity()
            StatusLine statusLine = httpResponse.getStatusLine()
            int statusCode = statusLine.getStatusCode()
            if (statusCode != 200) {
                throw new IllegalStateException("status code: " + statusLine.getStatusCode())
            }

            httpEntity.writeTo(baos)
            responseContent = baos.toString(UTF8)
        } catch (IOException | RuntimeException e) {
            log.warn e
            consume(httpEntity)
        } finally {
            consume(httpEntity)
        }

        log.info responseContent
        return responseContent
    }

    String post(String url, String data, List<Header> headers) {
        log.info "POST : ${url}"

        String responseContent = null
        HttpEntity httpEntity = null

        try {
            HttpPost httpPost = new HttpPost(url)
            httpPost.setHeader(KEY_USER_AGENT, USER_AGENT)
            if (headers != null) {
                httpPost.setHeaders(headers.toArray(new Header[headers.size()]))
            }

            httpPost.setEntity(new StringEntity(data))
            HttpResponse httpResponse = httpClient.execute(httpPost)
            ByteArrayOutputStream baos = new ByteArrayOutputStream()
            httpEntity = httpResponse.getEntity()
            StatusLine statusLine = httpResponse.getStatusLine()
            int statusCode = statusLine.getStatusCode()
            if (statusCode != 200) {
                log.info statusCode
                throw new IllegalStateException("status code: " + statusLine.getStatusCode())
            }

            httpEntity.writeTo(baos)
            responseContent = baos.toString(UTF8)
        } catch (IOException e) {
            log.warn e
        } finally {
            consume(httpEntity)
        }

        if (responseContent != null && responseContent.contains("VALID_TOKEN_REQUIRED")) {
            String apiToken = newApiToken(true)
            URIBuilder builder = new URIBuilder(url)
            builder.setParameter("api_token", apiToken)
            url = builder.build().toString()
            return post(url, data, headers)
        } else {
            log.info responseContent
            return responseContent
        }
    }

    String newApiToken(boolean force = false) {
        if (apiToken == null || force) {
            String responseContent = get(wwwUrl(), asList(BROWSER_HEADERS))
            String newToken = substringBetween(responseContent, "var checkForm", ";")
            if (newToken != null) {
                while (true) {
                    if (!newToken.startsWith("\"") && !newToken.startsWith("'") && !newToken.startsWith("=")
                            && !newToken.startsWith(" ")) {
                        while (newToken.endsWith("\"") || newToken.endsWith("'") || newToken.endsWith("=")
                                || newToken.endsWith(" ")) {
                            newToken = newToken.substring(0, newToken.length() - 1)
                        }

                        if (newToken.length() == 32) {
                            apiToken = newToken
                        }
                        break
                    }

                    newToken = newToken.substring(1)
                }
            }
        }
        return apiToken
    }

    boolean isUserTrack(Song song) {
        return song.getId().startsWith("-")
    }

    String getCoverUrl(Song song) {
        format("http://cdn-images.%s/images/cover/%s/500x500-000000-80-0-0.jpg", dawnDotCom(), song.getAlbumPicture())
    }

    String getDownloadUrl(Song song) {
        return cryptoService.getDownloadURL(song.getMd5(), getFormat(song), getSongID(song), song.getMediaVersion())
    }

    String getDownloadUrlEnd(Song song) {
        return cryptoService.getDownloadUrlEnd(song.getMd5(), getFormat(song), getSongID(song), song.getMediaVersion())
    }

    String getDownloadUrlEnd(didawn.gson.Track track) {
        track.trackExtra == null ? null : getDownloadUrlEnd(track.trackExtra)
    }

    String getDownloadUrlEnd(TrackExtra trackExtra) {
        trackExtra == null ? null : cryptoService.getDownloadUrlEnd(trackExtra.getMD5_ORIGIN(),
                getFormat(trackExtra),
                trackExtra.getSNG_ID(),
                trackExtra.getMEDIA_VERSION()
        )
    }

    boolean isUserTrack(TrackExtra trackExtra) {
        trackExtra.getSNG_ID() < 0
    }

    int getFormat(TrackExtra trackExtra) {
        if (isUserTrack(trackExtra)) {
            return 0;
        } else {
            return trackExtra.getFILESIZE_MP3_320() > 0L ? 3 : trackExtra.getFILESIZE_MP3_256() > 0L ? 5 : 1;
        }
    }

    String getDownloadUrl(String md5, int format, String id, int mediaVersion) {
        return cryptoService.getDownloadURL(md5, format, id, mediaVersion)
    }

    String getSongID(Song song) {
        if (isUserTrack(song)) {
            return song.getId().substring(1)
        } else {
            return song.getId()
        }
    }

    int getFormat(Song song) {
        if (isUserTrack(song)) {
            return 0
        } else {
            return song.getFileSizeMp3_320() > 0L ? 3 : song.getFileSizeMp3_256() > 0L ? 5 : 1
        }
    }

    boolean download(Song song, OutputStream outputStream) {
        download(song.getMd5(), getFormat(song), getSongID(song), song.getMediaVersion(), outputStream)
    }

    boolean download(String md5, int format, String id, int mediaVersion, OutputStream outputStream) {
        download(id, getDownloadUrl(md5, format, id, mediaVersion), outputStream)
    }

    boolean downloadFromData(String id, String data, OutputStream outputStream) {
        download(id, cryptoService.getDownloadUrlFromData(data), outputStream)
    }

    boolean download(String id, String url, OutputStream outputStream) {
        log.info "GET ${url}"
        HttpGet httpGet = new HttpGet(url)

        try {
            httpGet.setHeaders(BROWSER_HEADERS)
            HttpResponse httpResponse = httpClient.execute(httpGet)
            HttpEntity httpEntity = httpResponse.getEntity()
            StatusLine statusLine = httpResponse.getStatusLine()
            int statusCode = statusLine.getStatusCode()
            if (statusCode != 200) {
                return false
            } else {
                int chunkSize = 2048
                int intervalChunk = 3
                InputStream inputStream = httpEntity.getContent()
                long contentLength = httpEntity.getContentLength()
                byte[] chunk = new byte[chunkSize]
                int chunks = (int) (contentLength / chunkSize)
                int readTotal = 0
                int i = 0
                byte[] blowfishKey = cryptoService.getBlowfishKey(parseLong(id))

                int read
                while ((read = inputStream.read(chunk, 0, chunkSize)) != -1) {
                    if (read < chunkSize && i < chunks - 1) {
                        ByteBuffer buffer = ByteBuffer.allocate(chunkSize)
                        buffer.put(ByteBuffer.wrap(chunk, 0, read))

                        while (buffer.hasRemaining()) {
                            byte[] temp = new byte[buffer.remaining()]
                            int tempRead = inputStream.read(temp, 0, buffer.remaining())
                            read += tempRead
                            buffer.put(temp, 0, tempRead)
                        }

                        chunk = buffer.array()
                    }

                    if (i % intervalChunk == 0 && read == chunkSize) {
                        chunk = cryptoService.decryptBlowfish(chunk, blowfishKey)
                    }

                    outputStream.write(chunk, 0, read)
                    ++i
                    readTotal += read
                }

                outputStream.close()
                IOUtils.closeQuietly(outputStream)
                consume(httpEntity)
                return true
            }
        } catch (IOException e) {
            log.warn e
            return false
        }
    }

    Song getSong(String songID) {
        String token = newApiToken()
        String trackResponse = post("${wwwUrl()}/ajax/gw-light.php?api_version=1.0&api_token=" + token + "&input=3", "[{\"method\":\"song.getListData\",\"params\":{\"sng_ids\":[" + songID + "]}}]", asList(BROWSER_HEADERS))
        if (trackResponse == null || trackResponse.equals("[{\"error\":{\"REQUEST_ERROR\":\"Wrong parameters\"},\"results\":{}}]")) {
            return null
        } else {
            Response response = new Gson().fromJson(trackResponse, Response.class)
            Results result = response.getResults()
            if (result.getCount() == 0) {
                return null
            } else {
                return result.getTracks().get(0).toSong()
            }
        }
    }

    List<Song> getPlaylist(String pl) throws InterruptedException {
        List<Song> songList = new ArrayList<>()
        String searchResponse = get(pl, asList(BROWSER_HEADERS))
        if (searchResponse != null) {
            SearchResponse response = new Gson().fromJson(searchResponse, SearchResponse.class)
            Error error = response.getError()
            if (error != null) {
                if (error.getCode() == 4) {
                    sleep(WAIT_TIME)
                    return getPlaylist(pl)
                } else {
                    return songList
                }
            }

            List<Data> tracks = response.getTracks().getData()

            if (tracks.isEmpty()) {
                return songList
            }

            tracks.each { track ->
                String id = track.getId()
                String title = track.getTitle()
                String album = track.getAlbum().getTitle()
                ArtistList artists = new ArtistList()
                artists.add(track.getArtist().getName())
                long duration = track.getDuration()
                String altSID = track.getAlternative() == null ? "" : track.getAlternative().getID()
                Song s = new Song(id, track.getMd5(), title, artists, album, duration, 0L, "", track.getMediaVersion(), track.getAlbumPicture())
                s.setAlbumArtist(artists.get(0))
                s.setAlternativeID(altSID)

                String coverXL = track.getAlbum().getCoverXL()
                if (coverXL != null) {
                    s.setCoverURL(coverXL)
                } else {
                    String coverBig = track.getAlbum().getCoverBig()
                    if (coverBig != null) {
                        s.setCoverURL(coverBig)
                    }
                }
                songList.add(s)
            }

            if ((songList = getExtraInfo(songList)).size() == 400) {
                songList.addAll(getPlaylistTracks(pl, 400))
            }
        }

        return songList
    }

    List<Song> getArtist(String artistURL) throws InterruptedException {
        List<Song> songList = new ArrayList<>()
        String searchResponse = get(artistURL, asList(BROWSER_HEADERS))
        if (searchResponse != null) {
            SearchResponse response = new Gson().fromJson(searchResponse, SearchResponse.class)
            Error error = response.getError()
            if (error != null) {
                if (error.getCode() == 4) {
                    sleep(WAIT_TIME)
                    return getArtist(artistURL)
                }

                return songList
            }

            List<Data> albums = response.getData()
            if (albums.isEmpty()) {
                return songList
            }

            for (Data album : albums) {
                String link = album.getLink()
                if (!link.isEmpty()) {
                    songList.addAll(getAlbumTracks(link.replace("www.", "api.")))
                }
            }

            songList = getExtraInfo(songList)
            String next = response.getNext()
            if (!next.isEmpty()) {
                songList.addAll(getArtist(next))
            }
        }

        return songList
    }

    List<Song> getPlaylistTracks(String pl, int index) throws InterruptedException {
        List<Song> songList = new ArrayList<>()
        String searchResponse = get(pl + "/tracks?index=" + index, asList(BROWSER_HEADERS))
        if (searchResponse != null) {
            SearchResponse response = new Gson().fromJson(searchResponse, SearchResponse.class)
            Error error = response.getError()
            if (error != null) {
                if (error.getCode() == 4) {
                    sleep(WAIT_TIME)
                    return getPlaylistTracks(pl, index)
                } else {
                    return songList
                }
            }

            List<Data> tracks = response.getData()
            if (tracks.isEmpty()) {
                return songList
            }

            tracks.each { track ->
                String id = track.getId()
                String title = track.getTitle()
                String album = track.getAlbum().getTitle()
                ArtistList artists = new ArtistList()
                artists.add(track.getArtist().getName())
                long duration = track.getDuration()
                String altSID = track.getAlternative() == null ? "" : track.getAlternative().getID()
                Song s = new Song(id, track.getMd5(), title, artists, album, duration, 0L, "", track.getMediaVersion(), track.getAlbumPicture())
                s.setAlternativeID(altSID)
                s.setAlbumArtist(artists.get(0))
                String coverXL = track.getAlbum().getCoverXL()
                if (coverXL != null) {
                    s.setCoverURL(coverXL)
                } else {
                    String coverBig = track.getAlbum().getCoverBig()
                    if (coverBig != null) {
                        s.setCoverURL(coverBig)
                    }
                }

                songList.add(s)
            }

            songList = getExtraInfo(songList)
            if (response.getNext() != null) {
                songList.addAll(getPlaylistTracks(pl, index + 25))
            }
        }

        return songList
    }

    List<Song> getExtraInfo(List<Song> songList) {
        StringBuilder sb = new StringBuilder("[{\"method\":\"song.getListData\",\"params\":{\"sng_ids\":[")
        sb.append(songList.collect { it.id }.join(","))
        sb.append("]}}]")
        String ids = sb.toString()

        String url = "${wwwUrl()}/ajax/gw-light.php?api_version=1.0&api_token=" +
                newApiToken() + "&input=3&cid=" + randomAlphanumeric(18).toLowerCase()
        String trackResponse = post(url, ids, asList(BROWSER_HEADERS))

        Response[] response = new Gson().fromJson((String) trackResponse, Response[].class) as Response[]
        Response r = response[0]
        def results = r.getResults()
        List<Data> tracks = results.getData()

        tracks.each { track ->
            String tmpID = track.getSongID()
            Song s2 = songList.find { it.id.equals(tmpID) }
            s2.setArtists(track.getArtistList())
            s2.setMd5(track.getMd5())
            s2.setTrackNum(track.getTrackNumber())
            s2.setYear(track.getYear())
            s2.setDiskNumber(track.getDiskNumber())
            s2.setIsrc(track.getIsrc())
            s2.setComposer(track.getComposer())
            s2.setBpm(track.getBpm())
        }

        return songList
    }

    List<didawn.gson.Track> getTrackExtra(List<didawn.gson.Track> trackList) {
        if (Environment.current == Environment.PRODUCTION) {
            StringBuilder sb = new StringBuilder("[{\"method\":\"song.getListData\",\"params\":{\"sng_ids\":[")
            sb.append(trackList.collect { it.id }.join(","))
            sb.append("]}}]")
            String ids = sb.toString()

            String url = "${wwwUrl()}/ajax/gw-light.php?api_version=1.0&api_token=" +
                    newApiToken() + "&input=3&cid=" + randomAlphanumeric(18).toLowerCase()
            String extraInfoResponse = post(url, ids, asList(BROWSER_HEADERS))

            JSONObject extraInfoJSON = JSON.parse(extraInfoResponse)

            JSONArray data = extraInfoJSON.results.data
            List<TrackExtra> trackExtraList = Arrays.asList(new Gson().fromJson(data.toString(), TrackExtra[].class))

            trackList.each { t ->
                long trackId = t.getId()
                TrackExtra trackExtra = trackExtraList.find { trackId == it.SNG_ID }
                t.setTrackExtra(trackExtra)
            }
        }
        return trackList
    }

    List<Song> getAlbumTracks(String albumUrl) throws InterruptedException {
        List<Song> songList = new ArrayList<>()
        String searchResponse = get(albumUrl, asList(BROWSER_HEADERS))

        try {
            if (searchResponse != null) {
                SearchResponse response = new Gson().fromJson(searchResponse, SearchResponse.class)
                Error error = response.getError()
                if (error != null) {
                    if (error.getCode() == 4) {
                        sleep(WAIT_TIME)
                        return getAlbumTracks(albumUrl)
                    } else {
                        return songList
                    }
                }

                String album = response.getTitle()
                String genre = ""
                List<Data> data = response.getGenres().getData()
                if (data != null && !data.isEmpty()) {
                    genre = data.get(0).getName()
                }
                String label = response.getLabel()
                String trackCount = response.getNbTracks()

                String albumArtist = response.getArtist().getName()

                List<Data> tracks = response.getTracks().getData()
                if (tracks.isEmpty()) {
                    return songList
                }

                tracks.each { track ->
                    String id = track.getId()
                    String title = track.getTitle()
                    ArtistList artists = new ArtistList()
                    artists.add(track.getArtist().getName())
                    long duration = track.getDuration()
                    Alternative alternative = track.getAlternative()

                    Song s = new Song(id, track.getMd5(), title, artists, album, duration, 0L, "", track.getMediaVersion(), track.getAlbumPicture())

                    if (alternative != null) {
                        s.setAlternativeID(alternative.getID())
                    }

                    s.setGenre(genre)
                    s.setLabel(label)
                    s.setAlbumTrackCount(trackCount)
                    s.setAlbumArtist(albumArtist)
                    String coverXL = response.getCoverXL()
                    String coverBig = response.getCoverBig()
                    if (coverXL != null) {
                        s.setCoverURL(coverXL)
                    } else if (coverBig != null) {
                        s.setCoverURL(coverBig)
                    }

                    songList.add(s)
                }
            }
        } catch (NullPointerException e) {
            log.warn e
        }

        return getExtraInfo(songList)
    }

    boolean downloadHelper(Song song, OutputStream out) {
        if (download(song, out)) {
            return true
        } else {
            return (song.getAlternativeID() != null || !song.getAlternativeID().equals(song.getId())) && (song = getSong(song.getAlternativeID())) != null ? download(song, out) : false
        }
    }

    List<didawn.gson.Track> searchTracks(String query, int limit = 10) throws InterruptedException {
        List<didawn.gson.Track> tracks = null

        try {
            String url = "${apiUrl()}/search/track?q=${encode(query, UTF8)}&limit=${limit}"
            String searchResponse = get(url, asList(BROWSER_HEADERS))
            if (searchResponse != null) {
                JSONObject trackResponse = JSON.parse(searchResponse)
                def error = trackResponse.error
                if (error != null) {
                    if (error.code == 4) {
                        sleep(WAIT_TIME)
                        return searchTracks(query, limit)
                    }
                } else {
                    JSONArray data = trackResponse.data
                    tracks = Arrays.asList(new Gson().fromJson(data.toString(), didawn.gson.Track[].class))
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.warn e
        }

        return tracks == null ? Collections.emptyList() : getTrackExtra(tracks)
    }

    List<didawn.gson.Album> searchAlbums(String searchTerm, int limit = 10) throws InterruptedException {
        List<didawn.gson.Album> albumsJson = null
        try {
            String url = "${apiUrl()}/search/album?q=${URLEncoder.encode(searchTerm, UTF8)}&limit=${limit}"
            String searchResponse = get(url, asList(BROWSER_HEADERS))
            if (searchResponse != null) {
                JSONObject jsonResponse = JSON.parse(searchResponse)
                def error = jsonResponse.error
                if (error != null) {
                    if (error.code == 4) {
                        sleep(WAIT_TIME)
                        return searchAlbums(searchTerm, limit)
                    }
                }

                JSONArray data = jsonResponse.data
                albumsJson = Arrays.asList(new Gson().fromJson(data.toString(), didawn.gson.Album[].class))
            }
        } catch (UnsupportedEncodingException e) {
            log.warn "UTF-8 not supported", e
        }
        return albumsJson
    }

    List<Song> searchAlbumTracks(String searchTerm) throws InterruptedException {
        List<Song> songList = new ArrayList<>()
        List<didawn.models.Album> albums = searchAlbums(searchTerm)
        for (didawn.models.Album album : albums) {
            List<Song> tracks = findTracksByAlbumId(album.getId())
            songList.addAll(tracks)
        }
        return songList ?: Collections.emptyList()
    }

    List<Song> findTracksByAlbumId(String albumId) {
        String url = "${apiUrl()}/album/" + albumId
        return getAlbumTracks(url)
    }

    List<Song> query(String searchTerm) throws InterruptedException {
        List<Song> trackList = new ArrayList<>()
        String re1 = ".*?"
        String re2 = "(www|api)"
        String re3 = "(\\." + dawn() + "\\.com  / ) "
        String re5 = "(playlist)"
        String re6 = "(/)"
        String re7 = "(\\d+)"
        Pattern p = Pattern.compile(re1 + re2 + re3 + re5 + re6 + re7, Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
        Matcher m = p.matcher(searchTerm)
        if (m.find()) {
            trackList = getPlaylist("${apiUrl()}/playlist/" + m.group(5))
        } else {
            re5 = "(album)"
            p = Pattern.compile(re1 + re2 + re3 + re5 + re6 + re7, Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
            m = p.matcher(searchTerm)
            if (m.find()) {
                trackList = getAlbumTracks("${apiUrl()}/album/" + m.group(5))
            } else {
                re5 = "(artist)"
                p = Pattern.compile(re1 + re2 + re3 + re5 + re6 + re7, Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
                m = p.matcher(searchTerm)
                if (m.find()) {
                    trackList = getArtist("${apiUrl()}/artist/" + m.group(5) + "/albums")
                } else {
                    re5 = "(track)"
                    re7 = "((-?)\\d+)"
                    p = Pattern.compile(re1 + re2 + re3 + re5 + re6 + re7, Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
                    m = p.matcher(searchTerm)
                    if (m.find()) {
                        Song s = getSong(m.group(5))
                        if (s != null) {
                            trackList.add(s)
                        }
                    } else {
                        trackList = searchTracks(searchTerm)
                    }
                }
            }
        }

        trackList = trackList.unique { it.md5 }.sort { s1, s2 ->
            if (s1.getAlbum().equals(s2.getAlbum())) {
                return s1.getDiskNumber() == s2.getDiskNumber() ? s1.getTrackNumber().compareTo(s2.getTrackNumber())
                        : s1.getDiskNumber() - s2.getDiskNumber()
            } else {
                return s1.getAlbum().compareTo(s2.getAlbum())
            }
        }

        return trackList
    }
}
