package org.gitlab4j.api.models;

import java.util.List;

import org.gitlab4j.api.utils.JacksonJson;

public class BoardGroup {

	private Integer id;
	private String name;
	private Group group;
	private Milestone milestone;
	private List<BoardList> lists;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Milestone getMilestone() {
		return milestone;
	}

	public void setMilestone(Milestone milestone) {
		this.milestone = milestone;
	}

	public List<BoardList> getLists() {
		return lists;
	}

	public void setLists(List<BoardList> lists) {
		this.lists = lists;
	}

	@Override
	public String toString() {
		return (JacksonJson.toJsonString(this));
	}
}
