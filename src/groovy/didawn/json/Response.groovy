package didawn.json

import com.google.gson.annotations.SerializedName

public class Response {

    @SerializedName("results")
    private Results results

    public Results getResults() {
        return results
    }

    public void setResults(Results results) {
        this.results = results
    }
}
