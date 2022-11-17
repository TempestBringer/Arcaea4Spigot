package tempestissimo.club.arcaea.utils.entities.note_related;

public class Timing {
    public Integer t;
    public Float bpm;
    public Float beats;

    public Timing(Integer t, Float bpm, Float beats) {
        this.t = t;
        this.bpm = bpm;
        this.beats = beats;
    }

    @Override
    public String toString() {
        return "Timing{" +
                "t=" + t +
                ", bpm=" + bpm +
                ", beats=" + beats +
                '}';
    }
}
