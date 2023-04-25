package org.tduckcloud.license.core.check;

import org.tduckcloud.license.core.entity.LicenseEntity;

/**
 * 授权校验策略
 *
 * @author : tduck
 * @since :  2023/04/14 15:21
 **/
public interface LicenseCheckStrategy {


    /**
     * 授权类型1 任意机器
     */
    int LICENSE_TYPE_ANY_MACHINE = 1;

    /**
     * 授权类型2 指定设备
     */
    int LICENSE_TYPE_SPECIFY_MACHINE = 2;

    /**
     * 校验授权
     *
     * @param licenseEntity 授权实体
     * @return true 校验通过
     */
    boolean check(LicenseEntity licenseEntity);
}
