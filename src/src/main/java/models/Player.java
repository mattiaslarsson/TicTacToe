package models;

/**
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

}
