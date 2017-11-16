package didawn

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.wrapper.spotify.Api
import com.wrapper.spotify.models.Playlist
import com.wrapper.spotify.models.PlaylistTrack
import com.wrapper.spotify.models.User
import grails.transaction.Transactional
import org.apache.commons.codec.binary.Base64
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair

import javax.servlet.http.HttpServletRequest

@Transactional
class SpService {

    def grailsApplication

    public static final String ALWAYS_ASK_TO_LOGIN = false

    URI buildAuthorizeUri() {
        URIBuilder builder = new URIBuilder(sp().authorizeUrl)
        builder.addParameter("response_type", "code")
        builder.addParameter("client_id", sp().clientId)
        builder.addParameter("scope", "playlist-read-private user-library-read")
        if (ALWAYS_ASK_TO_LOGIN) {
            builder.addParameter("show_dialog", "true")
        }
        builder.addParameter("redirect_uri", sp().redirectUri)
        builder.build()
    }

    String handleCallbackRequest(HttpServletRequest httpServletRequest) {
        String code = httpServletRequest.getParameter("code")

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(sp().apiTokenUrl)

        // add header
        String auth = Base64.encodeBase64String((sp().clientId + ":" + sp().secret).getBytes())
        post.setHeader("Authorization", "Basic " + auth)

        List<NameValuePair> urlParameters = new ArrayList<>()
        urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"))
        urlParameters.add(new BasicNameValuePair("code", code))
        urlParameters.add(new BasicNameValuePair("redirect_uri", sp().redirectUri))

        post.setEntity(new UrlEncodedFormEntity(urlParameters))

        HttpResponse httpResponse = client.execute(post)

        JsonReader reader = new JsonReader(new InputStreamReader(httpResponse.getEntity().getContent()))
        SpAuthResponse spitofyAuthResponse = new Gson().fromJson(reader, SpAuthResponse.class)

        spitofyAuthResponse.accessToken
    }

    private def sp() {
        grailsApplication.config.sp
    }

    Api api(String token) {
        Api.builder().accessToken(token).build()
    }

    String getUserId(String token) {
        getMe(token).getId()
    }

    List<Playlist> getPlaylists(String token) {
        String userId = getUserId(token)
        List<Playlist> playlists = new ArrayList<>()
        def p = api(token).getPlaylistsForUser(userId).build().get()
        playlists.addAll(p.items)
        while (p.next) {
            log.info p.next
            p = api(token).getPlaylistsForUser(userId).offset(playlists.size()).build().get()
            playlists.addAll(p.items)
        }
        playlists
    }

    User getMe(String token) {
        api(token).getMe().build().get()
    }

    Playlist getPlaylist(String token, String userId, String playlistId) {
        def d = api(token).getPlaylist(userId, playlistId).build()
        log.info d.toUrl().toString()
        d.get()
    }

    List<PlaylistTrack> getTracks(String token, String userId, String playlistId) {
        List<PlaylistTrack> tracks = new ArrayList<>()
        def d = api(token).getPlaylistTracks(userId, playlistId).build()
        log.info d.toUrl().toString()
        def p = d.get()
        while (p.next) {
            log.info p.next
            p = api(token).getPlaylistTracks(userId, playlistId).offset(tracks.size()).build().get()
            tracks.addAll(p.items)
        }
        tracks
    }
}
