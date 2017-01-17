package model;

import java.util.Random;

/**
 * Describes a Rock-Paper-Scissor match.
 * 
 * @author wyb
 *
 */
public class Match {
	
	public static final int NEW_ID = -1;
	private int id;
	private int	userInput;		// 0: rock; 1: paper; 2: scissor
	private int	computerInput;
	private int	result;			// 0: lose; 1: deuce; 2: win
	private int userId;
	
	public Match(int id, int userInput, int computerInput, int result, int userId) {
		this.id = id;
		this.userInput = userInput;
		this.computerInput = computerInput;
		this.result = result;
		this.userId = userId;
	}
	
	public Match(int aUserInput, int id) {
		this.id = id;
		userInput = aUserInput;
		Random generate = new Random();
		computerInput = generate.nextInt(3);
		result = genResult();
	}

	private int genResult() {
		if ((userInput+1) % 3 == computerInput) {
			return 0;
		}else if ((userInput + 2) % 3 == computerInput) {
			return 2;
		}else {
			return 1;
		}
	}
	
	public boolean isNewMatch() {
		return id == -1;
	}

	public int getUserId() {
		return userId;
	}
	
	
	public int getUserInput() {
		return userInput;
	}

	public int getComputerInput() {
		return computerInput;
	}

	public int getResult() {
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("user picked ");
		sb.append(getPickName(userInput));
		sb.append(", computer picked ");
		sb.append(getPickName(computerInput));
		sb.append(", user ");
		sb.append(getResultName(result));
		return sb.toString();
	}
	
	private String getPickName(int input) {
		if (input == 0) {
			return "Rock";
		} else if (input == 1) {
			return "Paper";
		} else if (input == 2) {
			return "Scissor";
		} else {
			return "Invalid Input";
		}
	}
	
	private String getResultName(int input) {
		if (input == 0) {
			return "Lose";
		} else if (input == 1) {
			return "Deuce";
		} else if (input == 2) {
			return "Win";
		} else {
			return "Invalid Input";
		}
	}

	public static void main(String[] args) {
	}
}
