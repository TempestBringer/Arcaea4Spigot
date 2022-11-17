package tempestissimo.club.arcaea.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import tempestissimo.club.arcaea.Arcaea4pigot;
import tempestissimo.club.arcaea.utils.entities.Song;
import tempestissimo.club.arcaea.utils.entities.infer_related.FillJob;
import tempestissimo.club.arcaea.utils.entities.infer_related.Infer;
import tempestissimo.club.arcaea.utils.entities.note_related.*;

import java.util.ArrayList;
import java.util.HashMap;

public class MainRender {
    public Arcaea4pigot plugin;
    public FileConfiguration config;
    //渲染配置预读取
    public Double tps;
    public Double pi;
    public Double ground_x;
    public Double ground_y;
    public Double ground_z;
    public Double ground_interval;
    public Double track_x_upper_limit;
    public Double track_x_lower_limit;
    public Double arc_raise_block;
    public Double arc_raise_ratio;
    public Double default_speed_per_second;
    public Double wait_after_last_note;
    //材料配置预读取
    public String air_material;
    public String note_material;
    public String arctap_material;
    public String hold_side_material;
    public String hold_centre_material;
    public Boolean red_arc_centre_only;
    public String red_arc_material;
    public String red_arc_centre_material;
    public String red_arc_support_material;
    public Boolean blue_arc_centre_only;
    public String blue_arc_material;
    public String blue_arc_centre_material;
    public String blue_arc_support_material;
    public Boolean green_arc_centre_only;
    public String green_arc_material;
    public String green_arc_centre_material;
    public String green_arc_support_material;
    //运行时状态
    public Boolean compiling=false;
    public Boolean compileFinished=false;
    public ArrayList<HashMap<String, ArrayList<FillJob>>> compileResults;


    /**
     * 检测是否可运行编译，如果可以则异步运行。
     * @param songIndex
     * @param ratingClass
     * @return
     */
    public Boolean compileEntry(Integer songIndex,Integer ratingClass){
        if (compiling){
            return false;
        }
        compiling=true;compileFinished=false;
        new BukkitRunnable() {
            @Override
            public void run() {
                ArrayList<HashMap<String, ArrayList<FillJob>>> results = compileSong(songIndex, ratingClass);
                compileResults=results;
                compiling=false;
                compileFinished=true;
            }
        }.runTaskAsynchronously(plugin);
        return true;
    }

    public Boolean playEntry(){
        return compileFinished;
    }


    // 思考用于存放推理结果的数据结构，可以是ArrayList<HashMap<String,Infer>>
    // 最外层ArrayList以时间为顺序，每tick的内容对应一个HashMap
    // HashMap的key代表对一个note（一个绘制段）的唯一命名
    // 每当一个note（绘制段）完成判定，即从后续所有tick的HashMap中移除该note（绘制段）的key

