package tempestissimo.club.arcaea.utils.entities;

import java.util.ArrayList;
import java.util.HashMap;

public class Song {
    /**
     * 歌曲的数字索引
     */
    public Integer idx;
    /**
     * 歌曲的内部英文名，不做前台展示
     */
    public String id;
    /**
     * 歌曲的本地化译名，用于前台展示
     */
    public HashMap<String,String> title_localized;
    /**
     * 歌曲的作曲家
     */
    public String artist;
    /**
     * 歌曲的bpm字符串，仅用于展示，不用于计算
     */
    public String bpm;
    /**
     * 歌曲的基准bpm，不用于展示，但用于计算
     */
    public Float bpm_base;
    /**
     * 歌曲所在的曲包，对应Pack的'id'
     */
    public String set;
    /**
     * 伴随何种购买行为解锁，字符串
     */
    public String purchase;
    /**
     * 歌曲音频预览起点，单位毫秒
     */
    public Integer audioPreview;
    /**
     * 歌曲音频预览终点，单位毫秒
     */
    public Integer audioPreviewEnd;
    /**
     * 背景侧，0=光侧，1=对立侧
     */
    public Integer side;
    /**
     * 背景图片文件名
     */
    public String bg;
    /**
     * 更新时的时间戳
     */
    public Integer date;
    /**
     * 更新时的版本号
     */
    public String version;
    /**
     * 难度列表
     */
    public ArrayList<Difficulty> difficulties;
    /**
     * 附加文件列表
     */
    public ArrayList<String> additional_files;
    /**
     * 音频可用性
     */
    public Boolean music_available;

    public Song() {}

//    public Song(JsonObject json){
//        this.idx = Integer.parseInt(String.valueOf(json.get("idx")));
//        this.id = String.valueOf(json.get("id"));
//        //title_localized
//        this.title_localized = new HashMap<>();
//        JsonObject title_json = json.getAsJsonObject("title_localized");
//        String title_en = String.valueOf(title_json.get("en"));
//        if (title_en!=null)
//            title_localized.put("en",title_en);
//        String title_ja = String.valueOf(title_json.get("ja"));
//        if (title_ja!=null)
//            title_localized.put("ja",title_ja);
//        String title_ko = String.valueOf(title_json.get("ko"));
//        if (title_ko!=null)
//            title_localized.put("ko",title_ko);
//        String title_zh_Hant = String.valueOf(title_json.get("zh-Hant"));
//        if (title_zh_Hant!=null)
//            title_localized.put("zh-Hant",title_zh_Hant);
//        String title_zh_Hans = String.valueOf(title_json.get("zh-Hans"));
//        if (title_zh_Hans!=null)
//            title_localized.put("zh-Hans",title_zh_Hans);
//
////        for (String key:title_json.keySet())
////            this.title_localized.put(key,String.valueOf(title_json.get(key)));
////
//
//        this.artist = String.valueOf(json.get("artist"));
//        this.bpm = String.valueOf(json.get("bpm"));
//        this.bpm_base = Float.valueOf(String.valueOf(json.get("bpm_base")));
//        this.set = String.valueOf(json.get("set"));
//        this.purchase = String.valueOf(json.get("purchase"));
//        this.audioPreview = Integer.parseInt(String.valueOf(json.get("audioPreview")));
//        this.audioPreviewEnd = Integer.parseInt(String.valueOf(json.get("audioPreviewEnd")));
//        this.side = Integer.parseInt(String.valueOf(json.get("side")));
//        this.bg = String.valueOf(json.get("bg"));
//        this.date = Integer.parseInt(String.valueOf(json.get("date")));
//        this.version = String.valueOf(json.get("version"));
//        //difficulties
//        this.difficulties = new ArrayList<>();
//        JsonArray difficulties = json.getAsJsonArray("difficulties");
//        for (int i=0;i< difficulties.size();i++){
//            JsonObject temp_difficulty = difficulties.get(i).getAsJsonObject();
//            Difficulty diff = new Difficulty();
//            if (temp_difficulty.has("ratingClass")){
//                diff.ratingClass = Integer.parseInt(String.valueOf(temp_difficulty.get("ratingClass")));
//            }
//            if (temp_difficulty.has("chartDesigner")){
//                diff.chartDesigner = String.valueOf(temp_difficulty.get("chartDesigner"));
//            }
//            if (temp_difficulty.has("jacketDesigner")){
//                diff.jacketDesigner = String.valueOf(temp_difficulty.get("jacketDesigner"));
//            }
//            if (temp_difficulty.has("rating")){
//                diff.rating = Integer.parseInt(String.valueOf(temp_difficulty.get("rating")));
//            }
//            if (temp_difficulty.has("ratingPlus")){
//                diff.ratingPlus = Boolean.parseBoolean(String.valueOf(temp_difficulty.get("ratingPlus")));
//            }else{
//                diff.ratingPlus = false;
//            }
//            if (temp_difficulty.has("hidden_until")){
//                diff.hidden_until = String.valueOf(temp_difficulty.get("hidden_until"));
//            }else{
//                diff.hidden_until = "";
//            }
//            this.difficulties.add(diff);
//        }
//        //additional_files
//        this.additional_files = new ArrayList<>();
//        if (json.has("additional_files")){
//            JsonArray addi = json.getAsJsonArray("additional_files");
//            for (int i=0;i<addi.size();i++){
//                String res=addi.get(i).getAsString();
//                this.additional_files.add(res);
//            }
//        }
//    }

