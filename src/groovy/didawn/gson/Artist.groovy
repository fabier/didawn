package didawn.gson

import com.google.gson.annotations.SerializedName

class Artist extends BaseJSON {

    @SerializedName("id")
    long id

    @SerializedName("name")
    String name

    @SerializedName("link")
    String link

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

    @SerializedName("tracklist")
    String tracklist

    @SerializedName("type")
    String type

    // ############################ ARTIST EXTRA #################### BEGIN
    @SerializedName("ART_ID")
    long ART_ID

    @SerializedName("ROLE_ID")
    int ROLE_ID

    @SerializedName("ARTISTS_SONGS_ORDER")
    int ARTISTS_SONGS_ORDER

    @SerializedName("ART_NAME")
    String ART_NAME

    @SerializedName("ART_PICTURE")
    String ART_PICTURE

    @SerializedName("SMARTRADIO")
    int SMARTRADIO

    @SerializedName("RANK")
    long RANK

    @SerializedName("__TYPE__")
    String __TYPE__
    // ############################ ARTIST EXTRA #################### END
}
