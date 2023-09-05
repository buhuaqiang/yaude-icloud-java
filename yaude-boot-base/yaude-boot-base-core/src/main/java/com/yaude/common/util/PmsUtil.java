package com.yaude.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class PmsUtil {


    private static String uploadPath;

    @Value("${jeecg.path.upload}")
    public void setUploadPath(String uploadPath) {
        PmsUtil.uploadPath = uploadPath;
    }

    public static String saveErrorTxtByList(List<String> msg, String name) {
        Date d = new Date();
        String saveDir = "logs" + File.separator + DateUtils.yyyyMMdd.get().format(d) + File.separator;
        String saveFullDir = uploadPath + File.separator + saveDir;

        File saveFile = new File(saveFullDir);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        name += DateUtils.yyyymmddhhmmss.get().format(d) + Math.round(Math.random() * 10000);
        String saveFilePath = saveFullDir + name + ".txt";

        try {
            //封裝目的地
            BufferedWriter bw = new BufferedWriter(new FileWriter(saveFilePath));
            //遍歷集合
            for (String s : msg) {
                //寫數據
                if (s.indexOf("_") > 0) {
                    String arr[] = s.split("_");
                    bw.write("第" + arr[0] + "行:" + arr[1]);
                } else {
                    bw.write(s);
                }
                //bw.newLine();
                bw.write("\r\n");
            }
            //釋放資源
            bw.flush();
            bw.close();
        } catch (Exception e) {
            log.info("excel導入生成錯誤日志文件異常:" + e.getMessage());
        }
        return saveDir + name + ".txt";
    }

}
