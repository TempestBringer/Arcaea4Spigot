package tempestissimo.club.arcaea.utils.entities.note_related;

import java.util.ArrayList;
import java.util.List;

public class Arc {
    public Integer t1;
    public Integer t2;
    public Double x1;
    public Double x2;
    public String easing;
    public Double y1;
    public Double y2;
    public Integer color;
    public String fx;
    public Boolean skylineBoolean;
    public List<Integer> arctaplist;
    public Double pi = 3.1415926;

    public ArrayList<Double[]> getPosition(Integer curTimeInMs, Double zero_time_arc_play_dense){
        ArrayList<Double[]> results = new ArrayList<>();
        if (t1.equals(t2)){// 0时长物件
            return this.arcZero(zero_time_arc_play_dense);
        }else if (this.easing.equalsIgnoreCase("s")){
            return this.arcS(curTimeInMs);
        }else if (this.easing.equalsIgnoreCase("b")){
            return this.arcB(curTimeInMs);
        }else if (this.easing.equalsIgnoreCase("si")){
            return this.arcSi(curTimeInMs);
        }else if (this.easing.equalsIgnoreCase("so")){
            return this.arcSo(curTimeInMs);
        }else if (this.easing.equalsIgnoreCase("sisi")){
            return this.arcSiSi(curTimeInMs);
        }else if (this.easing.equalsIgnoreCase("siso")){
            return this.arcSiSo(curTimeInMs);
        }else if (this.easing.equalsIgnoreCase("sosi")){
            return this.arcSoSi(curTimeInMs);
        }else if (this.easing.equalsIgnoreCase("soso")){
            return this.arcSoSo(curTimeInMs);
        }else{
            return results;// 空列表
        }
    }

    public ArrayList<Double[]> arcZero(Double zero_time_arc_play_dense){
        ArrayList<Double[]> results = new ArrayList<>();
        double deltaX = (this.x2-this.x1)/zero_time_arc_play_dense;
        double deltaY = (this.y2-this.y1)/zero_time_arc_play_dense;
        for (int i = 0; i < zero_time_arc_play_dense; i++) {
            Double result[] = new Double[2];
            result[0] = i * deltaX + this.x1;
            result[1] = i * deltaY + this.y1;
            results.add(result);
        }
        return results;
    }

    /**
     * 计算Easing为S的Arc在各个时间点上的位置
     * @param curTimeInMs
     * @return
     */
    public ArrayList<Double[]> arcS(Integer curTimeInMs){
        ArrayList<Double[]> results = new ArrayList<>();
        Double[] result = new Double[2];
        double deltaTime= this.t2 - this.t1;
        double deltaX = (this.x2-this.x1)/deltaTime;
        double deltaY = (this.y2-this.y1)/deltaTime;
        result[0]=(curTimeInMs-this.t1)*deltaX+this.x1;
        result[1]=(curTimeInMs-this.t1)*deltaY+this.y1;
        results.add(result);
        return results;
    }

    /**
     * 计算Easing为B的Arc在各个时间点上的位置
     * @param curTimeInMs
     * @return
     */
    public ArrayList<Double[]> arcB(Integer curTimeInMs){
        ArrayList<Double[]> results = new ArrayList<>();
        Double[] result = new Double[2];

        double midTime= (this.t2 + this.t1)/2;
        double midX= (this.x2 + this.x1)/2;
        double midY= (this.y2 + this.y1)/2;
        if (curTimeInMs<midTime) {
            //arcSoSo
            double deltaTime= midTime - this.t1;
            result[0]=this.x1+(midX-this.x1)*(1-Math.cos(0.5*pi*(curTimeInMs-this.t1)/deltaTime));
            result[1]=this.y1+(midY-this.y1)*(1-Math.cos(0.5*pi*(curTimeInMs-this.t1)/deltaTime));
            results.add(result);
            return results;
        }else{
            //arcSiSi
            double deltaTime= this.t2 - midTime;
            result[0]=midX+(this.x2-midX)*Math.sin(0.5*pi*(curTimeInMs-midTime)/deltaTime);
            result[1]=midY+(this.y2-midY)*Math.sin(0.5*pi*(curTimeInMs-midTime)/deltaTime);
            results.add(result);
            return results;
        }
    }
    /**
     * 计算Easing为Si的Arc在各个时间点上的位置
     * @param curTimeInMs
     * @return
     */
    public ArrayList<Double[]> arcSi(Integer curTimeInMs){
        ArrayList<Double[]> results = new ArrayList<>();
        Double[] result = new Double[2];
        double deltaTime= this.t2 - this.t1;
        result[0]=this.x1+(this.x2-this.x1)*Math.sin(0.5*pi*(curTimeInMs-this.t1)/deltaTime);
        result[1]=this.y1;
        results.add(result);
        return results;
    }

