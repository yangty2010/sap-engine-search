package com.neusoft.saca.snap.file.domain.directory.vo;

import java.io.Serializable;

public class DirectoryPathVo implements Serializable {

	private static final long serialVersionUID = 7300692731897500051L;
	private String id;
	private String name;

	public DirectoryPathVo() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
