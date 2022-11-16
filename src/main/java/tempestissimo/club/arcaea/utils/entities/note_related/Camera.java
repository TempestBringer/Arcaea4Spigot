package tempestissimo.club.arcaea.utils.entities.note_related;

public class Camera {
    public Integer t;
    public Float tranverse;
    public Float bottomzoom;
    public Float linezoom;
    public Float steadyangle;
    public Float topzoom;
    public Float angle;
    public String easing; //[qi,qo,l,reset,s]
    public Integer lastingtime;

    public Camera(Integer t, Float tranverse, Float bottomzoom, Float linezoom, Float steadyangle, Float topzoom, Float angle, String easing, Integer lastingtime) {
        this.t = t;
        this.tranverse = tranverse;
        this.bottomzoom = bottomzoom;
        this.linezoom = linezoom;
        this.steadyangle = steadyangle;
        this.topzoom = topzoom;
        this.angle = angle;
        this.easing = easing;
        this.lastingtime = lastingtime;
    }
}
