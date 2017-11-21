package didawn.json

import com.google.gson.annotations.SerializedName

import static java.util.Collections.unmodifiableList

public class Datas {

    @SerializedName("data")
    private List<Data> data

    public List<Data> getData() {
        return unmodifiableList(data)
    }

    public void setData(List<Data> data) {
        this.data = data
    }
}
