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

	// uid:用户ID，alias：用户昵称，facePath：头像保存路径
	private String uid, alias, facePath;
	// regTime：注册日期，birthDay：生日
	private Date regTime, birthDay;
	// 性别 男:true 女:false
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
