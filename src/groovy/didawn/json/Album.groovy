package didawn.json

import com.google.gson.annotations.SerializedName

class Album {

    @SerializedName("title")
    private String title

    @SerializedName("cover_xl")
    private String coverXL

    @SerializedName("cover_big")
    private String coverBig

    public String getTitle() {
        return title
    }

    public void setTitle(String title) {
        this.title = title
    }

    public String getCoverXL() {
        return coverXL
    }

    public void setCoverXL(String coverXL) {
        this.coverXL = coverXL
    }

    public String getCoverBig() {
        return coverBig
    }

    public void setCoverBig(String coverBig) {
        this.coverBig = coverBig
    }
}
