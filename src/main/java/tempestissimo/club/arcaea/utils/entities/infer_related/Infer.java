package tempestissimo.club.arcaea.utils.entities.infer_related;

public class Infer implements Comparable{
    public Integer frame;
    public Double position;

    public Infer(Integer frame, Double position) {
        this.frame = frame;
        this.position = position;
    }

    @Override
    public int compareTo(Object o) {
        Infer other = (Infer) o;
        if(this.frame<other.frame)
            return 1;
        else if (this.frame == other.frame) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "Infer{" +
                "frame=" + frame +
                ", position=" + position +
                '}';
    }
}
