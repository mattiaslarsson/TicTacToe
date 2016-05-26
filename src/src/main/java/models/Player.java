package models;

/**
 * Handles player connections.
 *
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-21.
 */
public class Player {

	private String firstName, surName;
	private long id;
	private int rank;

	public Player() {
	}

	public Player(String firstName, String surName, long id) {
		this.firstName = firstName;
		this.surName = surName;
		this.id = id;
	}

	public Player(String firstName, String surName, int rank) {
		this.firstName = firstName;
		this.surName = surName;
		this.rank = rank;
	}

	public Player(String firstName, String surName, long id, int rank) {
		this.firstName = firstName;
		this.surName = surName;
		this.id = id;
		this.rank = rank;
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

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
}
