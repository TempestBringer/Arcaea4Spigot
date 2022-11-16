package tempestissimo.club.arcaea.utils.entities;

import java.util.ArrayList;
import java.util.HashMap;

public class Pack {
    /**
     * 曲包内部名字，不做展示，与song的set对应
     */
    public String id;
    /**
     * 是否附赠搭档，-1=不附带，其他=搭档数字编号
     */
    public Integer plus_character;
    /**
     * 曲包的本地化译名
     */
    public HashMap<String,String> name_localized;
    /**
     * 曲包的本地化描述
     */
    public HashMap<String,String> description_localized;

    public Pack(){}

    @Override
    public String toString() {
        return "Pack{" +
                "id='" + id + '\'' +
                ", plus_character=" + plus_character +
                ", name_localized=" + name_localized +
                ", description_localized=" + description_localized +
                '}';
    }
}
