package didawn.models

public class ArtistList extends ArrayList<String> {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.size(); ++i) {
            sb.append(this.get(i));
            if (i < this.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean contains(Object o) {
        String s2 = o.toString().toLowerCase();
        for (int i = 0; i < this.size(); ++i) {
            String s = this.get(i).toLowerCase();
            if (s2.equals(s)) {
                return true;
            }
        }
        return false;
    }
}
