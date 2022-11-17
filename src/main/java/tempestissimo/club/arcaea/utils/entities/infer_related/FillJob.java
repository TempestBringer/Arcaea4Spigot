package tempestissimo.club.arcaea.utils.entities.infer_related;

public class FillJob {
    public String type;             //what kind of note create this job
    public Integer priority;        //priority to obtain a block, lower is more important
    public Integer frame;           //render in how many ticks later
    public Boolean behind_line;     //false = before_input_line, true = behind_input_line
    public Integer x_low;
    public Integer x_high;
    public Integer y_low;
    public Integer y_high;
    public Integer z_low;
    public Integer z_high;
    public String material;

    public FillJob(String type, Integer priority, Integer frame, Boolean behind_line) {
        this.type = type;
        this.priority = priority;
        this.frame = frame;
        this.behind_line = behind_line;
        this.x_low=0;
        this.x_high=0;
        this.y_low=0;
        this.y_high=0;
        this.z_low=0;
        this.z_high=0;
        this.material="";
    }

    @Override
    public String toString() {
        return "FillJob{" +
                "type='" + type + '\'' +
                ", priority=" + priority +
                ", frame=" + frame +
                ", behind_line=" + behind_line +
                ", x_low=" + x_low +
                ", x_high=" + x_high +
                ", y_low=" + y_low +
                ", y_high=" + y_high +
                ", z_low=" + z_low +
                ", z_high=" + z_high +
                ", material='" + material + '\'' +
                '}';
    }
}
