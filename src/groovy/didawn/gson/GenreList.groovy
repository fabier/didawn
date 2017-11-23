package didawn.gson

import com.google.gson.annotations.SerializedName

class GenreList extends BaseJSON {

    @SerializedName("data")
    List<Genre> genres
}
