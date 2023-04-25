package org.tduckcloud.license.core.entity;

import java.io.Serializable;

/**
 * @author tduck
 * @version V1.0 , 2020/3/26
 */
public final class LicenseEntity implements Serializable {

    /**
     * 魔数
     */
    public static final byte[] MAGIC_NUM = "tduck-license".getBytes();
    /**
     * 公钥
     */
    private byte[] publicKeys;
    /**
     * 申请时间
     */
    private final long applyTime = System.currentTimeMillis();

    /**
     * 过期时间
     */
    private long expireTime;

    /**
     * 申请方
     */
    private String applicant;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 原文
     */
    private transient byte[] data;

    public LicenseEntity(long expireTime, byte[] publicKeys) {
        this.expireTime = expireTime;
        this.publicKeys = publicKeys;
    }

    public LicenseEntity() {
    }


    public LicenseEntity setExpireTime(long expireTime) {
        this.expireTime = expireTime;
        return this;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public byte[] getPublicKeys() {
        return publicKeys;
    }

    public long getApplyTime() {
        return applyTime;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
