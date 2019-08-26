package com.telusko.DemoHib;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="studentcse")
public class Alian {
	@Id
	private int id;
	private String name;
	private String gender;
	private String grade;
	private String term;
	//private String rank;
	
	private String semester;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		// TODO Auto-generated method stub
		this.grade = grade;
	}
	public String getSemester() {
		return semester;
	}
	public void setSemester(String semester) {
		this.semester = semester;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		// TODO Auto-generated method stub
		this.term = term;
	}
	
	
/*	@Override
	public String toString() {
		return "Alian [id=" + id + ", name=" + name + ", color=" + color + "]";
	}
	*/
	

}