    /**
     * 编译一个aff文件
     * @param songIndex 歌曲id
     * @param ratingClass 难度等级
     */
    public ArrayList<HashMap<String, ArrayList<FillJob>>> compileSong(Integer songIndex,Integer ratingClass){
        String affPath=plugin.songScanner.generate_aff_file_path(songIndex,ratingClass);
        Song song = plugin.songScanner.song_list.get(songIndex);
        //读取原始文件
        ArrayList<String> rawAff=plugin.affReader.read_raw(affPath);
        //解析原始文件为timingGroup
        HashMap<String, Object> parsedAff = plugin.affReader.raw_to_timing_group(rawAff);
        //1.文件头
        HashMap<String,String> file_head_dict = (HashMap<String, String>) parsedAff.get("file_head_dict");
        //2.主timingGroup
        ArrayList<String> main_timing_group = (ArrayList<String>) parsedAff.get("main_timing_group");
        //3.副timingGroup
        ArrayList<ArrayList<String>> timing_groups = (ArrayList<ArrayList<String>>) parsedAff.get("timing_groups");
        //4.副timingGroup参数
        ArrayList<String> timing_groups_args = (ArrayList<String>) parsedAff.get("timing_groups_args");
        //按照timingGroup逐个拆分
        //1.主timingGroup拆分
        HashMap<String, Object> parsed_main_timing_group = plugin.affReader.timing_group_parser(main_timing_group);
        //2.副timingGroup拆分
        ArrayList<HashMap<String,Object>> parsed_attached_timing_groups = new ArrayList<>();
        for (int i = 0; i < timing_groups.size(); i++) {
            parsed_attached_timing_groups.add(plugin.affReader.timing_group_parser(timing_groups.get(i)));
        }
        //分别渲染
        //1.渲染主timingGroup
        ArrayList<Note> main_notes = (ArrayList<Note>) parsed_main_timing_group.get("notes");
        ArrayList<Hold> main_holds = (ArrayList<Hold>) parsed_main_timing_group.get("holds");
        ArrayList<Arc> main_arcs = (ArrayList<Arc>) parsed_main_timing_group.get("arcs");
        ArrayList<Scenecontrol> main_scenecontrols = (ArrayList<Scenecontrol>) parsed_main_timing_group.get("scenecontrols");
        ArrayList<Timing> main_timings = (ArrayList<Timing>) parsed_main_timing_group.get("timings");
        ArrayList<Camera> main_cameras = (ArrayList<Camera>) parsed_main_timing_group.get("cameras");
        ArrayList<HashMap<String, ArrayList<FillJob>>> main_compile = compileTimingGroup(song, main_notes, main_holds, main_arcs, main_scenecontrols, main_timings, main_cameras, "main", "main_");
        //2.分别渲染副timingGroup
        for (int i = 0; i < parsed_attached_timing_groups.size(); i++) {
            HashMap<String,Object> parsed_attached_timing_group = parsed_attached_timing_groups.get(i);
            ArrayList<Note> attached_notes = (ArrayList<Note>) parsed_attached_timing_group.get("notes");
            ArrayList<Hold> attached_holds = (ArrayList<Hold>) parsed_attached_timing_group.get("holds");
            ArrayList<Arc> attached_arcs = (ArrayList<Arc>) parsed_attached_timing_group.get("arcs");
            ArrayList<Scenecontrol> attached_scenecontrols = (ArrayList<Scenecontrol>) parsed_attached_timing_group.get("scenecontrols");
            ArrayList<Timing> attached_timings = (ArrayList<Timing>) parsed_attached_timing_group.get("timings");
            ArrayList<Camera> attached_cameras = (ArrayList<Camera>) parsed_attached_timing_group.get("cameras");
            String timing_group_arg = timing_groups_args.get(i);
            ArrayList<HashMap<String, ArrayList<FillJob>>> attached_compile = compileTimingGroup(song, attached_notes, attached_holds, attached_arcs, attached_scenecontrols, attached_timings, attached_cameras, timing_group_arg,String.valueOf(i)+"_");
            main_compile=mergeCompileResults(main_compile, attached_compile);
        }
        return main_compile;
    }

    /**
     * 编译：一个TimingGroup
     * @param notes
     * @param holds
     * @param arcs
     * @param sceneControls
     * @param timings
     * @param main_cameras
     * @param timingGroupArg
     * @return
     */
    public ArrayList<HashMap<String, ArrayList<FillJob>>> compileTimingGroup(Song song, ArrayList<Note> notes, ArrayList<Hold> holds, ArrayList<Arc> arcs, ArrayList<Scenecontrol> sceneControls, ArrayList<Timing> timings, ArrayList<Camera> main_cameras, String timingGroupArg, String timingGroupPrefix){
        ArrayList<HashMap<String, ArrayList<FillJob>>> results = new ArrayList<>();
        //编译Notes
        for (int i=0;i<notes.size();i++){
            Note note = notes.get(i);
            HashMap<Integer,ArrayList<FillJob>> compiledNote = compileNote(song,note,timings);
            for (Integer tick: compiledNote.keySet()){
                if (results.get(tick).equals(null)){
                    results.add(tick,new HashMap<>());
                }
                if (!results.get(tick).containsKey(timingGroupPrefix+"note_"+i)){
                    results.get(tick).put(timingGroupPrefix+"note_"+i,compiledNote.get(tick));
                }
            }
        }
        return results;
    }


