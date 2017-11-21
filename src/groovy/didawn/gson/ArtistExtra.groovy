package didawn.gson

import com.google.gson.annotations.SerializedName

class ArtistExtra {

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
}
