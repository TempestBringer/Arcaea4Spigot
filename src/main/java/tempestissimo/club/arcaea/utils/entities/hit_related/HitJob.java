package tempestissimo.club.arcaea.utils.entities.hit_related;

import java.util.ArrayList;

/**
 * 判定时间
 */
public class HitJob {
    public Long frame;
    public Double x;
    public Double y;
    public Double z;
    public ArrayList<String> keys;

    public HitJob(Long frame, Double x, Double y, Double z) {
        this.frame = frame;
        this.x = x;
        this.y = y;
        this.z = z;
        this.keys = new ArrayList<>();
    }

    public HitJob() {}

    @Override
    public String toString() {
        return "HitJob{" +
                "frame=" + frame +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", keys=" + keys +
                '}';
    }
}
