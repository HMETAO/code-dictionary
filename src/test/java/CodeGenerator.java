import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import org.junit.Test;

import java.sql.Types;
import java.util.Collections;

public class CodeGenerator {

    @Test
    public void run() {
        String projectPath = System.getProperty("user.dir");
        FastAutoGenerator.create("jdbc:mysql://106.52.247.154:13306/sanhang?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8", "root", "root")
                .globalConfig(builder -> {
                    builder.author("lwt") // 设置作者
                            .disableOpenDir()
                            .enableSwagger() // 开启 swagger 模式
                            .outputDir(projectPath + "/src/main/java"); // 指定输出目录
                })
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT) {
                        // 自定义类型转换
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);
                }))
                .packageConfig(builder -> {
                    builder.parent("com.yshvv.xinvo") // 设置父包名
                            .moduleName("identify") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, projectPath + "/src/main/java")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("bsnss_identify") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_","bsnss_") // 设置过滤表前缀
                            .entityBuilder().enableLombok().enableChainModel()
                            .controllerBuilder().enableRestStyle().enableHyphenStyle()
                            .serviceBuilder().formatServiceFileName("%sService");
                })
                .execute();
    }

}
