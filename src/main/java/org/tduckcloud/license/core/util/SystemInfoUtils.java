package org.tduckcloud.license.core.util;

import org.tduckcloud.license.core.util.system.LinuxSystemInfo;
import org.tduckcloud.license.core.util.system.MacSystemInfo;
import org.tduckcloud.license.core.util.system.SystemInfoInterface;
import org.tduckcloud.license.core.util.system.WindowsSystemInfo;

/**
 * 获取系统信息
 *
 * @author : tduck
 * @since :  2023/04/18 15:25
 **/
public class SystemInfoUtils {
    static SystemInfoInterface systemInfoInterface;

    static {
        // 当前是什么系统
        String systemId = System.getProperty("os.name").toLowerCase();
        if (systemId.startsWith("windows")) {
            systemInfoInterface = new WindowsSystemInfo();
        } else if (systemId.startsWith("linux")) {
            systemInfoInterface = new LinuxSystemInfo();
        } else if (systemId.startsWith("mac")) {
            systemInfoInterface = new MacSystemInfo();
        } else {
            throw new RuntimeException("Unknown system" + systemId);
        }
    }

    /**
     * 获取当前系统对应的实现
     */
    public static SystemInfoInterface getSystemInfo() {
        return systemInfoInterface;
    }

    public static void main(String[] args) {
        String macAddress = getSystemInfo().getMacAddress();
        System.out.println(macAddress);
        String cpuAddress = getSystemInfo().getCpuAddress();
        System.out.println(cpuAddress);
        String mainBoardAddress = getSystemInfo().getMainBoardAddress();
        System.out.println(mainBoardAddress);
    }

}
