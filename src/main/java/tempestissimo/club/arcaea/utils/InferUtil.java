package tempestissimo.club.arcaea.utils;

import tempestissimo.club.arcaea.utils.entities.Song;
import tempestissimo.club.arcaea.utils.entities.infer_related.Infer;
import tempestissimo.club.arcaea.utils.entities.note_related.Scenecontrol;
import tempestissimo.club.arcaea.utils.entities.note_related.Timing;

import java.util.ArrayList;
import java.util.HashMap;

public class InferUtil {
    public static ArrayList<Infer> position_infer(Song song, HashMap<String,String> args, ArrayList<Timing> timings, Integer time){
        ArrayList<Infer> x_s = new ArrayList<>();
        Double frame_time = 1000/Double.parseDouble(args.get("tps"));
        Integer timings_pointer = 0;
        Float bpm_base = song.bpm_base;
        while(timings.get(timings_pointer).t<time){
            timings_pointer+=1;
            if (timings_pointer==timings.size())
                break;
        }
        timings_pointer-=1;
        Float default_speed_per_second = Float.parseFloat(args.get("default_speed_per_second"));
        Double cur_time = Double.valueOf(time);
        Double cur_x = (time%frame_time)*timings.get(timings_pointer).bpm*default_speed_per_second/bpm_base/1000;

        Float upper_limit = Float.parseFloat(args.get("track_x_upper_limit"));
        Float lower_limit = Float.parseFloat(args.get("track_x_lower_limit"));
        Float ground_x = Float.parseFloat(args.get("ground_x"));
        Double forward_frame;
        Integer start_timing,end_timing;
        while((upper_limit+ground_x)>cur_x && cur_x>(lower_limit+ground_x)){
            forward_frame = cur_time - frame_time;
            start_timing = 0;
            end_timing = 0;
            for (int index=0;index<timings.size();index++){
                if (index < timings.size() - 1){
                    if (timings.get(index).t<= forward_frame && forward_frame<= timings.get(index+1).t)
                        start_timing = index;
                    if (timings.get(index).t <= cur_time && cur_time<= timings.get(index+1).t)
                        end_timing = index;
                }

            else {
                    if (timings.get(index).t < forward_frame){
                        start_timing = index;
                        end_timing = index;
                    }
                }
            }
            if (start_timing == end_timing)  //在同一个timing里面
                cur_x += frame_time * timings.get(start_timing).bpm * default_speed_per_second / bpm_base / 1000;
            else{
                for (int index=start_timing;index<end_timing+1;index++){
                    if(index==start_timing)
                        cur_x+=(timings.get(index+1).t-forward_frame)*timings.get(index).bpm*default_speed_per_second/bpm_base/1000;
                    else if(index==end_timing)
                        cur_x+=(cur_time-timings.get(index).t)*timings.get(index).bpm*default_speed_per_second/bpm_base/1000;
                    else
                        cur_x+=(timings.get(index+1).t-timings.get(index).t)*timings.get(index).bpm*default_speed_per_second/bpm_base/1000;

                }
            }
            x_s.add(new Infer((int)(forward_frame/frame_time),cur_x));
            cur_time-=frame_time;
        }
        return x_s;


    }
    public static Boolean hide_group(HashMap<String,String> args, Integer frame, ArrayList<Scenecontrol> scenecontrols){
        Boolean hide_flag = false;
        Integer closest_frame=0;
        Float tps = Float.valueOf(args.get("tps"));
        Float frame_time = 1000/tps;
        Integer scene_frame=0;
        for (int index=0;index<scenecontrols.size();index++){
            if(scenecontrols.get(index).type== "hidegroup"){
                scene_frame = (int)(scenecontrols.get(index).t/frame_time);
                if(closest_frame <= scene_frame && scene_frame <= frame){
                    closest_frame = scene_frame;
                    if(scenecontrols.get(index).param2==1)
                        hide_flag=true;
                    else if (scenecontrols.get(index).param2==0)
                        hide_flag=false;
                    else{
                        System.out.println(scenecontrols.get(index).toString());
                        System.out.println("非法的hidegroup数值");
                        hide_flag = false;
                    }
                }
            }
        }
        return hide_flag;
    }
}