    @Override
    public String toString() {
        return "Song{" +
                "idx=" + idx +
                ", id='" + id + '\'' +
                ", title_localized=" + title_localized +
                ", artist='" + artist + '\'' +
                ", bpm='" + bpm + '\'' +
                ", bpm_base=" + bpm_base +
                ", set='" + set + '\'' +
                ", purchase='" + purchase + '\'' +
                ", audioPreview=" + audioPreview +
                ", audioPreviewEnd=" + audioPreviewEnd +
                ", side=" + side +
                ", bg='" + bg + '\'' +
                ", date=" + date +
                ", version='" + version + '\'' +
                ", difficulties=" + difficulties +
                ", additional_files=" + additional_files +
                '}';
    }

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, String> getTitle_localized() {
        return title_localized;
    }

    public void setTitle_localized(HashMap<String, String> title_localized) {
        this.title_localized = title_localized;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getBpm() {
        return bpm;
    }

    public void setBpm(String bpm) {
        this.bpm = bpm;
    }

    public Float getBpm_base() {
        return bpm_base;
    }

    public void setBpm_base(Float bpm_base) {
        this.bpm_base = bpm_base;
    }

    public String getPurchase() {
        return purchase;
    }

    public void setPurchase(String purchase) {
        this.purchase = purchase;
    }

    public Integer getAudioPreview() {
        return audioPreview;
    }

    public void setAudioPreview(Integer audioPreview) {
        this.audioPreview = audioPreview;
    }

    public Integer getAudioPreviewEnd() {
        return audioPreviewEnd;
    }

    public void setAudioPreviewEnd(Integer audioPreviewEnd) {
        this.audioPreviewEnd = audioPreviewEnd;
    }

    public Integer getSide() {
        return side;
    }

    public void setSide(Integer side) {
        this.side = side;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<Difficulty> getDifficulties() {
        return difficulties;
    }

    public void setDifficulties(ArrayList<Difficulty> difficulties) {
        this.difficulties = difficulties;
    }

    public ArrayList<String> getAdditional_files() {
        return additional_files;
    }

    public void setAdditional_files(ArrayList<String> additional_files) {
        this.additional_files = additional_files;
    }

    public Boolean getMusic_available() {
        return music_available;
    }

    public void setMusic_available(Boolean music_available) {
        this.music_available = music_available;
    }
}
