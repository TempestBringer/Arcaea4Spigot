package tempestissimo.club.arcaea.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import tempestissimo.club.arcaea.Arcaea4pigot;
import tempestissimo.club.arcaea.utils.entities.Song;
import tempestissimo.club.arcaea.utils.entities.infer_related.FillJob;
import tempestissimo.club.arcaea.utils.entities.infer_related.Infer;
import tempestissimo.club.arcaea.utils.entities.infer_related.ParticleJob;
import tempestissimo.club.arcaea.utils.entities.note_related.*;

import java.util.*;

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
    public Double zero_time_arc_play_dense;
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
    public String track_surface_material;
    public String track_line_material;
    public String extend_track_surface_material;
    public String extend_track_line_material;
    public Boolean enable_black_line;
    public Double black_line_particle_dense;
    public Boolean enable_double_note_line;
    public Double double_note_line_particle_dense;


    //运行时状态
    public Boolean compiling=false;
    public Boolean compileFinished=false;
    public HashMap<Integer,ArrayList<FillJob>> compileResults;

    public Integer minTick;
    public Integer maxTick;





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
                try{
                    ArrayList<FillJob> results = compileSong(songIndex, ratingClass);
                    compileResults = shuffleCompiledWorkLoad(results);
                    compiling=false;
                    compileFinished=true;
                    this.cancel();
                    plugin.info.broadcastSuccess("compile_finished",new ArrayList<>());
                }catch (Exception e){
                    compiling=false;
                    compileFinished=false;
                    this.cancel();
                    plugin.info.broadcastWarn("error_occurred_while_compiling",new ArrayList<>());
                }

            }
        }.runTaskAsynchronously(plugin);
        return true;
    }

    /**
     * 按照帧序号分类编译结果
     * @param input
     * @return
     */
    private HashMap<Integer,ArrayList<FillJob>> shuffleCompiledWorkLoad(ArrayList<FillJob> input){
        this.maxTick = 0;
        this.minTick = 0;
        HashMap<Integer,ArrayList<FillJob>> results = new HashMap<>();
        for (FillJob fillJob:input){
            ArrayList<FillJob> fillJobsInTick;
            if (results.containsKey(fillJob.frame)){
                fillJobsInTick = results.get(fillJob.frame);
            }else{
                fillJobsInTick = new ArrayList<>();
            }
            fillJobsInTick.add(fillJob);
            results.put(fillJob.frame,fillJobsInTick);

            if (fillJob.frame>maxTick)
                maxTick=fillJob.frame;
            if (fillJob.frame<minTick)
                minTick=fillJob.frame;
        }

        for (Integer i=0;i<maxTick;i++){
            if (!results.containsKey(i)){
                results.put(i,new ArrayList<>());
            }
        }
        for (Integer i=0;i<maxTick;i++){
            results.get(i).sort(new Comparator<FillJob>() {
                @Override
                public int compare(FillJob o1, FillJob o2) {
                    if (o1 == null && o2 == null) {
                        return 0;
                    }
                    if (o1 == null) {
                        return -1;
                    }
                    if (o2 == null) {
                        return 1;
                    }

                    if (o1.priority < o2.priority)
                        return 1;
                    else if (o1.priority.equals(o2.priority)) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
        }
        return results;
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
    public ArrayList<FillJob> compileSong(Integer songIndex,Integer ratingClass){
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
        ArrayList<FillJob> main_compile = compileTimingGroup(song, main_notes, main_holds, main_arcs, main_scenecontrols, main_timings, main_cameras, "main", "main_");
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
            ArrayList<FillJob> attached_compile = compileTimingGroup(song, attached_notes, attached_holds, attached_arcs, attached_scenecontrols, attached_timings, attached_cameras, timing_group_arg,String.valueOf(i)+"_");
            main_compile.addAll(attached_compile);
//            main_compile=mergeCompileResults(main_compile, attached_compile);
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
    public ArrayList<FillJob> compileTimingGroup(Song song, ArrayList<Note> notes, ArrayList<Hold> holds, ArrayList<Arc> arcs, ArrayList<Scenecontrol> sceneControls, ArrayList<Timing> timings, ArrayList<Camera> main_cameras, String timingGroupArg, String timingGroupPrefix){
        ArrayList<FillJob> results = new ArrayList<>();
        //编译Notes，对每一个Note执行
        for (int i=0;i<notes.size();i++){
            Note note = notes.get(i);
            ArrayList<FillJob> compiledNote = compileNote(song,note,timings,sceneControls,timingGroupPrefix+"note_"+i+"_");
            results.addAll(compiledNote);
        }
        //编译非黑线Arcs，对每一个Arc执行
        for (int i=0;i<arcs.size();i++){
            Arc arc = arcs.get(i);
            ArrayList<FillJob> compiledArc = compileArc(song,arc,timings,sceneControls,timingGroupPrefix+"arc_"+i+"_");
            results.addAll(compiledArc);
        }
        //编译Holds，对每一个Hold执行
        for (int i = 0; i < holds.size(); i++) {
            Hold hold = holds.get(i);
            ArrayList<FillJob> compiledHold = compileHold(song,hold,timings,sceneControls,timingGroupPrefix+"arc_"+i+"_");
            results.addAll(compiledHold);

        }
        // 渲染天地双押线
        ArrayList<FillJob> doubleKeys = compileSkyGroundDoubleKeyLine(song, notes, arcs, timings, sceneControls, timingGroupArg, timingGroupPrefix + "double_key_");
        results.addAll(doubleKeys);
//        System.out.println("finished compiling timing group : "+timingGroupPrefix);
        return results;
    }


    /**
     * 编译：一个Note，返回一个Note在当前刻的所有渲染任务。
     * @param song
     * @param note
     * @param timings
     * @return
     */
    public ArrayList<FillJob> compileNote(Song song, Note note, ArrayList<Timing> timings,ArrayList<Scenecontrol> sceneControls, String jobName){
        ArrayList<FillJob> results=new ArrayList<>();
        ArrayList<Infer> x_s = position_infer(song, timings, note.t);
        for (Infer infer:x_s){
            Integer frame =infer.frame;
            if (hide_group(frame, sceneControls)){
                continue;
            }
            Double x = infer.position;
            FillJob tempFill = new FillJob("note",0,frame,false);
            tempFill.y_low = ground_y.intValue();
            tempFill.y_high = ground_y.intValue();
            tempFill.x_low = x.intValue();
            tempFill.x_high = x.intValue();
            tempFill.z_low = getGroundTrackZ(note.lane,"left").intValue();
            tempFill.z_high = getGroundTrackZ(note.lane,"right").intValue();
            tempFill.material = note_material;
            tempFill.jobName = jobName;
            results.add(tempFill);
            FillJob airFill = new FillJob("air",10,frame+1,false);
            airFill.y_low = ground_y.intValue();
            airFill.y_high = ground_y.intValue();
            airFill.x_low = x.intValue();
            airFill.x_high = x.intValue();
            airFill.z_low = getGroundTrackZ(note.lane,"left").intValue();
            airFill.z_high = getGroundTrackZ(note.lane,"right").intValue();
            airFill.material = air_material;
            airFill.jobName = jobName;
            results.add(airFill);
        }
        return results;
    }

    public ArrayList<FillJob> compileSkyGroundDoubleKeyLine(Song song, ArrayList<Note> notes, ArrayList<Arc> arcs, ArrayList<Timing> timings, ArrayList<Scenecontrol> sceneControls, String timingGroupArg,String jobName){
        ArrayList<FillJob> results = new ArrayList<>();
        if (timingGroupArg.equalsIgnoreCase("noinput")){
            return results;
        }
        HashMap<Note,Arc> lines = new HashMap<>();
        double frameTime = 1000/tps;
        for(Note note:notes)
            for (Arc arc: arcs)
                for (int arctap:arc.arctaplist){
                    if (note.t ==arctap){
                        lines.put(note,arc);
                    }
                }
        for (Note note:lines.keySet()){
            Arc arc = lines.get(note);
            for (int arcTap:arc.arctaplist){
                if (note.t == arcTap){
                    ArrayList<Double[]> position = arc.getPosition(note.t, zero_time_arc_play_dense);
                    ArrayList<Infer> infers = position_infer(song,timings,arcTap);
                    // 构造一个0长度arc
                    double groundX = note.lane*0.5-0.75;
                    double skyX = position.get(0)[0];
                    double groundY = 0.0;
                    double skyY = position.get(0)[1];
                    Arc temp = new Arc(note.t, note.t, groundX, skyX, "s",groundY,skyY,0,"none",true,new ArrayList<>());
                    ArrayList<FillJob> double_key = compileArc(song, temp, timings, sceneControls, "double_key");
                    results.addAll(double_key);
                }

            }


        }

        return results;
    }

    public ArrayList<FillJob> compileHold(Song song, Hold hold, ArrayList<Timing> timings, ArrayList<Scenecontrol> sceneControls, String jobName){
        ArrayList<FillJob> results = new ArrayList<>();
        ArrayList<Infer> xFront = position_infer(song,timings,hold.t1);
        Collections.sort(xFront);
        ArrayList<Infer> xTail = position_infer(song,timings,hold.t2);
        Collections.sort(xTail);
        // 分离frame字段
        Integer xFrontStartFrame = xFront.get(xFront.size() - 1).frame;
        Integer xFrontEndFrame = xFront.get(0).frame;
        Integer xTailStartFrame = xTail.get(xTail.size() - 1).frame;
        Integer xTailEndFrame = xTail.get(0).frame;
        // 对于Hold头存在的帧
        for (Infer xFrame :xFront){
            if (hide_group(xFrame.frame, sceneControls)){
                continue;
            }
            Double minX=0.0;
            Double maxX=0.0;
            Double minZ=getGroundTrackZ(hold.lane, "left");
            Double maxZ=getGroundTrackZ(hold.lane, "right");
            Double midZ=getGroundTrackZ(hold.lane, "mid");
            // 如果该帧存在于Hold尾出现之前
            if (xFrame.frame<xTailStartFrame){
                minX = xFrame.position;
                maxX = ground_x+track_x_upper_limit;
            }else // 如果该帧出现在Hold尾出现之后，结束之前
                if(xFrame.frame<=xTailEndFrame){
                minX = xFrame.position;
                maxX = ground_x+track_x_upper_limit;
                for (Infer xTailFrame:xTail){
                    if (xTailFrame.frame.intValue()==xFrame.frame.intValue()){
                        maxX = xTailFrame.position;
                        break;
                    }
                }
            }else{// 如果该帧出现在Hold尾结束之后
                System.out.println("Unexpected hold");
            }
            FillJob cur_side = new FillJob("hold",2,xFrame.frame,false,minX.intValue(),maxX.intValue(),ground_y.intValue(),ground_y.intValue(),minZ.intValue(),maxZ.intValue(),hold_side_material,jobName);
            FillJob next_side = new FillJob("air",10,xFrame.frame+1,false,minX.intValue(),maxX.intValue(),ground_y.intValue(),ground_y.intValue(),minZ.intValue(),maxZ.intValue(),air_material,jobName);
            FillJob cur_centre = new FillJob("hold",2,xFrame.frame,false,minX.intValue(),maxX.intValue(),ground_y.intValue(),ground_y.intValue(),midZ.intValue(),midZ.intValue(),hold_centre_material,jobName);
            FillJob next_centre = new FillJob("air",10,xFrame.frame+1,false,minX.intValue(),maxX.intValue(),ground_y.intValue(),ground_y.intValue(),midZ.intValue(),midZ.intValue(),air_material,jobName);
            results.add(cur_side);
            results.add(cur_centre);
            results.add(next_side);
            results.add(next_centre);
        }
        // 对于Hold尾存在的帧
        for (Infer xFrame :xTail){
            if (hide_group(xFrame.frame, sceneControls)){
                continue;
            }
            Double minX=0.0;
            Double maxX=0.0;
            Double minZ=getGroundTrackZ(hold.lane, "left");
            Double maxZ=getGroundTrackZ(hold.lane, "right");
            Double midZ=getGroundTrackZ(hold.lane, "mid");
            // 当Hold头未结束
            if (xFrame.frame<=xFrontEndFrame){
                for (Infer xFrontFrame: xFront){
                    if (xFrontFrame.frame.intValue()==xFrame.frame.intValue()){
                        minX = xFrontFrame.position;
                        maxX = xFrame.position;
                        break;
                    }
                }
            }else // 当Hold头结束而Hold尾未结束
                if (xFrame.frame<=xTailEndFrame){
                minX = ground_x;
                maxX = xFrame.position;
            }else{
                System.out.println("Unexpected hold");
            }
            FillJob cur_side = new FillJob("hold",2,xFrame.frame,false,minX.intValue(),maxX.intValue(),ground_y.intValue(),ground_y.intValue(),minZ.intValue(),maxZ.intValue(),hold_side_material,jobName);
            FillJob next_side = new FillJob("air",10,xFrame.frame+1,false,minX.intValue(),maxX.intValue(),ground_y.intValue(),ground_y.intValue(),minZ.intValue(),maxZ.intValue(),air_material,jobName);
            FillJob cur_centre = new FillJob("hold",2,xFrame.frame,false,minX.intValue(),maxX.intValue(),ground_y.intValue(),ground_y.intValue(),midZ.intValue(),midZ.intValue(),hold_centre_material,jobName);
            FillJob next_centre = new FillJob("air",10,xFrame.frame+1,false,minX.intValue(),maxX.intValue(),ground_y.intValue(),ground_y.intValue(),midZ.intValue(),midZ.intValue(),air_material,jobName);
            results.add(cur_side);
            results.add(cur_centre);
            results.add(next_side);
            results.add(next_centre);
        }
        if (xFrontEndFrame<xTailStartFrame){
            //Optimize Loop
            for (int i = xFrontEndFrame; i < xTailStartFrame; i++) {
                if (hide_group(i, sceneControls)){
                    continue;
                }
                Double minX=ground_x;
                Double maxX=ground_x+track_x_upper_limit;
                Double minZ=getGroundTrackZ(hold.lane,"left");
                Double maxZ=getGroundTrackZ(hold.lane,"right");
                Double midZ=getGroundTrackZ(hold.lane,"mid");
                FillJob cur_side = new FillJob("hold",2,i,false,minX.intValue(),maxX.intValue(),ground_y.intValue(),ground_y.intValue(),minZ.intValue(),maxZ.intValue(),hold_side_material,jobName);
                FillJob next_side = new FillJob("air",10,i+1,false,minX.intValue(),maxX.intValue(),ground_y.intValue(),ground_y.intValue(),minZ.intValue(),maxZ.intValue(),air_material,jobName);
                FillJob cur_centre = new FillJob("hold",2,i,false,minX.intValue(),maxX.intValue(),ground_y.intValue(),ground_y.intValue(),midZ.intValue(),midZ.intValue(),hold_centre_material,jobName);
                FillJob next_centre = new FillJob("air",10,i+1,false,minX.intValue(),maxX.intValue(),ground_y.intValue(),ground_y.intValue(),midZ.intValue(),midZ.intValue(),air_material,jobName);
                results.add(cur_side);
                results.add(cur_centre);
                results.add(next_side);
                results.add(next_centre);
            }
        }


        return results;
    }

//    /**
//     * 渲染：黑线，本体是Arc
//     * @param song
//     * @param arc
//     * @param timings
//     * @param jobName
//     * @return
//     */
//    public ArrayList<ParticleJob> compileArcBlackLine(Song song, Arc arc, ArrayList<Timing> timings, String jobName){
//        ArrayList<ParticleJob> results = new ArrayList<>();
//        Double frameTime=1000/tps;
//        if (arc.skylineBoolean){
//            if (enable_black_line){
//                Double start_frame_time = arc.t1-arc.t1%frameTime;
//                Double cueTime=start_frame_time;
//                while(cueTime<arc.t2){
//                    ArrayList<Double[]> positions = arc.getPosition(cueTime.intValue(), zero_time_arc_play_dense);
//                    for (Double[] position:positions){
//                        ArrayList<ParticleJob> fillJobs = compileBlackLineBody(song, timings, position[0], position[1], arc.color, cueTime.intValue(), jobName);
//                        results.addAll(fillJobs);
//                    }
//                }
//            }
//        }
//        return results;
//    }
//
//    /**
//     * 编译：渲染Arc黑线
//     * @param song 歌曲song对象
//     * @param timings TimingGroup
//     * @param x 横截面的x坐标，相当于游戏中的z轴
//     * @param y 横截面的y坐标，相当于游戏中的y轴
//     * @param color 蛇颜色， 0，1，2分别是蓝色，红色，绿色
//     * @param arcBodyTime 当前全局毫秒时间
//     * @param jobName 任务名称，debug用
//     * @return
//     */
//    public ArrayList<ParticleJob> compileBlackLineBody(Song song, ArrayList<Timing> timings, Double x,Double y,Integer color,Integer arcBodyTime,String jobName) {
//        ArrayList<ParticleJob> results = new ArrayList<>();
//        ArrayList<Infer> xS = position_infer(song, timings, arcBodyTime);
//        for (int i = 0; i < xS.size(); i++) {
//            Infer tempInfer = xS.get(i);
//            Integer curFrame=tempInfer.frame;
//            Double startX=tempInfer.position;
//            Double startY=getSkyTrackY(y);
//            Double startZ=getSkyTrackZ(x);
//            ParticleJob particleJob = new ParticleJob(startX,startY,startZ,curFrame,"b");
//            results.add(particleJob);
//        }
//        return results;
//    }

    /**
     * 渲染：Arc，分天键和arc本体渲染
     * @param song
     * @param arc
     * @param timings
     * @param jobName
     * @return
     */
    public ArrayList<FillJob> compileArc(Song song, Arc arc, ArrayList<Timing> timings, ArrayList<Scenecontrol> sceneControls,String jobName){
        ArrayList<FillJob> results = new ArrayList<>();
        //渲染Arctap
        List<Integer> arctaps = arc.arctaplist;
        for (int i=0;i<arctaps.size();i++){
            Integer arctap = arctaps.get(i);
            ArrayList<Double[]> pois = arc.getPosition(arctap, this.zero_time_arc_play_dense);
            for (Double[] poi:pois){
                ArrayList<FillJob> arcTapResult = this.compileArcTap(song, timings, poi[0], poi[1], arctap, sceneControls,jobName + "arctap_" + i + "_");
                results.addAll(arcTapResult);
            }
        }
        //渲染Arc本体
        Double frameTime=1000/tps;
        //是黑线
        if (arc.skylineBoolean){
            // 如果开启黑线绘制
            if (enable_black_line) {
                Double start_frame_time = arc.t1-arc.t1%frameTime;
                Double cueTime=start_frame_time;
                while(cueTime<arc.t2){
                    ArrayList<Double[]> positions = arc.getPosition(cueTime.intValue(), zero_time_arc_play_dense);
                    for (Double[] position:positions){
                        ArrayList<FillJob> fillJobs = compileArcBody(song, timings, position[0], position[1], -1, cueTime.intValue(), sceneControls, jobName);
                        results.addAll(fillJobs);
                    }
                    cueTime+=frameTime*tps/default_speed_per_second/black_line_particle_dense;
                }
            }
        }else{
            //是实体蛇
            Double start_frame_time = arc.t1-arc.t1%frameTime;
            Double cueTime=start_frame_time;
            while(cueTime<arc.t2){
                ArrayList<Double[]> positions = arc.getPosition(cueTime.intValue(), zero_time_arc_play_dense);
                for (Double[] position:positions){
                    ArrayList<FillJob> fillJobs = compileArcBody(song, timings, position[0], position[1], arc.color, cueTime.intValue(), sceneControls, jobName);
                    results.addAll(fillJobs);
                }
                cueTime+=frameTime*tps/default_speed_per_second;
            }
        }
        return results;
    }

    /**
     * 编译一个ArcTap
     * @param song
     * @param timings
     * @param x
     * @param y
     * @param arcTapTime
     * @param jobName
     * @return
     */
    public ArrayList<FillJob> compileArcTap(Song song, ArrayList<Timing> timings, Double x,Double y,Integer arcTapTime, ArrayList<Scenecontrol> sceneControls,String jobName) {
        ArrayList<FillJob> results = new ArrayList<>();
        ArrayList<Infer> xS = position_infer(song, timings, arcTapTime);
        for (Infer infer:xS){
            Integer frame =infer.frame;
            if (hide_group(frame, sceneControls)){
                continue;
            }
            Double startX = infer.position;
            Double startY = getSkyTrackY(y);
            Double startZ = getSkyTrackZ(x)-(ground_interval-1)/2;
            Double endZ = getSkyTrackZ(x)+(ground_interval-1)/2;
            FillJob tempFill = new FillJob("arctap",0,frame,false);
            tempFill.x_low= startX.intValue();
            tempFill.x_high= startX.intValue();
            tempFill.y_low= startY.intValue();
            tempFill.y_high= startY.intValue();
            tempFill.z_low= startZ.intValue();
            tempFill.z_high= endZ.intValue();
            tempFill.material = arctap_material;
            tempFill.jobName=jobName;
            results.add(tempFill);
            FillJob airFill= new FillJob("air",10,frame+1,false);
            airFill.x_low= startX.intValue();
            airFill.x_high= startX.intValue();
            airFill.y_low= startY.intValue();
            airFill.y_high= startY.intValue();
            airFill.z_low= startZ.intValue();
            airFill.z_high= endZ.intValue();
            airFill.material = air_material;
            airFill.jobName=jobName;
            results.add(airFill);
        }
        return results;
    }

    /**
     * 编译：渲染一格宽的Arc横截面
     * @param song 歌曲song对象
     * @param timings TimingGroup
     * @param x 横截面的x坐标，相当于游戏中的z轴
     * @param y 横截面的y坐标，相当于游戏中的y轴
     * @param color 蛇颜色， 0，1，2分别是蓝色，红色，绿色
     * @param arcBodyTime 当前全局毫秒时间
     * @param jobName 任务名称，debug用
     * @return
     */
    public ArrayList<FillJob> compileArcBody(Song song, ArrayList<Timing> timings, Double x,Double y,Integer color,Integer arcBodyTime,ArrayList<Scenecontrol> sceneControls,String jobName) {
        ArrayList<FillJob> results = new ArrayList<>();
        ArrayList<Infer> xS = position_infer(song, timings, arcBodyTime);
        for (int i = 0; i < xS.size(); i++) {
            Infer tempInfer = xS.get(i);
            Integer curFrame=tempInfer.frame;
            if (hide_group(curFrame, sceneControls)){
                continue;
            }
            Double startX=tempInfer.position;
            Double endX=tempInfer.position;
            Double startY=getSkyTrackY(y);
            Double endY=getSkyTrackY(y);
            Double startZ=getSkyTrackZ(x);
            Double endZ=getSkyTrackZ(x);
            if(color==-1){ // 颜色-1，黑线
                FillJob particleFill = new FillJob("blackline", 9, curFrame, false, startX,startY,startZ,"end_rod",jobName);
                results.add(particleFill);
            } else if (color==0){//颜色0，蓝色蛇
                FillJob centreFill = new FillJob("arc",4,curFrame,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue(),endZ.intValue(),blue_arc_centre_material,jobName);
                results.add(centreFill);
                FillJob centreAir = new FillJob("air",10,curFrame+1,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue(),endZ.intValue(),air_material,jobName);
                results.add(centreAir);
                //蓝蛇描边
                if (!blue_arc_centre_only){
                    FillJob leftFill = new FillJob("arc",5,curFrame,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue()-1,endZ.intValue()-1,blue_arc_material,jobName);
                    FillJob leftAir = new FillJob("air",10,curFrame+1,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue()-1,endZ.intValue()-1,air_material,jobName);
                    results.add(leftFill);
                    results.add(leftAir);
                    FillJob rightFill = new FillJob("arc",5,curFrame,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue()+1,endZ.intValue()+1,blue_arc_material,jobName);
                    FillJob rightAir = new FillJob("air",10,curFrame+1,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue()+1,endZ.intValue()+1,air_material,jobName);
                    results.add(rightFill);
                    results.add(rightAir);
                    FillJob upFill = new FillJob("arc",5,curFrame,false,startX.intValue(),endX.intValue(),startY.intValue()+1,endY.intValue()+1,startZ.intValue(),endZ.intValue(),blue_arc_material,jobName);
                    FillJob upAir = new FillJob("air",10,curFrame+1,false,startX.intValue(),endX.intValue(),startY.intValue()+1,endY.intValue()+1,startZ.intValue(),endZ.intValue(),air_material,jobName);
                    results.add(upFill);
                    results.add(upAir);
                }
            }else if (color==1){//颜色1，红色蛇
                FillJob centreFill = new FillJob("arc",4,curFrame,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue(),endZ.intValue(),red_arc_centre_material,jobName);
                results.add(centreFill);
                FillJob centreAir = new FillJob("air",10,curFrame+1,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue(),endZ.intValue(),air_material,jobName);
                results.add(centreAir);
                //蓝蛇描边
                if (!red_arc_centre_only){
                    FillJob leftFill = new FillJob("arc",5,curFrame,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue()-1,endZ.intValue()-1,red_arc_material,jobName);
                    FillJob leftAir = new FillJob("air",10,curFrame+1,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue()-1,endZ.intValue()-1,air_material,jobName);
                    results.add(leftFill);
                    results.add(leftAir);
                    FillJob rightFill = new FillJob("arc",5,curFrame,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue()+1,endZ.intValue()+1,red_arc_material,jobName);
                    FillJob rightAir = new FillJob("air",10,curFrame+1,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue()+1,endZ.intValue()+1,air_material,jobName);
                    results.add(rightFill);
                    results.add(rightAir);
                    FillJob upFill = new FillJob("arc",5,curFrame,false,startX.intValue(),endX.intValue(),startY.intValue()+1,endY.intValue()+1,startZ.intValue(),endZ.intValue(),red_arc_material,jobName);
                    FillJob upAir = new FillJob("air",10,curFrame+1,false,startX.intValue(),endX.intValue(),startY.intValue()+1,endY.intValue()+1,startZ.intValue(),endZ.intValue(),air_material,jobName);
                    results.add(upFill);
                    results.add(upAir);
                }
            }else if (color==2){//颜色2，绿色蛇
                FillJob centreFill = new FillJob("arc",4,curFrame,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue(),endZ.intValue(),green_arc_centre_material,jobName);
                results.add(centreFill);
                FillJob centreAir = new FillJob("air",10,curFrame+1,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue(),endZ.intValue(),air_material,jobName);
                results.add(centreAir);
                //蓝蛇描边
                if (!green_arc_centre_only){
                    FillJob leftFill = new FillJob("arc",5,curFrame,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue()-1,endZ.intValue()-1,green_arc_material,jobName);
                    FillJob leftAir = new FillJob("air",10,curFrame+1,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue()-1,endZ.intValue()-1,air_material,jobName);
                    results.add(leftFill);
                    results.add(leftAir);
                    FillJob rightFill = new FillJob("arc",5,curFrame,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue()+1,endZ.intValue()+1,green_arc_material,jobName);
                    FillJob rightAir = new FillJob("air",10,curFrame+1,false,startX.intValue(),endX.intValue(),startY.intValue(),endY.intValue(),startZ.intValue()+1,endZ.intValue()+1,air_material,jobName);
                    results.add(rightFill);
                    results.add(rightAir);
                    FillJob upFill = new FillJob("arc",5,curFrame,false,startX.intValue(),endX.intValue(),startY.intValue()+1,endY.intValue()+1,startZ.intValue(),endZ.intValue(),green_arc_material,jobName);
                    FillJob upAir = new FillJob("air",10,curFrame+1,false,startX.intValue(),endX.intValue(),startY.intValue()+1,endY.intValue()+1,startZ.intValue(),endZ.intValue(),air_material,jobName);
                    results.add(upFill);
                    results.add(upAir);
                }
            }
        }
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
            return ground_z+track*ground_interval+(ground_interval/2-1);
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
        while(timings.get(timings_pointer).t<=time){
            timings_pointer+=1;
            if (timings_pointer==timings.size())
                break;
        }
        //debug
//        System.out.println("Forwarding to pointer "+(timings_pointer-1));

        timings_pointer-=1;
        //当前帧的毫秒。帧对齐，这一帧内会撞判定线
        Double cur_time = Double.valueOf(time)-time%frame_time;
//        Double cur_time = Double.valueOf(time);
        //位置的帧对齐，使长度极短的物件如装饰arc也可以渲染
        Double cur_x = (time%frame_time)*timings.get(timings_pointer).bpm*default_speed_per_second/bpm_base/1000+ground_x;
        Double forward_frame;
        Integer start_timing,end_timing;
        while((track_x_upper_limit+ground_x)>cur_x && cur_x>(track_x_lower_limit+ground_x)) {
            // 上一帧时间和下一帧时间
            forward_frame = cur_time - frame_time;
            // 获取两帧之间的所有timings并计算note位移
            start_timing = 0;
            end_timing = 0;
            for (int index = 0; index < timings.size(); index++) {
                // 不是最后一个timing
                if (index < (timings.size() - 1)) {
                    if (timings.get(index).t <= forward_frame && forward_frame <= timings.get(index + 1).t)
                        start_timing = index;
                    if (timings.get(index).t <= cur_time && cur_time <= timings.get(index + 1).t)
                        end_timing = index;
                }
                // 最后一个timing
                else {
                    if (timings.get(index).t < forward_frame) {
                        start_timing = index;
                        end_timing = index;
                    }
                }
            }
            if (start_timing == end_timing){//在同一个timing里面
                cur_x += frame_time * timings.get(start_timing).bpm * default_speed_per_second / bpm_base / 1000;
            }else{ // 一帧横跨多个timing
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
            if(scenecontrols.get(index).type.equalsIgnoreCase("hidegroup")){
                scene_frame = (int)(scenecontrols.get(index).t/frame_time);
                if(closest_frame <= scene_frame && scene_frame <= frame){
                    closest_frame = scene_frame;
                    if(scenecontrols.get(index).param2==1) {
                        hide_flag = true;
                        break;
                    }
                    else if (scenecontrols.get(index).param2==0) {
                        hide_flag = false;
                        break;
                    }
                    else{
                        System.out.println(scenecontrols.get(index).toString());
                        System.out.println("非法的hidegroup数值");
                        hide_flag = false;
                        break;
                    }
                }
            }
        }
        return hide_flag;
    }

    /**
     * 获得一个FillJob在下一帧需要填充为的空气
     * @param source
     * @return
     */
    public FillJob nextFrameAir(FillJob source){
        FillJob result = source;
        result.type="air";
        result.frame+=1;
        result.material=air_material;
        result.priority=10;
        return result;
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
        //Render.Time
        this.wait_after_last_note=config.getDouble("Render.Position.ground_x");
        this.zero_time_arc_play_dense=config.getDouble("Render.Time.zero_time_arc_play_dense");
        //Render.Particle 粒子配置预读取
        this.enable_black_line=config.getBoolean("Render.Particle.enable_black_line");
        this.black_line_particle_dense=config.getDouble("Render.Particle.black_line_particle_dense");
        this.enable_double_note_line=config.getBoolean("Render.Particle.enable_double_note_line");
        this.double_note_line_particle_dense=config.getDouble("Render.Particle.double_note_line_particle_dense");
        //Render.Material 材料配置预读取
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
        this.track_surface_material=config.getString("Render.Material.track_surface_material");
        this.track_line_material=config.getString("Render.Material.track_line_material");
        this.extend_track_surface_material=config.getString("Render.Material.extend_track_surface_material");
        this.extend_track_line_material=config.getString("Render.Material.extend_track_line_material");
    }
}
