package tempestissimo.club.arcaea.utils;

import tempestissimo.club.arcaea.utils.entities.infer_related.BlockFillJob;
import tempestissimo.club.arcaea.utils.entities.infer_related.FillJob;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class FillJobOptimizer {
    public static FillJob[] optimizeFillJobs(ArrayList<FillJob> fillJobs){
        FillJob[] results = new FillJob[0];
        if (fillJobs.size()==0){
            return results;
        }
        // 按照帧升序排序
        fillJobs.sort(new Comparator<FillJob>() {
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

                if (o1.frame < o2.frame)
                    return -1;
                else if (o1.frame.equals(o2.frame)) {
                    return 0;
                } else {
                    return 1;
                }
            }

        });
        // 标记起始帧和结束帧
        Integer minFrame = fillJobs.get(0).frame;
        Integer maxFrame = fillJobs.get(fillJobs.size()-1).frame;
        // 按照 帧-列表 分类渲染工作
        HashMap<Integer,ArrayList<FillJob>> shuffledFillJobLists = new HashMap<>();
        for (FillJob job:fillJobs){
            // 假如不存在则创建
            if (!shuffledFillJobLists.containsKey(job.frame)){
                shuffledFillJobLists.put(job.frame,new ArrayList<>());
            }
            shuffledFillJobLists.get(job.frame).add(job);
        }
        // 存储占用方块的状态, Key Obj代表位置
        HashMap<BlockFillJob, FillJob> pool = new HashMap<>();
        // 帧内优化，同时生成每帧时候的状态
        // 对于每一帧
        for (Integer i = minFrame;i<=maxFrame;i++){
            ArrayList<FillJob> shuffledFillJobList = shuffledFillJobLists.get(i);
            // 任务登记
            for (FillJob job:shuffledFillJobList){
                // 拆分渲染命令到块，这些FillJob每一条仅一个方块，三个维度上的渲染上下限相等
                ArrayList<FillJob> blockFillJobs = decomposeFillJobToFillJob(job);
                // 对于每一个单块FillJob，加入到HashMap，Key为包含x，y，z和帧的字符串
                for (FillJob blockJob:blockFillJobs){

                    BlockFillJob keyObj = new BlockFillJob((int)blockJob.x_low, (int)blockJob.y_low, (int)blockJob.z_low, blockJob.frame);
                    // 假如池内存在，即某帧的某个方块被放置过
                    if (pool.containsKey(keyObj)){
                        FillJob curFrameBlock = pool.get(keyObj);
                        // 已经存在的方块的优先级低于这个方块任务的优先级，则替换；其他情况，忽略
                        if (curFrameBlock.priority>blockJob.priority){
                            pool.put(keyObj,blockJob);
                        }
                    }
                    // 假如池内不存在，即某帧的某个方块尚未被放置过
                    else {
                        pool.put(keyObj, blockJob);
                    }
                }
            }
        }

        // 导出
        return  pool.values().toArray(new FillJob[pool.size()]);
    }


    public static ArrayList<FillJob> decomposeFillJobToFillJob(FillJob job){
        ArrayList<FillJob> results = new ArrayList<>();
        for (int x= (int)job.x_low;x<=job.x_high;x++){
            for (int y= (int)job.y_low;y<=job.y_high;y++){
                for (int z= (int)job.z_low;z<=job.z_high;z++){
                    FillJob temp = new FillJob(job.type,job.priority,job.frame,job.behind_line,x,x,y,y,z,z,job.material,job.jobName);
                    results.add(temp);
                }
            }
        }
        return results;
    }
}
