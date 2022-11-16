package tempestissimo.club.arcaea.utils.entities.note_related;

import java.util.List;

public class Arc {
    public Integer t1;
    public Integer t2;
    public Float x1;
    public Float x2;
    public String easing;
    public Float y1;
    public Float y2;
    public Integer color;
    public String fx;
    public Boolean skylineBoolean;
    public List<Integer> arctaplist;

    public Arc(Integer t1, Integer t2, Float x1, Float x2, String easing, Float y1, Float y2, Integer color, String fx, Boolean skylineBoolean, List<Integer> arctaplist) {
        this.t1 = t1;
        this.t2 = t2;
        this.x1 = x1;
        this.x2 = x2;
        this.easing = easing;
        this.y1 = y1;
        this.y2 = y2;
        this.color = color;
        this.fx = fx;
        this.skylineBoolean = skylineBoolean;
        this.arctaplist = arctaplist;
    }
}
