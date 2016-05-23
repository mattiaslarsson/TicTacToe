package models;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johan Lindström (jolindse@hotmail.com) on 2016-05-21.
 */
public class Message<T> {

	private String command;
	private List<String> commandData;

	public Message(String command) {
		this.command = command;
		commandData = new ArrayList<>();
	}

	public Message(String command, T cmdData) {
		this.command = command;
		commandData = new ArrayList<>();
		addCommandData(cmdData);
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public List<String> getCommandData() {
		return commandData;
	}

	public void setCommandData(List<String> commandData) {
		this.commandData = commandData;
	}

	public void addCommandData(T currData){
		Gson gson = new Gson();
		String cmdData = gson.toJson(currData);
		commandData.add(cmdData);
	}
}
