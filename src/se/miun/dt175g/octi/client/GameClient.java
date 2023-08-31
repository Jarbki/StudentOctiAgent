package se.miun.dt175g.octi.client;

import java.io.*;
import java.net.*;

import se.miun.dt175g.octi.core.OctiAction;
import se.miun.dt175g.octi.core.OctiJsonAdapter;
import se.miun.dt175g.octi.core.OctiState;
import se.miun.dt175g.octi.core.Player;
import se.miun.dt175g.octi.core.communicator.PlayerSetup;


// You shouldn't change this class
public class GameClient {
	private Socket clientSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private StudentAgent studentAgent;
	private PlayerSetup setup;

	public GameClient(PlayerSetup setup, StudentAgent studentAgent) {
		this.setup = setup;
		this.studentAgent = studentAgent;
	}

	public void startConnection() {
		try {
			clientSocket = new Socket(setup.serverIpAddress, setup.gamePort);
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendPlayerSetup(PlayerSetup setup) {
		try {
			out.writeObject(setup);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String receiveStateAsString() {
		try {
			String jsonState = (String) in.readObject();
			return jsonState;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void receiveAndSetPlayerInfo() {
		try {
			Player player = (Player) in.readObject();
			Player oppPlayer = (Player) in.readObject();
			long timeLimit = (Long) in.readObject();

			studentAgent.setPlayersAndTimeLimit(player, oppPlayer, timeLimit);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void sendJsonAction(String action) {
		try {
			out.writeObject(action);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopConnection() {
		try {
			in.close();
			out.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void play() {
		this.startConnection();
		this.sendPlayerSetup(setup);
		this.receiveAndSetPlayerInfo();
		while (true) {
			String stateAsJson = this.receiveStateAsString();
			OctiState state = OctiState.createStateFromJson(stateAsJson);

			OctiAction action = studentAgent.getNextMove(state);

			String actionJson = OctiJsonAdapter.octiActionToJson(action);
			this.sendJsonAction(actionJson);
		}
	}

}
