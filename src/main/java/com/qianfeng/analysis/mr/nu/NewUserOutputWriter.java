package com.qianfeng.analysis.mr.nu;
import com.qianfeng.analysis.model.base.BaseDimension;
import com.qianfeng.analysis.model.key.StatsUserDimension;
import com.qianfeng.analysis.model.value.OutputMapWritable;
import com.qianfeng.analysis.model.value.StatsOutputValue;
import com.qianfeng.analysis.mr.IoOutputWriter;
import com.qianfeng.analysis.mr.service.IDimension;
import com.qianfeng.common.GlobalConstants;
import com.qianfeng.common.KpiType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewUserOutputWriter  implements IoOutputWriter {
    @Override
    public void output(Configuration conf, BaseDimension key, StatsOutputValue statsOutputValue, PreparedStatement ps, IDimension iDimension) {
        try {
            StatsUserDimension k= (StatsUserDimension)key;
            OutputMapWritable value = (OutputMapWritable)statsOutputValue;//reduce端的输出   {维度：{kpiName,uuid.size}}
            //获取新增用户值
            int newUser=((IntWritable)(value.getValue().get(new IntWritable(1)))).get();//Map端没有指定泛型所以强转
            int i=0;
            //通过对象返回的id 对应到数据库中的id   output_mapping.xml
            //根据传入维度对象获取维度的id
            ps.setInt(++i,iDimension.getIDimensionImplByDimension(k.getStatsCommonDimension().getDt()));//获取传入对象的id
            ps.setInt(++i,iDimension.getIDimensionImplByDimension(k.getStatsCommonDimension().getPl()));//在数据库中生成的维度的ip
            if(value.getKpi().equals(KpiType.BROWSER_NEW_USER)){
                ps.setInt(++i,iDimension.getIDimensionImplByDimension(k.getBrowserDimension()));
            }
            //需要在runner类 在运行赋值的时候设置
            ps.setInt(++i,newUser);//新增用户的数量
            ps.setString(++i,conf.get(GlobalConstants.RUNNING_DATE));
            ps.setInt(++i,newUser);
            ps.addBatch();//添加到批处理中。
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
