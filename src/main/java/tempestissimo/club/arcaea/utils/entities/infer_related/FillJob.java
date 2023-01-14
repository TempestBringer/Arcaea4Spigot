package tempestissimo.club.arcaea.utils.entities.infer_related;

public class FillJob{
    public String type;             //what kind of note create this job
    public Integer priority;        //priority to obtain a block, lower is more important
    public Integer frame;           //render in how many ticks later
    public Boolean behind_line;     //false = before_input_line, true = behind_input_line
    public double x_low;
    public double x_high;
    public double y_low;
    public double y_high;
    public double z_low;
    public double z_high;
    public String material;
    public String jobName;

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
        this.jobName="";
    }

    public FillJob(String type, Integer priority, Integer frame, Boolean behind_line, Integer x_low, Integer x_high, Integer y_low, Integer y_high, Integer z_low, Integer z_high, String material, String jobName) {
        this.type = type;
        this.priority = priority;
        this.frame = frame;
        this.behind_line = behind_line;
        if(x_low>x_high){
            this.x_low = x_high;
            this.x_high = x_low;
        }else{
            this.x_low = x_low;
            this.x_high = x_high;
        }
        if(y_low>y_high){
            this.y_low = y_high;
            this.y_high = y_low;
        }else{
            this.y_low = y_low;
            this.y_high = y_high;
        }
        if(z_low>z_high){
            this.z_low = z_high;
            this.z_high = z_low;
        }else{
            this.z_low = z_low;
            this.z_high = z_high;
        }
        this.material = material;
        this.jobName = jobName;
    }

    public FillJob(String type, int priority, Integer curFrame, boolean behind_line, Double startX, Double startY, Double startZ, String particle, String jobName) {
        this.type = type;
        this.priority = priority;
        this.frame = curFrame;
        this.behind_line = behind_line;
        this.x_low = startX;
        this.x_high = startX;
        this.y_low = startY;
        this.y_high = startY;
        this.z_low = startZ;
        this.z_high = startZ;
        this.material = particle;
        this.jobName = jobName;
    }

//    public FillJob(String type, Integer priority, Integer frame, Boolean behind_line, double x_low, double x_high, double y_low, double y_high, double z_low, double z_high, String material, String jobName) {
//        this.type = type;
//        this.priority = priority;
//        this.frame = frame;
//        this.behind_line = behind_line;
//        this.x_low = x_low;
//        this.x_high = x_high;
//        this.y_low = y_low;
//        this.y_high = y_high;
//        this.z_low = z_low;
//        this.z_high = z_high;
//        this.material = material;
//        this.jobName = jobName;
//    }

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
                ", jobName='" + jobName + '\'' +
                '}';
    }

}
