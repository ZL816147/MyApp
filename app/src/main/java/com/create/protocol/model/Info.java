package com.create.protocol.model;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by jsntnjzb on 2018/3/12.
 */

public class Info extends DataSupport {

    private boolean isShow = false; // 是否显示CheckBox
    private boolean isChecked = false; // 是否选中CheckBox
    @Column(unique = true, defaultValue = "unknown")
    private String protocolBitmap;

    public String getProtocolBitmap() {
        return protocolBitmap;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int id = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String site = "";    //地点
    private String date = "";    //制单日
    private String status = "";  //赔付情况
    private String power = "";   //供电所
    private String construction = ""; //施工方
    private String projectCode = ""; //项目文号
    private String projectName = ""; //项目名称
    private String describe = ""; //描述

    private String identityCardImage = ""; //身份证照片
    private String bankCardImage = ""; //银行卡
    private String sceneImage1 = ""; //现场照片1
    private String sceneImage2 = ""; //现场照片2
    private String sceneImage3 = ""; //现场照片3
    private String sceneImage4 = ""; //现场照片4

    private String involvedPeople = ""; //涉赔人签字
    private String responsiblePeople = ""; //经办人签字

    private String openBank = ""; //开户行
    private String bankCard = ""; //银行卡号
    private String contactNumber = ""; //联系电话
    private String marketingNo = ""; //营销户号
    private String powerPeople = ""; //供电所负责人

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getConstruction() {
        return construction;
    }

    public void setConstruction(String construction) {
        this.construction = construction;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String name) {
        this.projectName = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getIdentityCardImage() {
        return identityCardImage;
    }

    public void setIdentityCardImage(String identityCardImage) {
        this.identityCardImage = identityCardImage;
    }

    public String getBankCardImage() {
        return bankCardImage;
    }

    public void setBankCardImage(String bankCardImage) {
        this.bankCardImage = bankCardImage;
    }

    public String getSceneImage1() {
        return sceneImage1;
    }

    public void setSceneImage1(String sceneImage1) {
        this.sceneImage1 = sceneImage1;
    }

    public String getSceneImage2() {
        return sceneImage2;
    }

    public void setSceneImage2(String sceneImage2) {
        this.sceneImage2 = sceneImage2;
    }

    public String getSceneImage3() {
        return sceneImage3;
    }

    public void setSceneImage3(String sceneImage3) {
        this.sceneImage3 = sceneImage3;
    }

    public String getSceneImage4() {
        return sceneImage4;
    }

    public void setSceneImage4(String sceneImage4) {
        this.sceneImage4 = sceneImage4;
    }

    public String getInvolvedPeople() {
        return involvedPeople;
    }

    public void setInvolvedPeople(String involvedPeople) {
        this.involvedPeople = involvedPeople;
    }

    public String getResponsiblePeople() {
        return responsiblePeople;
    }

    public void setResponsiblePeople(String responsiblePeople) {
        this.responsiblePeople = responsiblePeople;
    }

    public String getOpenBank() {
        return openBank;
    }

    public void setOpenBank(String openBank) {
        this.openBank = openBank;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getMarketingNo() {
        return marketingNo;
    }

    public void setMarketingNo(String marketingNo) {
        this.marketingNo = marketingNo;
    }

    public String getPowerPeople() {
        return powerPeople;
    }

    public void setPowerPeople(String powerPeople) {
        this.powerPeople = powerPeople;
    }

    public void setProtocolBitmap(String protocolBitmap) {
        this.protocolBitmap = protocolBitmap;
    }
}
