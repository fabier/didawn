package didawn.gson

import com.google.gson.annotations.SerializedName

class Album extends BaseJSON {

    @SerializedName("id")
    long id

    @SerializedName("title")
    String title

    @SerializedName("link")
    String link

    @SerializedName("share")
    String share

    @SerializedName("cover")
    String cover

    @SerializedName("cover_small")
    String coverSmall

    @SerializedName("cover_medium")
    String coverMedium

    @SerializedName("cover_big")
    String coverBig

    @SerializedName("cover_xl")
    String coverXl

    @SerializedName("genre_id")
    int genreId

    @SerializedName("genres")
    GenreList genres

    @SerializedName("label")
    String label

    @SerializedName("duration")
    int duration

    @SerializedName("fans")
    long fans

    @SerializedName("rating")
    int rating

    @SerializedName("release_date")
    String releaseDate

    @SerializedName("nb_tracks")
    int nbTracks

    @SerializedName("record_type")
    String recordType

    @SerializedName("available")
    boolean available

    @SerializedName("tracklist")
    String tracklist

    @SerializedName("explicit_lyrics")
    boolean explicitLyrics

    @SerializedName("contributors")
    List<Artist> contributors

    @SerializedName("artist")
    Artist artist

    @SerializedName("type")
    String type

    @SerializedName("tracks")
    TrackList tracks
}
