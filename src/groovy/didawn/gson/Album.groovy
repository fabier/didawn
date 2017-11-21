package didawn.gson

import com.google.gson.annotations.SerializedName

class Album {

    @SerializedName("id")
    long id

    @SerializedName("title")
    String title

    @SerializedName("link")
    String link

    @SerializedName("cover")
    String cover

    @SerializedName("cover_small")
    String cover_small

    @SerializedName("cover_medium")
    String cover_medium

    @SerializedName("cover_big")
    String cover_big

    @SerializedName("cover_xl")
    String coverXl

    @SerializedName("genre_id")
    int genreId

    @SerializedName("nb_tracks")
    int nbTracks

    @SerializedName("record_type")
    String recordType

    @SerializedName("tracklist")
    String tracklist

    @SerializedName("explicit_lyrics")
    boolean explicitLyrics

    @SerializedName("artist")
    Artist artist

    @SerializedName("type")
    String type
}
