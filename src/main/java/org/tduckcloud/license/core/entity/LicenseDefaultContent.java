package org.tduckcloud.license.core.entity;

/**
 * 默认授权内容
 *
 * @author : tduck
 * @since :  2023/04/14 15:08
 **/
public class LicenseDefaultContent {


    /**
     * 过期时间
     */
    private String expireDate;

    /**
     * 授权类型
     * 1. 不限制设备数
     * 2. 限制设备 会校验设备序列号
     */
    private Integer licenseType;

    /**
     * cpu序列号
     */
    private String cpuSerialNumber;

    /**
     * mac地址
     */
    private String macAddress;


    public String getExpireDate() {
        return expireDate;
    }

    public LicenseDefaultContent setExpireDate(String expireDate) {
        this.expireDate = expireDate;
        return this;
    }

    public Integer getLicenseType() {
        return licenseType;
    }

    public LicenseDefaultContent setLicenseType(Integer licenseType) {
        this.licenseType = licenseType;
        return this;
    }

    public String getCpuSerialNumber() {
        return cpuSerialNumber;
    }

    public LicenseDefaultContent setCpuSerialNumber(String cpuSerialNumber) {
        this.cpuSerialNumber = cpuSerialNumber;
        return this;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public LicenseDefaultContent setMacAddress(String macAddress) {
        this.macAddress = macAddress;
        return this;
    }
}
