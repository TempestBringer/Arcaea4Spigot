package tempestissimo.club.arcaea.utils.entities.infer_related;

public class BlockFillJob {
    public Integer x;
    public Integer y;
    public Integer z;
    public Integer frame;

    public BlockFillJob(Integer x, Integer y, Integer z, Integer frame) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.frame = frame;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockFillJob that = (BlockFillJob) o;

        if (!x.equals(that.x)) return false;
        if (!y.equals(that.y)) return false;
        if (!z.equals(that.z)) return false;
        return frame.equals(that.frame);
    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        result = 31 * result + z.hashCode();
        result = 31 * result + frame.hashCode();
        return result;
    }
}
