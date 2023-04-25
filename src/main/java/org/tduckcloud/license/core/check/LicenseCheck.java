/*
 * Copyright (c) 2020-2020, org.smartboot. All rights reserved.
 * project name: smart-license
 * file name: License.java
 * Date: 2020-03-22
 * Author: sandao (zhengjunweimail@163.com)
 */

package org.tduckcloud.license.core.check;

import org.tduckcloud.license.core.LicenseException;
import org.tduckcloud.license.core.entity.LicenseEntity;
import org.tduckcloud.license.core.util.Md5;

import javax.crypto.Cipher;
import java.io.*;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 读取License并解析其内容
 *
 * @author 三刀
 * @version V1.0 , 2020/3/20
 */
public class LicenseCheck {


    /**
     * 检查
     */
    public static final LicenseCheckStrategy defaultLicenseCheckStrategy = new DefaultLicenseCheckStrategy();

    /**
     * 异常
     */
    public static final RuntimeExpireStrategy defaultExpireStrategy = entity -> {
        throw new LicenseException("license is expired");
    };

    private static final String KEY_ALGORITHM = "RSA";
    private final byte[] readBuffer = new byte[8];

    private MonitorLicenseExpire monitorLicenseExpire;

    public LicenseCheck() {
        this(defaultLicenseCheckStrategy, defaultExpireStrategy, TimeUnit.HOURS.toMillis(1), true);
    }

    public LicenseCheck(LicenseCheckStrategy checkStrategy,
                        RuntimeExpireStrategy expireStrategy,
                        long period, boolean openMonitor) {
        if (period < TimeUnit.SECONDS.toMillis(1)) {
            throw new IllegalArgumentException("period is too fast");
        }
        /**
         * 过期策略
         */
        if (openMonitor) {
            this.monitorLicenseExpire = new MonitorLicenseExpire(checkStrategy, expireStrategy, period);
        }
    }

    /**
     * 使用公钥进行解密
     *
     * @param data      待解密数据
     * @param publicKey 公钥
     * @return byte[] 解密数据
     */
    private byte[] decryptByPublicKey(byte[] data, byte[] publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(encodedKeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, pubKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new LicenseException("decrypt exception", e);
        }
    }


    /**
     * 检查License是否有效
     *
     * @return true:有效，false:无效
     */
    public Boolean checkLicense() {
        return false;
    }


    public LicenseEntity loadLicense(byte[] bytes) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        // q: 为什么要读取这个魔数？
        // a: 防止用户随意修改License文件，导致License文件被篡改
        byte[] magicBytes = new byte[LicenseEntity.MAGIC_NUM.length];
        inputStream.read(magicBytes);
        checkBytes(magicBytes, LicenseEntity.MAGIC_NUM);


        // 加密版本 1 暂时不做处理
        byte[] versionBytes = new byte[readInt(inputStream)];
        // 申请时间
        // q: 为什么这样就可以读取到申请时间？
        // a: 因为在加密的时候，是先将数据分段加密，然后将每段加密后的数据长度和数据一起写入到流中，所以在解密的时候，只需要按照加密的顺序，先读取长度，再读取数据，就可以得到原始数据
        // inputstream 被读取过后，指针会移动到下一个位置，所以这里读取完申请时间后，指针会移动到过期时间的位置
        long applyTime = readLong(inputStream);
        if (applyTime > System.currentTimeMillis()) {
            throw new LicenseException("invalid license");
        }
        //过期时间
        long expireTime = readLong(inputStream);

        if (expireTime < System.currentTimeMillis()) {
            throw new LicenseException("license expire");
        }

        //md5
        byte[] md5 = new byte[readInt(inputStream)];
        inputStream.read(md5);

        //公钥
        byte[] publicKey = new byte[readInt(inputStream)];
        inputStream.read(publicKey);

        //申请者
        byte[] applicant = new byte[readInt(inputStream)];
        inputStream.read(applicant);

        //联系方式
        byte[] contact = new byte[readInt(inputStream)];
        inputStream.read(contact);

        // q: 为什么要这样读取？
        // a: 因为在加密的时候，是先将数据分段加密，然后将每段加密后的数据长度和数据一起写入到流中，所以在解密的时候，需要先读取每段数据的长度，然后再读取数据
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int size;
//        // q: inputStream.read() 返回的是什么？
//        // a: 返回的是读取到的字节的长度，如果返回-1，表示已经读取到了文件末尾
        while ((size = inputStream.read()) > 0) {
            byte[] part = new byte[size];
            // q: part 会被填充满吗？
            // a: 不会，因为在写入的时候，是先写入了数据的长度，然后再写入数据，所以在读取的时候，先读取了数据的长度，然后再读取数据，所以读取的数据的长度一定是和写入的数据的长度一致的
            inputStream.read(part);
            byte[] decodeData = decryptByPublicKey(part, publicKey);
            byteArrayOutputStream.write(decodeData);
            if (readLong(inputStream) != expireTime % part.length) {
                throw new LicenseException("invalid license");
            }

        }
        // q：解密为什么报错
        // a: 因为在加密的时候，是先将数据分段加密，然后将每段加密后的数据长度和数据一起写入到流中，所以在解密的时候，需要先读取每段数据的长度，然后再读取数据
        System.out.println("data" + Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
//        byte[] data = decryptByPublicKey(decodeData, publicKey);
        byte[] data = byteArrayOutputStream.toByteArray();
        if (!Objects.equals(Md5.md5(byteArrayOutputStream.toByteArray()), new String(md5))) {
            throw new LicenseException("invalid license");
        }
        LicenseEntity entity = new LicenseEntity(expireTime, publicKey);
        entity.setApplicant(new String(applicant));
        entity.setContact(new String(contact));
        entity.setData(data);
        monitorLicenseExpire.startMonitor(entity);
        return entity;
    }

    private void checkBytes(byte[] b1, byte[] b2) {
        if (b1.length != b2.length) {
            throw new LicenseException("invalid license");
        }
        for (int i = 0; i < b1.length; i++) {
            if (b1[i] != b2[i]) {
                throw new LicenseException("invalid license");
            }
        }
    }


    private long readLong(InputStream inputStream) throws IOException {
        inputStream.read(readBuffer, 0, 8);
        // q: 为什么要这样读取？
        // a: 因为java的long是8个字节，而InputStream的read方法是读取一个字节，所以需要8次读取
        // q: 为啥是56，48，40，32，24，16，8，0？
        // a: 因为long是8个字节，每个字节8位，所以每次读取一个字节，需要左移8位，然后再与之前的数据进行或运算
        return (((long) readBuffer[0] << 56) +
                ((long) (readBuffer[1] & 255) << 48) +
                ((long) (readBuffer[2] & 255) << 40) +
                ((long) (readBuffer[3] & 255) << 32) +
                ((long) (readBuffer[4] & 255) << 24) +
                ((readBuffer[5] & 255) << 16) +
                ((readBuffer[6] & 255) << 8) +
                ((readBuffer[7] & 255) << 0));
    }


    /**
     * q: 这个方法是干嘛的？
     * a: 读取int
     * q: 为什么要这么读取？
     * a: 因为java的int是4个字节，而InputStream的read方法是读取一个字节，所以需要4次读取
     *
     * @param in
     * @return
     * @throws IOException
     */
    private int readInt(InputStream in) throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        // q: 为什么要这样读取？
        // a: 因为java的int是4个字节，而InputStream的read方法是读取一个字节，所以需要4次读取
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        // q: 位移的目的是什么？
        // a: 位移的目的是将每个字节转换为int类型
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

}
