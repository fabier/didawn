package didawn

import com.google.gson.Gson
import didawn.gson.*
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

    CryptoService cryptoService

    DiWsService diWsService

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

    String getCoverUrl(Track track) {
        format("http://cdn-images.%s/images/cover/%s/500x500-000000-80-0-0.jpg", dawnDotCom(), track.ALB_PICTURE)
    }

    String getDownloadUrl(Track track) {
        return cryptoService.getDownloadURL(
                track.getMD5_ORIGIN(),
                getFormat(track),
                track.getSNG_ID(),
                track.getMEDIA_VERSION()
        )
    }

    String getDownloadUrlEnd(Track track) {
        track == null ? null : cryptoService.getDownloadUrlEnd(
                track.getMD5_ORIGIN(),
                getFormat(track),
                track.getSNG_ID(),
                track.getMEDIA_VERSION()
        )
    }

    boolean isUserTrack(Track track) {
        track.getSNG_ID() < 0
    }

    int getFormat(Track  track) {
        if (isUserTrack(track)) {
            return 0;
        } else {
            return track.getFILESIZE_MP3_320() > 0L ? 3 : track.getFILESIZE_MP3_256() > 0L ? 5 : 1;
        }
    }

    boolean download(Track track, OutputStream outputStream) {
        download(
                track.getMD5_ORIGIN(),
                getFormat(track),
                Long.toString(track.getSNG_ID()),
                track.getMEDIA_VERSION(),
                outputStream
        )
    }

    boolean download(String md5, int format, String id, int mediaVersion, OutputStream outputStream) {
        download(id, getDownloadUrl(md5, format, id, mediaVersion), outputStream)
    }

    String getDownloadUrl(String md5, int format, String id, int mediaVersion) {
        return cryptoService.getDownloadURL(md5, format, id, mediaVersion)
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

    Track getTrackExtra(String id) {
        String token = newApiToken()
        String responseAsText = post("${wwwUrl()}/ajax/gw-light.php?api_version=1.0&api_token=" + token + "&input=3", "[{\"method\":\"song.getListData\",\"params\":{\"sng_ids\":[" + id + "]}}]", asList(BROWSER_HEADERS))
        if (responseAsText == null || responseAsText.equals("[{\"error\":{\"REQUEST_ERROR\":\"Wrong parameters\"},\"results\":{}}]")) {
            return null
        } else {
            JSONObject response = JSON.parse(responseAsText) as JSONObject
            JSONArray results = response.results.tracks
            if (results.isEmpty()) {
                return null
            } else {
                return new Gson().fromJson(results.first().toString(), Track.class)
            }
        }
    }

    List<Track> getPlaylist(String playlistId, int limit = 100) throws InterruptedException {
        String url = format("%s/playlist/%s?limit=%d", apiUrl(), playlistId, limit)
        Playlist playlist = diWsService.callApi(Playlist.class, url)
        List<Track> tracks = playlist.tracklist.tracks
        return getTrackExtra(tracks)
    }

    // TODO gérer "next"
    List<Track> getPlaylistTracks(String playlistId, int index = 0, int limit = 100) throws InterruptedException {
        String url = format("%s/playlist/%s/tracks?index=%d&limit=%d", apiUrl(), playlistId, index, limit)
        List<Track> tracks = diWsService.callApiAsList(Track.class, url)
        return getTrackExtra(tracks)
    }

    List<Track> getArtistTracks(String artistId) throws InterruptedException {
        String url = format("%s/artist/%s/albums", apiUrl(), artistId)
        List<Album> albums = diWsService.callApiAsList(Album.class, url)
        List<Track> tracks = new ArrayList<>()
        albums.each {
            tracks.addAll(getAlbumTracks(Long.toString(it.id)))
        }
        return getTrackExtra(tracks)
    }

    List<Track> getTrackExtra(List<Track> trackList) {
        boolean forceTrackExtra = true
        if (forceTrackExtra || Environment.current == Environment.PRODUCTION) {
            StringBuilder sb = new StringBuilder("[{\"method\":\"song.getListData\",\"params\":{\"sng_ids\":[")
            sb.append(trackList.collect { it.id }.join(","))
            sb.append("]}}]")
            String ids = sb.toString()

            String url = "${wwwUrl()}/ajax/gw-light.php?api_version=1.0&api_token=" +
                    newApiToken() + "&input=3&cid=" + randomAlphanumeric(18).toLowerCase()
            String extraInfoResponse = post(url, ids, asList(BROWSER_HEADERS))

            JSONObject extraInfoJSON = JSON.parse(extraInfoResponse) as JSONObject

            JSONArray data = extraInfoJSON.results.data
            List<Track> trackExtraList = asList(new Gson().fromJson(data.toString(), Track[].class)) as List<Track>

            trackList.each { t ->
                long trackId = t.getId()
                Track trackExtra = trackExtraList.find { trackId == it.SNG_ID }
                log.info t.SNG_ID
                t << trackExtra
                log.info t.SNG_ID
            }
        }
        return trackList
    }

    // TODO return extra infos
    List<Track> getAlbumTracks(String albumId) throws InterruptedException {
        String url = format("%s/album/%s", apiUrl(), albumId)
        Album album = diWsService.callApi(Album.class, url)
        album.tracks.tracks
    }

    List<Artist> searchArtists(String searchTerm, int limit = 25) throws InterruptedException {
        String url = format("%s/search/artist?q=%s&limit=%d", apiUrl(), encode(searchTerm, UTF8), limit)
        return diWsService.callApiAsList(Artist.class, url)
    }

    List<Album> searchAlbums(String searchTerm, int limit = 25) throws InterruptedException {
        String url = format("%s/search/album?q=%s&limit=%d", apiUrl(), encode(searchTerm, UTF8), limit)
        return diWsService.callApiAsList(Album.class, url)
    }

    List<Track> searchTracks(String query, int limit = 25) {
        String url = format("%s/search/track?q=%s&limit=%d", apiUrl(), encode(query, UTF8), limit)
        List<Track> tracks = diWsService.callApiAsList(Track.class, url)
        return getTrackExtra(tracks);
    }

    List<Track> searchAlbumTracks(String searchTerm) throws InterruptedException {
        List<Track> tracks = new ArrayList<>()
        List<Album> albums = searchAlbums(searchTerm)
        albums.each {
            tracks.addAll(getAlbumTracks(Long.toString(it.id)))
        }
        return tracks ?: Collections.emptyList()
    }

    List<Track> query(String searchTerm) throws InterruptedException {
        List<Track> trackList = new ArrayList<>()
        String re1 = ".*?"
        String re2 = "(www|api)"
        String re3 = "(\\." + dawn() + "\\.com  / ) "
        String re5 = "(playlist)"
        String re6 = "(/)"
        String re7 = "(\\d+)"
        Pattern p = Pattern.compile(re1 + re2 + re3 + re5 + re6 + re7, Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
        Matcher m = p.matcher(searchTerm)
        if (m.find()) {
            trackList = getPlaylist(m.group(5))
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
                    trackList = getArtistTracks(m.group(5))
                } else {
                    re5 = "(track)"
                    re7 = "((-?)\\d+)"
                    p = Pattern.compile(re1 + re2 + re3 + re5 + re6 + re7, Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
                    m = p.matcher(searchTerm)
                    if (m.find()) {
                        Track s = getTrackExtra(m.group(5))
                        if (s != null) {
                            trackList.add(s)
                        }
                    } else {
                        trackList = searchTracks(searchTerm)
                    }
                }
            }
        }

        trackList = trackList.unique { it.id }.sort { s1, s2 ->
            if (s1.getAlbum().equals(s2.getAlbum())) {
                return s1.getDISK_NUMBER() == s2.getDISK_NUMBER() ?
                        s1.getTRACK_NUMBER() - s2.getTRACK_NUMBER() :
                        s1.getDISK_NUMBER() - s2.getDISK_NUMBER()
            } else {
                return s1.album.title.compareTo(s2.album.title)
            }
        }

        return trackList
    }
}
