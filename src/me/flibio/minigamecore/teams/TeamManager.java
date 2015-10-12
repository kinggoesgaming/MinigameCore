package me.flibio.minigamecore.teams;

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
}
