package org.tduckcloud.license.core.check;

import org.tduckcloud.license.core.entity.LicenseEntity;

/**
 * 监控过期类
 *
 * @author : tduck
 * @since :  2023/04/14 15:39
 **/
public class MonitorLicenseExpire {

    private final LicenseCheckStrategy licenseCheckStrategy;
    private final RuntimeExpireStrategy runtimeExpireStrategy;

    // 一分钟执行一次
    private long period = 1000 * 60;

    public MonitorLicenseExpire(LicenseCheckStrategy licenseCheckStrategy,
                                RuntimeExpireStrategy runtimeExpireStrategy, long period) {
        this.licenseCheckStrategy = licenseCheckStrategy;
        this.runtimeExpireStrategy = runtimeExpireStrategy;
        if (period > 0) {
            this.period = period;
        }

    }


    /**
     * 启动License过期监控
     */
    public void startMonitor(final LicenseEntity licenseEntity) {
        Thread thread = new Thread(() -> {
            while (true) {
                boolean check = licenseCheckStrategy.check(licenseEntity);
                if (!check) {
                    runtimeExpireStrategy.expire(licenseEntity);
                }
                try {
                    Thread.sleep(period);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "licenseMonitor");
        // q: 为什么要设置为守护线程？
        // a: 因为如果主线程结束了，那么守护线程也就没有存在的必要了
        thread.setDaemon(true);
        thread.start();
    }
}
