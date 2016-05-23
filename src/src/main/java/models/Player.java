package models;

/**
 * Handles player connections.
 *
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-21.
 */
public class Player {

	private String firstName, surName;
	private long id;

	public Player(String firstName, String surName, long id) {
		this.firstName = firstName;
		this.surName = surName;
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSurName() {
		return surName;
	}

	public void setSurName(String surName) {
		this.surName = surName;
	}

	public long getId() {
		return id;
	}

}
