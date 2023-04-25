package org.tduckcloud.license.core.util.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 从windown系统获取mac地址
 *
 * @author : tduck
 * @since :  2023/04/18 13:48
 **/
public class WindowsSystemInfo implements SystemInfoInterface {
    final static Logger log = LoggerFactory.getLogger(WindowsSystemInfo.class);

    public String getMacAddress() {
        InetAddress ip = null;
        NetworkInterface ni = null;
        List<String> macList = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface
                    .getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                ni = (NetworkInterface) netInterfaces.nextElement();
                //  遍历所有 IP 特定情况，可以考虑用 ni.getName() 判断
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    ip = (InetAddress) ips.nextElement();
                    // 非127.0.0.1
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
                        macList.add(getMacFromBytes(ni.getHardwareAddress()));
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取mac地址异常" + e.getMessage());
        }
        if (macList.size() > 0) {
            return macList.get(0);
        } else {
            return "";
        }

    }

    /**
     * 从字节获取 MAC
     *
     * @param bytes - 字节
     * @return String - MAC
     * @author XinLau
     * @creed The only constant is change ! ! !
     * @since 2020/10/12 8:55
     */
    private static String getMacFromBytes(byte[] bytes) {
        StringBuilder mac = new StringBuilder();
        byte currentByte;
        boolean first = false;
        for (byte b : bytes) {
            if (first) {
                mac.append("-");
            }
            currentByte = (byte) ((b & 240) >> 4);
            mac.append(Integer.toHexString(currentByte));
            currentByte = (byte) (b & 15);
            mac.append(Integer.toHexString(currentByte));
            first = true;
        }
        return mac.toString().toUpperCase();
    }

    public String getCpuAddress() {
        String cpuSerialNumber = "";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"wmic", "cpu", "get", "ProcessorId"});
            process.getOutputStream().close();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("") && !line.trim().equalsIgnoreCase("ProcessorId")) {
                    cpuSerialNumber = line.trim();
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
            String command = "wmic baseboard get serialnumber";
            Process process = Runtime.getRuntime().exec(command);
            process.getOutputStream().close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                if (line.contains("SerialNumber")) {
                    String[] serialNumber = line.split("\\s+");
                    System.out.println("Windows Mainboard Serial Number: " + serialNumber[1]);
                    return serialNumber[1];
                }
                line = reader.readLine();
            }
        } catch (Exception e) {
            log.error("获取主板序列号异常", e);
        }
        return null;
    }
}