    /**
     * 计算Easing为So的Arc在各个时间点上的位置
     * @param curTimeInMs
     * @return
     */
    public ArrayList<Double[]> arcSo(Integer curTimeInMs){
        ArrayList<Double[]> results = new ArrayList<>();
        Double[] result = new Double[2];
        double deltaTime= this.t2 - this.t1;
        result[0]=this.x1+(this.x2-this.x1)*(1-Math.sin(0.5*pi*(curTimeInMs-this.t1)/deltaTime));
        result[1]=this.y1;
        results.add(result);
        return results;
    }

    /**
     * 计算Easing为SiSi的Arc在各个时间点上的位置
     * @param curTimeInMs
     * @return
     */
    public ArrayList<Double[]> arcSiSi(Integer curTimeInMs){
        ArrayList<Double[]> results = new ArrayList<>();
        Double[] result = new Double[2];
        double deltaTime= this.t2 - this.t1;
        result[0]=this.x1+(this.x2-this.x1)*Math.sin(0.5*pi*(curTimeInMs-this.t1)/deltaTime);
        result[1]=this.y1+(this.y2-this.y1)*Math.sin(0.5*pi*(curTimeInMs-this.t1)/deltaTime);
        results.add(result);
        return results;
    }

    /**
     * 计算Easing为SiSo的Arc在各个时间点上的位置
     * @param curTimeInMs
     * @return
     */
    public ArrayList<Double[]> arcSiSo(Integer curTimeInMs){
        ArrayList<Double[]> results = new ArrayList<>();
        Double[] result = new Double[2];
        double deltaTime= this.t2 - this.t1;
        result[0]=this.x1+(this.x2-this.x1)*Math.sin(0.5*pi*(curTimeInMs-this.t1)/deltaTime);
        result[1]=this.y1+(this.y2-this.y1)*(1-Math.sin(0.5*pi*(curTimeInMs-this.t1)/deltaTime));
        results.add(result);
        return results;
    }

    /**
     * 计算Easing为SoSi的Arc在各个时间点上的位置
     * @param curTimeInMs
     * @return
     */
    public ArrayList<Double[]> arcSoSi(Integer curTimeInMs){
        ArrayList<Double[]> results = new ArrayList<>();
        Double[] result = new Double[2];
        double deltaTime= this.t2 - this.t1;
        result[0]=this.x1+(this.x2-this.x1)*(1-Math.sin(0.5*pi*(curTimeInMs-this.t1)/deltaTime));
        result[1]=this.y1+(this.y2-this.y1)*Math.sin(0.5*pi*(curTimeInMs-this.t1)/deltaTime);
        results.add(result);
        return results;
    }

    /**
     * 计算Easing为SoSo的Arc在各个时间点上的位置
     * @param curTimeInMs
     * @return
     */
    public ArrayList<Double[]> arcSoSo(Integer curTimeInMs){
        ArrayList<Double[]> results = new ArrayList<>();
        Double[] result = new Double[2];
        double deltaTime= this.t2 - this.t1;
        result[0]=this.x1+(this.x2-this.x1)*(1-Math.sin(0.5*pi*(curTimeInMs-this.t1)/deltaTime));
        result[1]=this.y1+(this.y2-this.y1)*(1-Math.sin(0.5*pi*(curTimeInMs-this.t1)/deltaTime));
        results.add(result);
        return results;
    }


    public Arc(Integer t1, Integer t2, Double x1, Double x2, String easing, Double y1, Double y2, Integer color, String fx, Boolean skylineBoolean, List<Integer> arctaplist) {
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
