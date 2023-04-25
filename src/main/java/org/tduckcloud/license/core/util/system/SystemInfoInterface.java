package org.tduckcloud.license.core.util.system;

/**
 * 系统信息
 * @author : tduck
 * @since :  2023/04/18 13:47
 **/
public interface SystemInfoInterface {

    /**
     * 获取mac地址
     * @return mac address
     */
    String getMacAddress();


    /**
     * 获取cpu序列号
     */
    String getCpuAddress();

    /**
     * 主板序列号
     * 不怎么准确 有些主板获取不到
     */
    String getMainBoardAddress();

}
