package tempestissimo.club.arcaea.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tempestissimo.club.arcaea.utils.entities.Difficulty;
import tempestissimo.club.arcaea.utils.entities.Song;
import tempestissimo.club.arcaea.utils.entities.Pack;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SongScanner {
    public String execute_path;
    public ArrayList<Song> song_list = new ArrayList<>();
    public ArrayList<Pack> pack_list = new ArrayList<>();

    /**
     * 从songlist抽取一定数量的连续歌曲
     * @param start
     * @param number
     * @return
     */
    public ArrayList<Song> select_from_song_list(Integer start,Integer number){
        ArrayList<Song> result=new ArrayList<>();
        if (start<0)
            start=0;
        if(number<0)
            number=0;
        if (start> song_list.size())
            start= song_list.size();
        if (start+number> song_list.size())
            number= song_list.size()-start;
        for (int i = start; i < start+number; i++)
            result.add(song_list.get(i));
        return result;
    }

    /**
     * 根据歌曲id以及谱面难度生成文件路径
     * @param songIndex
     * @param ratingClass
     * @return
     */
    public String generate_aff_file_path(Integer songIndex,Integer ratingClass){
        String songName=song_list.get(songIndex).id;
        String song_folder = execute_path.concat("/songs/").concat(songName.replace("\"","")).concat("/");
        String aff_path = song_folder.concat(song_list.get(songIndex).difficulties.get(ratingClass).ratingClass.toString()).concat(".aff");
        return aff_path;
    }

    /**
     * 检查单个aff文件是否存在
     * @param songIndex 歌曲id
     * @param ratingClass aff难度
     * @return 存在性
     */
    public Boolean check_file_if_exist(Integer songIndex,Integer ratingClass){
        String aff_path = generate_aff_file_path(songIndex,ratingClass);
        File file=new File(aff_path);
        if (file.exists()){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 解析songlist以及歌曲并检查完整性
     * @return ArrayList形式存储的歌曲列表
     */
    public ArrayList<Song> scan_song(){
        this.song_list = new ArrayList<>();
        String song_list_path = execute_path.concat("/songlist");
        String song_list_json = this.file_read(song_list_path);
        JsonObject songlist_obj = JsonParser.parseString(song_list_json).getAsJsonObject();
        JsonArray songs = songlist_obj.get("songs").getAsJsonArray();
        for (int i=0;i<songs.size();i++){
            JsonObject song = songs.get(i).getAsJsonObject();
            try{
                Song single_song=song_parser(song);
                this.song_list.add(single_song);
            }catch (Exception e){
                System.out.println(song.toString());
                System.exit(0);
            }
        }
        this.generate_availability(execute_path);
        return this.song_list;
    }

    /**
     * 解析曲包信息
     * @return ArrayList形式存储的曲包列表
     */
    public ArrayList<Pack> scan_pack() {
        String pack_list_path = execute_path.concat("/packlist");
        String pack_list_json = this.file_read(pack_list_path);
        JsonObject packlist_obj = JsonParser.parseString(pack_list_json).getAsJsonObject();
        JsonArray packs = packlist_obj.get("packs").getAsJsonArray();
        for (int i=0;i<packs.size();i++){
            Pack pack = new Pack();
            JsonObject json_pack=packs.get(i).getAsJsonObject();
            pack.id= String.valueOf(json_pack.get("id"));
            pack.plus_character = json_pack.get("plus_character").getAsInt();
            JsonObject name_localized_json=json_pack.getAsJsonObject("name_localized");
            pack.name_localized=new HashMap<>();
            for (String key: name_localized_json.keySet())
                pack.name_localized.put(key,name_localized_json.get(key).getAsString().replace("\n",""));
            JsonObject description_localized_json=json_pack.getAsJsonObject("description_localized");
            pack.description_localized=new HashMap<>();
            for (String key: description_localized_json.keySet())
                pack.description_localized.put(key,description_localized_json.get(key).getAsString().replace("\n",""));
            pack_list.add(pack);
        }
        return this.pack_list;
    }

    /**
     * 检查解析到song_list的歌曲描述与文件系统的一致性
     * @param execute_path 存储根目录
     */
    public void generate_availability(String execute_path){
        for (int i=0;i<song_list.size();i++){
            String song_folder = execute_path.concat("/songs/").concat(song_list.get(i).id.replace("\"","")).concat("/");
            //检查Aff文件一致性
            for (int j=0;j<song_list.get(i).difficulties.size();j++){
                String aff_path = song_folder.concat(song_list.get(i).difficulties.get(j).ratingClass.toString()).concat(".aff");
                File file=new File(aff_path);
                if (file.exists()){
                    song_list.get(i).difficulties.get(j).aff_available=true;
                }else{
                    song_list.get(i).difficulties.get(j).aff_available=false;
                }
            }
            //检查Ogg文件一致性
            String ogg_path =  execute_path.concat("/songs/").concat(song_list.get(i).id.replace("\"","")).concat("/base.ogg");
            File file=new File(ogg_path);
            if (file.exists()){
                song_list.get(i).music_available=true;
            }else{
                song_list.get(i).music_available=false;
            }
        }
    }

//    /**
//     * 逐行读取文件
//     * @param file_path 待读取文件路径
//     * @return 字符串总和
//     */
//    private String file_read(String file_path){
//        String sumString="";
//        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file_path))) {
//            String tempString;
//            while ((tempString = bufferedReader.readLine()) != null) {
//                tempString = tempString.strip();
//                sumString = sumString.concat(tempString);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return sumString;
//    }

    public static String file_read(String filePath) {
        String fileContent = "";
        try {
            File f = new File(filePath);
            if (f.isFile() && f.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(f), "UTF-8");
                BufferedReader reader = new BufferedReader(read);
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent = fileContent.concat(line.strip());
                }
                read.close();
            }
        } catch (Exception e) {
            System.out.println("读取文件内容操作出错");
            e.printStackTrace();
        }
        return fileContent;
    }

    /**
     * 解析song字符串
     * @param json json字符串
     * @return Song
     */
    public Song song_parser(JsonObject json){
        Song song=new Song();
        song.idx = json.get("idx").getAsInt();
        song.id = json.get("id").getAsString();
        //title_localized
        song.title_localized = new HashMap<>();
        JsonObject title_json = json.getAsJsonObject("title_localized");
        for (String key : title_json.keySet()){
            song.title_localized.put(key,title_json.get(key).getAsString());
        }
//        String title_en = String.valueOf(title_json.get("en"));
//        if (title_en!=null)
//            song.title_localized.put("en",title_en);
//        String title_ja = String.valueOf(title_json.get("ja"));
//        if (title_ja!=null)
//            song.title_localized.put("ja",title_ja);
//        String title_ko = String.valueOf(title_json.get("ko"));
//        if (title_ko!=null)
//            song.title_localized.put("ko",title_ko);
//        String title_zh_Hant = String.valueOf(title_json.get("zh-Hant"));
//        if (title_zh_Hant!=null)
//            song.title_localized.put("zh-Hant",title_zh_Hant);
//        String title_zh_Hans = String.valueOf(title_json.get("zh-Hans"));
//        if (title_zh_Hans!=null)
//            song.title_localized.put("zh-Hans",title_zh_Hans);

        song.artist = json.get("artist").getAsString();
        song.bpm = json.get("bpm").getAsString();
        song.bpm_base = json.get("bpm_base").getAsFloat();
        song.set = json.get("set").getAsString();
        song.purchase = json.get("purchase").getAsString();
        song.audioPreview = json.get("audioPreview").getAsInt();
        song.audioPreviewEnd = json.get("audioPreviewEnd").getAsInt();
        song.side = json.get("side").getAsInt();
        song.bg = json.get("bg").getAsString();
        song.date = json.get("date").getAsInt();
        song.version = json.get("version").getAsString();
        //difficulties
        song.difficulties = new ArrayList<>();
        JsonArray difficulties = json.getAsJsonArray("difficulties");
        for (int i=0;i< difficulties.size();i++){
            JsonObject temp_difficulty = difficulties.get(i).getAsJsonObject();
            Difficulty diff = new Difficulty();
            if (temp_difficulty.has("ratingClass")){
                diff.ratingClass = temp_difficulty.get("ratingClass").getAsInt();
            }
            if (temp_difficulty.has("chartDesigner")){
                diff.chartDesigner = temp_difficulty.get("chartDesigner").getAsString();
            }
            if (temp_difficulty.has("jacketDesigner")){
                diff.jacketDesigner = temp_difficulty.get("jacketDesigner").getAsString();
            }
            if (temp_difficulty.has("rating")){
                diff.rating = temp_difficulty.get("rating").getAsInt();
            }
            if (temp_difficulty.has("ratingPlus")){
                diff.ratingPlus = temp_difficulty.get("ratingPlus").getAsBoolean();
            }else{
                diff.ratingPlus = false;
            }
            if (temp_difficulty.has("hidden_until")){
                diff.hidden_until = temp_difficulty.get("hidden_until").getAsString();
            }else{
                diff.hidden_until = "";
            }
            song.difficulties.add(diff);
        }
        //additional_files
        song.additional_files = new ArrayList<>();
        if (json.has("additional_files")){
            JsonArray addi = json.getAsJsonArray("additional_files");
            for (int i=0;i<addi.size();i++){
                String res=addi.get(i).getAsString();
                song.additional_files.add(res);
            }
        }
        return song;
    }

    public SongScanner(String execute_path) {
        this.execute_path = execute_path;
    }

    public static void main(String[] args) {
        SongScanner song_scanner = new SongScanner("F:/arcaea");
        song_scanner.scan_song();
        for (Song song:song_scanner.song_list){
            System.out.println(song.toString());
            System.out.println("\n");
        }
        song_scanner.scan_pack();
        for (Pack pack:song_scanner.pack_list){
            System.out.println(pack.toString());
            System.out.println("\n");
        }
    }
}
