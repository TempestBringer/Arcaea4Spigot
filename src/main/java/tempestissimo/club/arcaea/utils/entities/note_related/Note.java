package tempestissimo.club.arcaea.utils.entities.note_related;

import tempestissimo.club.arcaea.utils.InferUtil;
import tempestissimo.club.arcaea.utils.entities.Song;
import tempestissimo.club.arcaea.utils.entities.infer_related.FillJob;
import tempestissimo.club.arcaea.utils.entities.infer_related.Infer;

import java.util.ArrayList;
import java.util.HashMap;

public class Note {
    public Integer t;
    public Integer lane;

    public Note(Integer t, Integer lane) {
        this.t = t;
        this.lane = lane;
    }
    public ArrayList<FillJob> get_logic_render(Song song, HashMap<String,String> args, ArrayList<Timing> timings, ArrayList<Scenecontrol> scenecontrols){
        ArrayList<FillJob> results = new ArrayList<>();
        ArrayList<Infer> x_s = InferUtil.position_infer(song, args, timings, this.t);
        for (Infer infer:x_s){
            if(InferUtil.hide_group(args, infer.frame, scenecontrols)==false){
                results.add(new FillJob("note",1,infer.frame,false));
            }
        }
        return results;
    }

    @Override
    public String toString() {
        return "Note{" +
                "t=" + t +
                ", lane=" + lane +
                '}';
    }
}
