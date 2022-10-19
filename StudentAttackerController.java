package edu.ufl.cise.cs1.controllers;
import game.controllers.AttackerController;
import game.models.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public final class StudentAttackerController implements AttackerController {

	public void init(Game game) {
	}

	public void shutdown(Game game) {
	}

	public int update(Game game, long timeDue) {

		int action = 0;
		Attacker gator = game.getAttacker();
		List<Defender> defenders = game.getDefenders();
		Defender closestDefender = (Defender) gator.getTargetActor(defenders, true);
		Node defenderLocation = closestDefender.getLocation();
		Node gatorLocation = gator.getLocation();
		int time = closestDefender.getVulnerableTime();
		List<Defender> vulnerableDef = new ArrayList<>();
		int defenderDistance = gatorLocation.getPathDistance(defenderLocation);

		//if there are any vulnerable defenders, attack them in order of proximity
		for (int i = 0; i < defenders.size(); i++)   {
			if (game.getDefender(i).isVulnerable())   {
				vulnerableDef.add(game.getDefender(i));
			}
		}
		//find the closest of the vulnerable defenders if there are any and attack it
		if (!vulnerableDef.isEmpty()) {
			Defender closestVulnerable = (Defender) gator.getTargetActor(vulnerableDef, true);
			Node vulnerableLocation = closestVulnerable.getLocation();
			action = gator.getNextDir(vulnerableLocation, true);

			//if there is a non-vulnerable defender within a distance of 5, flee
			if (!closestDefender.isVulnerable())   {
				if (defenderDistance <= 5) {
					action = gator.getNextDir(defenderLocation, false);
				}
			}
		}

		//if there are power pills available and no vulnerable defenders, get a power pill
		if (game.getPowerPillList().size() != 0 && vulnerableDef.isEmpty()) {
			List<Node> powerPillList = game.getPowerPillList();
			Node target = gator.getTargetNode(powerPillList, true);
			//if the distance to the power pill is less than or equal to 100, go after it
			gatorLocation = gator.getLocation();
			action = gator.getNextDir(target, true);
			/*when the gator gets next to the power pill, wait until a defender comes within a distance
			of 2, and then attack*/
			int powerPillDistance = gatorLocation.getPathDistance(target);
			if (powerPillDistance == 1 & defenderDistance > 2)   {
				action = gator.getReverse();
			}

			//if there is a non-vulnerable defender within a distance of 5, flee
			if (!closestDefender.isVulnerable())   {
				if (defenderDistance <= 5) {
					action = gator.getNextDir(defenderLocation, false);
				}
			}
		}

		//if there are no available power pills and no vulnerable defenders, go for the regular pills
		if (game.getPowerPillList().size() == 0 && vulnerableDef.isEmpty()) {
			List<Node> pillList = game.getPillList();
			Node target = gator.getTargetNode(pillList, true);
			action = gator.getNextDir(target, true);

			//if there is a non-vulnerable defender within a distance of 5, flee
			if (!closestDefender.isVulnerable())   {
				if (defenderDistance <= 5) {
					action = gator.getNextDir(defenderLocation, false);
				}
			}
		}
		return action;
	}
}