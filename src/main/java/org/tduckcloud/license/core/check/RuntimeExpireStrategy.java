package org.tduckcloud.license.core.check;

import org.tduckcloud.license.core.entity.LicenseEntity;

/**
 * 定时任务校验过期策略
 *
 * @author : tduck
 * @since :  2023/04/14 15:43
 **/
public interface RuntimeExpireStrategy {

    /**
     * 过期出现的异常
     *
     * @param licenseEntity 授权实体
     */
    void expire(LicenseEntity licenseEntity);
}
