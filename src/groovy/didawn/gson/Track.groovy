package didawn.gson

import com.google.gson.annotations.SerializedName

class Track {

    @SerializedName("id")
    long id

    @SerializedName("readable")
    boolean readable

    @SerializedName("title")
    String title

    @SerializedName("title_short")
    String title_short

    @SerializedName("title_version")
    String title_version

    @SerializedName("link")
    String link

    @SerializedName("duration")
    long duration

    @SerializedName("rank")
    long rank

    @SerializedName("explicit_lyrics")
    boolean explicit_lyrics

    @SerializedName("preview")
    String preview

    @SerializedName("artist")
    Artist artist

    @SerializedName("album")
    Album album

    @SerializedName("type")
    String type

    // ############################ TRACK EXTRA #################### BEGIN
    @SerializedName("SNG_ID")
    long SNG_ID

    @SerializedName("ALB_ID")
    long ALB_ID

    @SerializedName("ALB_PICTURE")
    String ALB_PICTURE

    @SerializedName("ALB_TITLE")
    String ALB_TITLE

    @SerializedName("ARRANGER")
    String ARRANGER

    @SerializedName("ARTISTS")
    List<Artist> artists

    @SerializedName("ART_ID")
    long ART_ID

    @SerializedName("ART_NAME")
    String ART_NAME

    @SerializedName("AUTHOR")
    String AUTHOR

    @SerializedName("BPM")
    float BPM

    @SerializedName("COMPOSER")
    String COMPOSER

    @SerializedName("CREATIVE_COMMON")
    String CREATIVE_COMMON

    @SerializedName("DATE_START")
    String DATE_START

    @SerializedName("DATE_START_PREMIUM")
    String DATE_START_PREMIUM

    @SerializedName("DIGITAL_RELEASE_DATE")
    String DIGITAL_RELEASE_DATE

    @SerializedName("DISK_NUMBER")
    int DISK_NUMBER

    @SerializedName("DURATION")
    long DURATION

    @SerializedName("EXPLICIT_LYRICS")
    String EXPLICIT_LYRICS

    @SerializedName("FILESIZE")
    long FILESIZE

    @SerializedName("FILESIZE_MP3_64")
    long FILESIZE_MP3_64

    @SerializedName("FILESIZE_MP3_128")
    long FILESIZE_MP3_128

    @SerializedName("FILESIZE_MP3_256")
    long FILESIZE_MP3_256

    @SerializedName("FILESIZE_MP3_320")
    long FILESIZE_MP3_320

    @SerializedName("FILESIZE_FLAC")
    long FILESIZE_FLAC

    @SerializedName("FULL_PATH_ORIGIN")
    String FULL_PATH_ORIGIN

    @SerializedName("GAIN")
    String GAIN

    @SerializedName("GENRE_ID")
    String GENRE_ID

    @SerializedName("GRID")
    String GRID

    @SerializedName("INDEXATION_DATE")
    String INDEXATION_DATE

    @SerializedName("ISRC")
    String ISRC

    @SerializedName("KEYWORD")
    String KEYWORD

    @SerializedName("LABEL_ID")
    long LABEL_ID

    @SerializedName("LANG")
    int LANG

    @SerializedName("LYRICS_ID")
    long LYRICS_ID

    @SerializedName("MD5_ORIGIN")
    String MD5_ORIGIN

    @SerializedName("MEDIA_VERSION")
    int MEDIA_VERSION

    @SerializedName("NOTE")
    int NOTE

    @SerializedName("ORIGIN")
    int ORIGIN

    @SerializedName("PERFORMER")
    String PERFORMER

    @SerializedName("PHYSICAL_RELEASE_DATE")
    String PHYSICAL_RELEASE_DATE

    @SerializedName("PROVIDER_ID")
    int PROVIDER_ID

    @SerializedName("PUID")
    String PUID

    @SerializedName("RANK_SNG")
    long RANK_SNG

    @SerializedName("SMARTRADIO")
    int SMARTRADIO

    @SerializedName("SNG_ID_NEW")
    int SNG_ID_NEW

    @SerializedName("SNG_STATUS")
    int SNG_STATUS

    @SerializedName("SNG_TITLE")
    String SNG_TITLE

    @SerializedName("STATUS")
    int STATUS

    @SerializedName("S_MOD")
    int S_MOD

    @SerializedName("S_PREMIUM")
    int S_PREMIUM

    @SerializedName("S_WIDGET")
    int S_WIDGET

    @SerializedName("TRACK_NUMBER")
    int TRACK_NUMBER

    @SerializedName("TYPE")
    int TYPE

    @SerializedName("UPDATE_DATE")
    String UPDATE_DATE

    @SerializedName("UPLOAD_ID")
    int UPLOAD_ID

    @SerializedName("URL_REWRITING")
    String URL_REWRITING

    @SerializedName("USER_ID")
    int USER_ID

    @SerializedName("VERSION")
    String VERSION

    @SerializedName("__TYPE__")
    String __TYPE__
    // ############################ TRACK EXTRA #################### END

    String data
}
