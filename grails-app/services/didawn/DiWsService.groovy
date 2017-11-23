package didawn

import com.google.gson.Gson
import grails.converters.JSON
import grails.transaction.Transactional
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

import java.lang.reflect.Array
import java.nio.ByteBuffer

import static java.lang.Long.parseLong
import static java.lang.Thread.sleep
import static java.util.Arrays.asList
import static org.apache.commons.lang3.StringUtils.substringBetween
import static org.apache.http.util.EntityUtils.consume

@Transactional
class DiWsService {

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

    private final HttpClient httpClient = HttpClientBuilder.create().build()

    private final String UTF8 = "UTF-8"

    private final WAIT_TIME = 1000

    private String apiToken

    CryptoService cryptoService

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

    def <T> List<T> callApiAsList(Class<T> type, String url) {
        List<T> result = null
        String searchResponse = get(url, asList(BROWSER_HEADERS))
        if (searchResponse != null) {
            JSONObject responseJson = JSON.parse(searchResponse) as JSONObject
            def error = responseJson.error
            if (error != null) {
                if (error.code == 4) {
                    sleep(WAIT_TIME)
                    return callApiAsList(type, url)
                }
            } else {
                JSONArray data = responseJson.data
                result = asList(new Gson().fromJson(data.toString(), Array.newInstance(type, 0).getClass())) as List<T>
            }
        }
        return result ?: Collections.emptyList()
    }


    def <T> T callApi(Class<T> type, String url) {
        T result = null
        String searchResponse = get(url, asList(BROWSER_HEADERS))
        if (searchResponse != null) {
            JSONObject responseJson = JSON.parse(searchResponse) as JSONObject
            def error = responseJson.error
            if (error != null) {
                if (error.code == 4) {
                    sleep(WAIT_TIME)
                    result = callApi(type, url)
                }
            } else {
                result = new Gson().fromJson(searchResponse, type)
            }
        }
        return result
    }
}
