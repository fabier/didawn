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

    TrackExtra trackExtra

    String data
}
