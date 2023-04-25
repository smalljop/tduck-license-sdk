/*
 * Copyright (c) 2020-2020, org.smartboot. All rights reserved.
 * project name: smart-license
 * file name: LicenseTest.java
 * Date: 2020-03-22
 * Author: sandao (zhengjunweimail@163.com)
 */

package org.tduckcloud.license.core;

import org.tduckcloud.license.core.check.LicenseCheck;
import org.tduckcloud.license.core.entity.LicenseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 三刀
 * @version V1.0 , 2020/3/21
 */
public class LicenseClientTest {
    public static void main(String[] args) throws IOException {
        InputStream inputStream = LicenseClientTest.class.getClassLoader().getResourceAsStream("license.td");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int size;
        byte[] buffer = new byte[1024];
        while ((size = inputStream.read(buffer)) > 0) {
            byteArrayOutputStream.write(buffer, 0, size);
        }
        inputStream.close();

        LicenseCheck license = new LicenseCheck();
        LicenseEntity licenseData = license.loadLicense(byteArrayOutputStream.toByteArray());
        System.out.println(new String(licenseData.getData()));
    }
}
