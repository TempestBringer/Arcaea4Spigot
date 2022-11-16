package tempestissimo.club.arcaea.utils.entities;

public class Difficulty {
    /**
     * 难度分级，用于切换难度。0=past，1=present，2=future,3=beyond
     */
    public Integer ratingClass;
    /**
     * 铺面设计
     */
    public String chartDesigner;
    /**
     * 插画设计
     */
    public String jacketDesigner;
    /**
     * 详细的难度数字等级
     */
    public Integer rating;
    /**
     * 难度是否有加号
     */
    public Boolean ratingPlus;
    /**
     * 区分隐藏曲
     */
    public String hidden_until;
    /**
     * 谱面Aff文件的可用性
     */
    public Boolean aff_available;

    public Difficulty() {}

    public Integer getRatingClass() {
        return ratingClass;
    }

    public void setRatingClass(Integer ratingClass) {
        this.ratingClass = ratingClass;
    }

    public String getChartDesigner() {
        return chartDesigner;
    }

    public void setChartDesigner(String chartDesigner) {
        this.chartDesigner = chartDesigner;
    }

    public String getJacketDesigner() {
        return jacketDesigner;
    }

    public void setJacketDesigner(String jacketDesigner) {
        this.jacketDesigner = jacketDesigner;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Boolean getRatingPlus() {
        return ratingPlus;
    }

    public void setRatingPlus(Boolean ratingPlus) {
        this.ratingPlus = ratingPlus;
    }

    public String getHidden_until() {
        return hidden_until;
    }

    public void setHidden_until(String hidden_until) {
        this.hidden_until = hidden_until;
    }

    public Boolean getAff_available() {
        return aff_available;
    }

    public void setAff_available(Boolean aff_available) {
        this.aff_available = aff_available;
    }

    public Difficulty(Integer ratingClass, String chartDesigner, String jacketDesigner, Integer rating, Boolean ratingPlus, String hidden_until) {
        this.ratingClass = ratingClass;
        this.chartDesigner = chartDesigner;
        this.jacketDesigner = jacketDesigner;
        this.rating = rating;
        this.ratingPlus = ratingPlus;
        this.hidden_until = hidden_until;
    }

    @Override
    public String toString() {
        return "Difficulty{" +
                "ratingClass=" + ratingClass +
                ", chartDesigner='" + chartDesigner + '\'' +
                ", jacketDesigner='" + jacketDesigner + '\'' +
                ", rating=" + rating +
                ", ratingPlus=" + ratingPlus +
                ", hidden_until='" + hidden_until + '\'' +
                ", aff_available=" + aff_available +
                '}';
    }
}
