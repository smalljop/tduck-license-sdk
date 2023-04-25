package org.tduckcloud.license.core.check;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tduckcloud.license.core.entity.LicenseDefaultContent;
import org.tduckcloud.license.core.entity.LicenseEntity;
import org.tduckcloud.license.core.util.SystemInfoUtils;
import org.tduckcloud.license.core.util.system.WindowsSystemInfo;

import java.time.LocalDateTime;

/**
 * 默认授权校验策略
 *
 * @author : tduck
 * @since :  2023/04/14 15:26
 **/
public class DefaultLicenseCheckStrategy implements LicenseCheckStrategy {
    final static Logger log = LoggerFactory.getLogger(WindowsSystemInfo.class);

    /**
     * 校验授权
     *
     * @param licenseEntity 授权实体
     * @return true: 校验通过
     */
    @Override
    public boolean check(LicenseEntity licenseEntity) {
        byte[] data = licenseEntity.getData();
        // data
        String dataJson = new String(data);
        Gson gson = new Gson();
        LicenseDefaultContent licenseDefaultContent = gson.fromJson(dataJson, LicenseDefaultContent.class);
        // licenseDefaultContent
        // 1. 校验license是否过期
        LocalDateTime expireDate = LocalDateTime.parse(licenseDefaultContent.getExpireDate());
        if (expireDate.isBefore(LocalDateTime.now())) {
            return false;
        }
        // 2. 校验是什么级别的授权
        Integer licenseType = licenseDefaultContent.getLicenseType();
        if (LICENSE_TYPE_ANY_MACHINE == licenseType) {
            return true;
        }
        // 3. 校验指定设备
        if (LICENSE_TYPE_SPECIFY_MACHINE == licenseType) {
            String cpuSerialNumber = licenseDefaultContent.getCpuSerialNumber();
            String macAddress = licenseDefaultContent.getMacAddress();
            String systemCpuAddress = SystemInfoUtils.getSystemInfo().getCpuAddress();
            String systemMacAddress = SystemInfoUtils.getSystemInfo().getMacAddress();
            if (cpuSerialNumber.equals(systemCpuAddress) && macAddress.equals(systemMacAddress)) {
                return true;
            }
            log.error("授权校验失败, cpu: {}, mac: {}", systemCpuAddress, systemMacAddress);
        }
        return false;
    }
}
