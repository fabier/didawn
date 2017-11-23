package didawn.gson

import com.google.gson.annotations.SerializedName

class Playlist extends BaseJSON {

    @SerializedName("id")
    long id

    @SerializedName("title")
    String title

    @SerializedName("description")
    String description

    @SerializedName("duration")
    long duration

    @SerializedName("public")
    boolean isPublic

    @SerializedName("is_loved_track")
    boolean isLovedTrack

    @SerializedName("collaborative")
    boolean collaborative

    @SerializedName("rating")
    long rating

    @SerializedName("nb_tracks")
    int nbTracks

    @SerializedName("fans")
    long fans

    @SerializedName("link")
    String link

    @SerializedName("share")
    String share

    @SerializedName("picture")
    String picture

    @SerializedName("picture_small")
    String pictureSmall

    @SerializedName("picture_medium")
    String pictureMedium

    @SerializedName("picture_big")
    String pictureBig

    @SerializedName("picture_xl")
    String pictureXl

    @SerializedName("checksum")
    String checksum

    @SerializedName("tracklist")
    TrackList tracklist

    @SerializedName("creation_date")
    String creationDate
}
