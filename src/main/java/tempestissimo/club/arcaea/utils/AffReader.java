package tempestissimo.club.arcaea.utils;

import tempestissimo.club.arcaea.utils.entities.note_related.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AffReader {

    /**
     * 逐行读取整个Aff文件并加入列表
     * @param path
     * @return 逐行读取的结果
     */
    public ArrayList<String> read_raw(String path){
        ArrayList<String> command_list = new ArrayList<>();
        try{
            // 需要读取的文件路径
            File file = new File(path);
            // 判断文件是否存在
            if (file.isFile() && file.exists()){
                System.out.println("Reading Raw Aff File："+path);
                InputStreamReader read = new InputStreamReader(new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineText = null;

                // 按行读取文件并打印,如果需要对内容进行操作可以在这里进行
                while((lineText = bufferedReader.readLine())!=null){
                    lineText = lineText.strip();
                    command_list.add(lineText);
                }
            }else{
                System.out.println("File Not Exist："+path);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("Raw File Read Done!");
        return command_list;
    }

    /**
     * 拆分Aff文件，产生含有若干列表的HashMap：
     * 通过'file_head_dict'可以得到Aff文件头部标注，即'-'上部的内容
     * 通过'main_timing_group'可以得到主timing_group语句的ArrayList
     * 通过'timing_groups'可以得到含有若干timingGroup描述的ArrayList，每个描述是含有若干String的ArrayList
     * 通过'timing_groups_args'可以得到含有若干timingGroup参数的ArrayList
     * 后两者的index匹配
     * @param raw
     * @return
     */
    public HashMap<String,Object> raw_to_timing_group(ArrayList<String> raw){
        System.out.println("Parsing Raw Aff to Timing Groups");
        HashMap<String,String> head_dict = new HashMap<>();
        Boolean file_head_flag = true;
        Boolean main_timing_group_flag = true;
        ArrayList<ArrayList<String>> timing_groups = new ArrayList<>();
        ArrayList<String> timing_group = new ArrayList<>();
        ArrayList<String> main_timing_group = new ArrayList<>();
        ArrayList<String> timing_groups_args = new ArrayList<>();
        for(int i=0;i<raw.size();i++){
            String str = raw.get(i);
            if(file_head_flag==true){
                if(str.startsWith("-")){
                    file_head_flag = false;
                }else{
                    String[] segments = str.split(":");
                    head_dict.put(segments[0],segments[1]);
                }
            }else{
                if(main_timing_group_flag==true){
                    if(str.startsWith("timinggroup")){
                        main_timing_group_flag=false;
                        Integer str_length = str.length();
                        timing_groups_args.add(str.substring(12,str_length-2));
                    }else{
                        main_timing_group.add(str);
                    }
                }else{
                    if(str.startsWith("};")){
                        main_timing_group_flag=true;
                        timing_groups.add(timing_group);
                        timing_group = new ArrayList<>();
                    }else{
                        timing_group.add(str);
                    }
                }
            }
        }
        HashMap<String,Object> result = new HashMap<>();
        result.put("file_head_dict",head_dict);
        result.put("main_timing_group",main_timing_group);
        result.put("timing_groups",timing_groups);
        result.put("timing_groups_args",timing_groups_args);
        System.out.println("Parsing Raw Aff Done!");
        return result;
    }

    /**
     * 解析含有若干String的timingGroup描述，得到含有对当前timingGroup的物件分组后结果的HashMap：
     * 通过'timings'可以得到当前timgGroup的timing语句；
     * 通过'scenecontrols'可以得到当前timgGroup的scenecontrol语句；
     * 通过'arcs'可以得到当前timgGroup的arc语句；
     * 通过'notes'可以得到当前timgGroup的note语句；
     * 通过'holds'可以得到当前timgGroup的hold语句；
     * 通过'cameras'可以得到当前timgGroup的camera语句；
     * @param lines
     * @return
     */
    public HashMap<String,Object> timing_group_parser(ArrayList<String> lines){
        HashMap<String,Object> result = new HashMap<>();
        ArrayList<Note> notes = new ArrayList<>();
        ArrayList<Hold> holds = new ArrayList<>();
        ArrayList<Arc> arcs = new ArrayList<>();
        ArrayList<Scenecontrol> scenecontrols = new ArrayList<>();
        ArrayList<Timing> timings = new ArrayList<>();
        ArrayList<Camera> cameras = new ArrayList<>();

        String line = "";
        for (int i=0; i<lines.size();i++)
            line = lines.get(i).strip();
            if (line.startsWith("timing(")){
                String[] segments = line.replace("timing(","").replace(");","").split(",");
                timings.add(new Timing(Integer.parseInt(segments[0]),Float.parseFloat(segments[1]),Float.parseFloat(segments[2])));
            }else if (line.startsWith("(")){
                String[] segments = line.replace("(","").replace(");","").split(",");
                notes.add(new Note(Integer.parseInt(segments[0]),Integer.parseInt(segments[1])));
            }else if (line.startsWith("hold(")){
                String[] segments = line.replace("hold(","").replace(");","").split(",");
                holds.add(new Hold(Integer.parseInt(segments[0]),Integer.parseInt(segments[1]),Integer.parseInt(segments[2])));
            }else if (line.startsWith("arc(")){
                String arc = line.replace("[","/").split("/")[0];
                String tap = line.replace("[","/").split("/")[1];
                ArrayList<Integer> arctaps = new ArrayList<>();
                String[] tap_segments = tap.replace("arctap(","").replace(")","").replace("];","").split(",");
                for (int i=0;i< tap_segments.length;i++)
                    arctaps.add(Integer.parseInt(tap_segments[i]));
                String[] arc_segments = arc.replace("arc(","").replace(")","").split(",");
                Boolean flag = true;
                if (arc_segments[8]=="true"){
                    flag = true;
                }else
                    flag = false;
                arcs.add(new Arc(Integer.parseInt(arc_segments[0]),Integer.parseInt(arc_segments[1]),Float.parseFloat(arc_segments[2]),
                        Float.parseFloat(arc_segments[3]),arc_segments[4],Float.parseFloat(arc_segments[5]),Float.parseFloat(arc_segments[6]),
                        Integer.parseInt(arc_segments[7]),arc_segments[8],flag,arctaps));
            }else if (line.startsWith("scenecontrol(")){
                String[] segments = line.replace("scenecontrol(","").replace(");","").split(",");
                scenecontrols.add(new Scenecontrol(Integer.parseInt(segments[0]),segments[1],Float.parseFloat(segments[2]),Integer.parseInt(segments[3])));

            }else if (line.startsWith("camera(")){
                // ignore
            }else{
            }
        System.out.println("finished parse");
        result.put("timings",timings);
        result.put("scenecontrols",scenecontrols);
        result.put("arcs",arcs);
        result.put("notes",notes);
        result.put("holds",holds);
        result.put("cameras",cameras);
        return result;
    }

    public static void main(String[] args) throws Exception {
        String path = "F:/arcaea/songs/pentiment/3.aff";
        AffReader reader = new AffReader();
        ArrayList<String> command_list = reader.read_raw(path);
        HashMap<String,Object> result = reader.raw_to_timing_group(command_list);
        HashMap<String,String> file_head_dict = (HashMap<String, String>) result.get("file_head_dict");
        ArrayList<ArrayList<String>> timing_groups = (ArrayList<ArrayList<String>>) result.get("timing_groups");
        ArrayList<String> main_timing_group = (ArrayList<String>) result.get("main_timing_group");
        ArrayList<String> timing_groups_args = (ArrayList<String>) result.get("timing_groups_args");
        reader.timing_group_parser(main_timing_group);
        System.out.println(file_head_dict);
    }
}