    /**
     * 编译：一个Note，返回的HashMap以渲染所在刻为key，描述一个Note在当前刻的所有渲染任务。
     * @param song
     * @param note
     * @param timings
     * @return
     */
    public HashMap<Integer,ArrayList<FillJob>> compileNote(Song song, Note note, ArrayList<Timing> timings){
        HashMap<Integer,ArrayList<FillJob>> results=new HashMap<>();
        ArrayList<Infer> x_s = position_infer(song, timings, note.t);
        for (Infer infer:x_s){
            Integer frame =infer.frame;
            Double x = infer.position;
            if (!results.containsKey(frame))
                results.put(frame,new ArrayList<FillJob>());
            if (!results.containsKey(frame+1))
                results.put(frame+1,new ArrayList<FillJob>());
            FillJob tempFill = new FillJob("note",0,frame,false);
            tempFill.y_low = ground_y.intValue();
            tempFill.y_high = ground_y.intValue();
            tempFill.x_low = x.intValue();
            tempFill.x_high = x.intValue();
            tempFill.z_low = getGroundTrackZ(note.lane,"left").intValue();
            tempFill.z_high = getGroundTrackZ(note.lane,"right").intValue();
            tempFill.material = note_material;
            results.get(frame).add(tempFill);
            tempFill.material = air_material;
            results.get(frame+1).add(tempFill);
        }
        return results;
    }

    public HashMap<Integer,ArrayList<FillJob>> compileHold(Song song, Hold hold, ArrayList<Timing> timings){
        HashMap<Integer,ArrayList<FillJob>> results=new HashMap<>();
        return results;
    }

    public HashMap<Integer,ArrayList<FillJob>> compileArc(Song song, Arc arc, ArrayList<Timing> timings){
        HashMap<Integer,ArrayList<FillJob>> results=new HashMap<>();
        return results;
    }

    /**
     * 编译：地面轨道音符的横向位置，可用位置为left，right，mid，其他默认为mid
     * @param track
     * @return
     */
    public Double getGroundTrackZ(Integer track, String position){
        if (position.equalsIgnoreCase("left")){
            return ground_z+track*ground_interval-(ground_interval/2-1);
        }else if (position.equalsIgnoreCase("right")){
            return ground_z+track*ground_interval+(ground_interval/2+1);
        }else if (position.equalsIgnoreCase("mid")){
            return ground_z+track*ground_interval;
        }else{
            return ground_z+track*ground_interval;
        }
    }

    /**
     * 编译：天空轨道音符的高度
     * @param y
     * @return
     */
    public Double getSkyTrackY(Double y){
            return ground_y+ground_interval*y*arc_raise_ratio+arc_raise_block;
    }

    /**
     * 编译：天空轨道音符的横向位置
     * @param x 天空音符在Aff上的横坐标表述
     * @return
     */
    public Double getSkyTrackZ(Double x){
        return ground_z+1.5*ground_interval+x*2*ground_interval+0.5;
    }

