package org.tduckcloud.license.core.util.system;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 从linux系统获取mac地址
 *
 * @author : tduck
 * @since :  2023/04/18 13:48
 **/
public class LinuxSystemInfo implements SystemInfoInterface {

    final static Logger log = LoggerFactory.getLogger(LinuxSystemInfo.class);


    public String getMacAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        String REGEX = "\\b\\w+:\\w+:\\w+:\\w+:\\w+:\\w+\\b";

        try {
            // Linux下的命令 显示或设置网络设备
            process = Runtime.getRuntime().exec("ifconfig");
            // 显示信息中包含有 MAC 地址信息
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                Pattern pat = Pattern.compile(REGEX);
                Matcher mat = pat.matcher(line);
                if (mat.find()) {
                    mac = mat.group(0);
                }
            }
        } catch (IOException e) {
            log.error("获取 Linux MAC 信息错误", e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
                log.error("获取 Linux MAC 信息错误", e1);
            }
        }
        return mac;

    }


    public String getCpuAddress() {
        String cpuSerialNumber = "";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "dmidecode -t processor | grep ID"});
            process.getOutputStream().close();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("ID:")) {
                    cpuSerialNumber = line.split(":")[1].trim();
                    break;
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cpuSerialNumber;
    }

    @Override
    public String getMainBoardAddress() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/sys/class/dmi/id/board_serial"));
            String line = reader.readLine();
            reader.close();
            return line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
