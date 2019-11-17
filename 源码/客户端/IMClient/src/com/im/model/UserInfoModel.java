package com.im.model;

import java.util.Date;

public class UserInfoModel {

	public UserInfoModel(String uid, String alias, String face, Date regtime,
			Date birthday, boolean sex) {
		this.uid = uid;
		this.alias = alias;
		this.facePath = face;
		this.regTime = regtime;
		this.birthDay = birthday;
		this.sex = sex;
	}

	// uid:�û�ID��alias���û��ǳƣ�facePath��ͷ�񱣴�·��
	private String uid, alias, facePath;
	// regTime��ע�����ڣ�birthDay������
	private Date regTime, birthDay;
	// �Ա� ��:true Ů:false
	private boolean sex;

	public String getAlias() {
		return alias;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public String getFacePath() {
		return facePath;
	}

	public Date getRegTime() {
		return regTime;
	}

	public String getUid() {
		return uid;
	}

	public boolean getSex() {
		return sex;
	}
}
