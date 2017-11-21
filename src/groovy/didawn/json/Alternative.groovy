package didawn.json

import com.google.gson.annotations.SerializedName

public class Alternative {

    @SerializedName("id")
    private String id

    public String getID() {
        return id
    }

    public void setID(String id) {
        this.id = id
    }
}
