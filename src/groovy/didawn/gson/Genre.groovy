package didawn.gson

import com.google.gson.annotations.SerializedName

class Genre extends BaseJSON {

    @SerializedName("id")
    long id

    @SerializedName("name")
    String name

    @SerializedName("picture")
    String picture

    @SerializedName("type")
    String type
}