    /**
     * 编译:用时间推理，推理打击时刻为time的物件在应当被渲染的各个时刻的在纵深方向的的位置
     * @param song
     * @param timings
     * @param time
     * @return
     */
    public ArrayList<Infer> position_infer(Song song, ArrayList<Timing> timings, Integer time){
        ArrayList<Infer> x_s = new ArrayList<>();
        Double frame_time = 1000/tps;
        Integer timings_pointer = 0;
        Float bpm_base = song.bpm_base;
        //正向遍历，定位到time后的第一个timing记录
        while(timings.get(timings_pointer).t<time){
            timings_pointer+=1;
            if (timings_pointer==timings.size())
                break;
        }
        timings_pointer-=1;
        Double cur_time = Double.valueOf(time);
        //位置的帧对齐，使长度极短的物件如装饰arc也可以渲染
        Double cur_x = ground_x+(time%frame_time)*timings.get(timings_pointer).bpm*default_speed_per_second/bpm_base/1000;
        Double forward_frame;
        Integer start_timing,end_timing;
        while((track_x_upper_limit+ground_x)>cur_x && cur_x>(track_x_lower_limit+ground_x)){
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

    /**
     * 编译：用时间推理，推理打击时刻为time的物件在应当被渲染的各个时刻的显示或隐藏属性
     * @param frame
     * @param scenecontrols
     * @return
     */
    public Boolean hide_group(Integer frame, ArrayList<Scenecontrol> scenecontrols){
        Boolean hide_flag = false;
        Integer closest_frame=0;
        Double frame_time = 1000/tps;
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


    /**
     * 确定一个timingGroup的最长时间（ms）
     * @param notes
     * @param holds
     * @param arcs
     * @return
     */
    public Integer findMaxLength(ArrayList<Note> notes, ArrayList<Hold> holds, ArrayList<Arc> arcs){

        return null;
    }

    /**
     * 合并不同TimingGroup的编译结果
     * @param source
     * @param add
     * @return
     */
    public ArrayList<HashMap<String, ArrayList<FillJob>>> mergeCompileResults(ArrayList<HashMap<String, ArrayList<FillJob>>> source, ArrayList<HashMap<String, ArrayList<FillJob>>> add){
        ArrayList<HashMap<String, ArrayList<FillJob>>> results = new ArrayList<>();
        for (int i = 0; i < add.size(); i++) {
            HashMap<String, ArrayList<FillJob>> tempHashMap = source.get(i);
            for (String key:add.get(i).keySet()){
                tempHashMap.put(key, add.get(i).get(key));
            }
            results.add(i,tempHashMap);
        }
        return results;
    }



    public MainRender(Arcaea4pigot plugin, FileConfiguration config){
        this.config=config;
        this.plugin=plugin;
        this.readConfig(config);
    }

    public void readConfig(FileConfiguration config){
        this.tps=config.getDouble("Render.tps");
        this.pi=config.getDouble("Render.Position.ground_x");
        this.ground_x=config.getDouble("Render.Position.ground_x");
        this.ground_y=config.getDouble("Render.Position.ground_y");
        this.ground_z=config.getDouble("Render.Position.ground_z");
        this.ground_interval=config.getDouble("Render.Position.ground_interval");
        this.track_x_upper_limit=config.getDouble("Render.Position.track_x_upper_limit");
        this.track_x_lower_limit=config.getDouble("Render.Position.track_x_lower_limit");
        this.arc_raise_block=config.getDouble("Render.Position.arc_raise_block");
        this.arc_raise_ratio=config.getDouble("Render.Position.arc_raise_ratio");
        this.default_speed_per_second=config.getDouble("Render.Position.default_speed_per_second");
        this.wait_after_last_note=config.getDouble("Render.Position.ground_x");
        //材料配置预读取
        this.air_material=config.getString("Render.Material.air_material");
        this.note_material=config.getString("Render.Material.note_material");
        this.arctap_material=config.getString("Render.Material.arctap_material");
        this.hold_side_material=config.getString("Render.Material.hold_side_material");
        this.hold_centre_material=config.getString("Render.Material.hold_centre_material");
        this.red_arc_centre_only=config.getBoolean("Render.Material.red_arc_centre_only");
        this.red_arc_material=config.getString("Render.Material.red_arc_material");
        this.red_arc_centre_material=config.getString("Render.Material.red_arc_centre_material");
        this.red_arc_support_material=config.getString("Render.Material.red_arc_support_material");
        this.blue_arc_centre_only=config.getBoolean("Render.Material.blue_arc_centre_only");
        this.blue_arc_material=config.getString("Render.Material.blue_arc_material");
        this.blue_arc_centre_material=config.getString("Render.Material.blue_arc_centre_material");
        this.blue_arc_support_material=config.getString("Render.Material.blue_arc_support_material");
        this.green_arc_centre_only=config.getBoolean("Render.Material.green_arc_centre_only");
        this.green_arc_material=config.getString("Render.Material.green_arc_material");
        this.green_arc_centre_material=config.getString("Render.Material.green_arc_centre_material");
        this.green_arc_support_material=config.getString("Render.Material.green_arc_support_material");
    }
}
