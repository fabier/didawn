package didawn.gson

import com.google.gson.annotations.SerializedName

class Artist {

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
}
