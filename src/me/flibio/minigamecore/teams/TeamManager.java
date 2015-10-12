package me.flibio.minigamecore.teams;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class TeamManager {
	
	private CopyOnWriteArrayList<Team> teams = new CopyOnWriteArrayList<Team>();
	
	public TeamManager() {
		
	}
	
	/**
	 * Gets a team from the list of teams
	 * @param teamName
	 * 	The name of the team to get
	 * @return
	 * 	The team
	 */
	public Optional<Team> getTeam(String teamName) {
		for(Team team : teams) {
			if(team.getName().equalsIgnoreCase(teamName)) {
				return Optional.of(team);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * Checks if a team exists
	 * @param teamName
	 * 	The name of the team to check for
	 * @return
	 * 	If the team exists or not
	 */
	public boolean teamExists(String teamName) {
		for(Team team : teams) {
			if(team.getName().equalsIgnoreCase(teamName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds a team to the team list
	 * @param team
	 * 	The team to be added
	 * @return
	 * 	If the method was successful or not
	 */
	public boolean addTeam(Team team) {
		if(teamExists(team.getName())) {
			return false;
		} else {
			teams.add(team);
			return true;
		}
	}
	
	/**
	 * Removes a team from the list of teams
	 * @param teamName
	 * 	The name of the team to remove
	 * @return
	 * 	If the method was successful or not
	 */
	public boolean removeTeam(String teamName) {
		for(Team team : teams) {
			if(team.getName().equalsIgnoreCase(teamName)) {
				teams.remove(team);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds a new basic team
	 * @param name
	 * 	The name of the team to add
	 * @return
	 * 	If the method was successful or not
	 */
	public boolean addBasicTeam(String name) {
		if(teamExists(name)) {
			return false;
		} else {
			teams.add(new Team(name));
			return true;
		}
	}
	
	/**
	 * Checks if a player is on a team
	 * @param playerName
	 * 	The name of the player
	 * @return
	 * 	If the player is on a team
	 */
	public boolean isPlayerOnTeam(String playerName) {
		for(Team team : teams) {
			if(team.playerExists(playerName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets all of the teams a player has joined
	 * @param playerName
	 * 	The name of the player to get the teams of
	 * @return
	 * 	A list of the teams the player has joined
	 */
	public List<Team> getJoinedTeams(String playerName) {
		ArrayList<Team> joinedTeams = new ArrayList<Team>();
		for(Team team : teams) {
			if(team.playerExists(playerName)) {
				joinedTeams.add(team);
			}
		}
		return joinedTeams;
	}
	
	/**
	 * Removes a player from all teams they are on
	 * @param playerName
	 * 	The name of the player to remove from all teams
	 */
	public void removeFromAllTeams(String playerName) {
		for(Team team : teams) {
			team.removePlayer(playerName);
		}
	}
}
