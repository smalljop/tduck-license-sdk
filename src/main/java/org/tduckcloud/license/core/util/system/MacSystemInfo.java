package org.tduckcloud.license.core.util.system;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 从mac系统获取mac地址
 *
 * @author : tduck
 * @since :  2023/04/18 13:48
 **/
public class MacSystemInfo implements SystemInfoInterface {

    final static Logger log = LoggerFactory.getLogger(MacSystemInfo.class);


    public String getMacAddress() {
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"ifconfig", "-a"});
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("ether")) {
                    result = line.trim().split(" ")[1];
                    break;
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public String getCpuAddress() {
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/usr/sbin/system_profiler", "SPHardwareDataType"});
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("Serial Number")) {
                    result = line.split(":")[1].trim();
                    break;
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String getMainBoardAddress() {
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/usr/sbin/ioreg", "-l"});
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("IOPlatformSerialNumber")) {
                    result = line.split("\"")[3];
                    break;
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
