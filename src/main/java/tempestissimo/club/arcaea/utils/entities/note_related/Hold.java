package tempestissimo.club.arcaea.utils.entities.note_related;

import tempestissimo.club.arcaea.utils.InferUtil;
import tempestissimo.club.arcaea.utils.entities.Song;
import tempestissimo.club.arcaea.utils.entities.infer_related.FillJob;
import tempestissimo.club.arcaea.utils.entities.infer_related.Infer;

import java.util.ArrayList;
import java.util.HashMap;

public class Hold {
    public Integer t1;
    public Integer t2;
    public Integer lane;

    public Hold(Integer t1, Integer t2, Integer lane) {
        this.t1 = t1;
        this.t2 = t2;
        this.lane = lane;
    }
//    public ArrayList<FillJob> get_logic_render(Song song, HashMap<String,String> args, ArrayList<Timing> timings, ArrayList<Scenecontrol> scenecontrols){
//        ArrayList<FillJob> results = new ArrayList<>();
//        ArrayList<Infer> x_front = InferUtil.position_infer(song, args, timings, this.t1);
//        ArrayList<Infer> x_tail = InferUtil.position_infer(song, args, timings, this.t2);
//        Integer x_front_start_frames = x_front.get(x_front.size()-1).frame;
//        Integer x_front_end_frames = x_front.get(0).frame;
//        Integer x_tail_start_frames = x_tail.get(x_front.size()-1).frame;
//        Integer x_tail_end_frames = x_tail.get(0).frame;
//        Float min_x,max_x,min_z,max_z,cent_z;
//        Float ground_x= Float.valueOf(args.get("ground_x"));
//        Float track_x_upper_limit= Float.valueOf(args.get("track_x_upper_limit"));
//        for (Infer x_frame:x_front){
//            if (InferUtil.hide_group(args,x_frame.frame,scenecontrols)==false){
//                if (x_frame.frame<x_tail_start_frames){
//                    min_x = x_frame.position;
//                    max_x = ground_x + track_x_upper_limit;
//                    min_z = get_track_z(args, this.lane)-3;
//                    max_z = get_track_z(args, this.lane)+3;
//                    cent_z = get_track_z(args, this.lane);
//                }
//                results.add(new FillJob("hold",1,x_frame.frame,false));
//
//            }
//        }
//        return results;
//    }

    public static Float get_track_z(HashMap<String,String> args,Integer lane){
        Float ground_z = Float.valueOf(args.get("ground_z"));
        Float ground_interval = Float.valueOf(args.get("ground_interval"));
        return ground_z+lane*ground_interval;
    }
}
