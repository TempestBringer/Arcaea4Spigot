package tempestissimo.club.arcaea.utils.entities.note_related;

public class Scenecontrol {
    public Integer t;
    public String type;
    public Float param1;
    public Integer param2;

    public Scenecontrol(Integer t, String type, Float param1, Integer param2) {
        this.t = t;
        this.type = type;
        this.param1 = param1;
        this.param2 = param2;
    }

    @Override
    public String toString() {
        return "Scenecontrol{" +
                "t=" + t +
                ", type='" + type + '\'' +
                ", param1=" + param1 +
                ", param2=" + param2 +
                '}';
    }
}
