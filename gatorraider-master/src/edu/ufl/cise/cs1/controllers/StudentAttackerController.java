package edu.ufl.cise.cs1.controllers;

import game.controllers.AttackerController;
import game.models.Defender;
import game.models.Game;
import game.models.Node;

import java.util.List;

public final class StudentAttackerController implements AttackerController
{
	private Node bottomLeftPill;

	public void init(Game game)
	{
		//This method is called once when the game begins.
		//(For some controllers this method may be empty.)
		bottomLeftPill = game.getPowerPillList().get(3);
	}

	public void shutdown(Game game)
	{
		//Called once at the end of the game for cleanup.
		//(For some controllers this method may be empty.)
	}

	public int update(Game game, long timeDue)
	{
		if (game.getLevel() == 0)
		{
			return game.getAttacker().getNextDir(game.getAttacker().getTargetNode(game.getPillList(), true)
					, true);
		}

		else
			{
			//catches NullPointerException
			try {
				//finds nearest power pill
				List<Node> pills = game.getPowerPillList();
				Node target = game.getAttacker().getTargetNode(pills, true);
				;

				//calculates distance from each defender to the attacker
				int defender1distance = game.getAttacker().getLocation().getPathDistance(game.getDefender(0).getLocation());
				int defender2distance = game.getAttacker().getLocation().getPathDistance(game.getDefender(1).getLocation());
				int defender3distance = game.getAttacker().getLocation().getPathDistance(game.getDefender(2).getLocation());
				int defender4distance = game.getAttacker().getLocation().getPathDistance(game.getDefender(3).getLocation());

				//gator goes to bottom left pill
				if (game.checkPowerPill(bottomLeftPill) && game.getAttacker().getLocation().getPathDistance(bottomLeftPill) < 15) {
					//gator eats first power pill when defender leaves lair
					if ((defender1distance < 10 && defender1distance > -1) || (defender2distance < 10 && defender2distance > -1)
							|| (defender3distance < 10 && defender3distance > -1) || (defender4distance < 10 && defender4distance > -1)) {
						return game.getAttacker().getNextDir(bottomLeftPill, true);
					}

					return game.getAttacker().getReverse();
				}

				//gator goes towards nearest vulnerable defender
				//made so that if two out of the four defenders are vulnerable instead of just one
				else if (game.getDefender(0).isVulnerable() && game.getDefender(1).isVulnerable()
						|| game.getDefender(2).isVulnerable() && game.getDefender(3).isVulnerable()) {
					//Calculates closest defender
					int c = 0;
					int min = 10000;
					for (int i = 0; i < 4; i++) {
						//gator won't accidentally eat a power pill to get a vulnerable defender
						if ((game.getDefender(0).isVulnerable() || game.getDefender(1).isVulnerable()
								|| game.getDefender(2).isVulnerable() || game.getDefender(3).isVulnerable())
								&& (game.getAttacker().getLocation().getPathDistance(target) < 5)) {
							return game.getAttacker().getReverse();
						}

						//finds the closest vulnerable defender
						if ((game.getDefender(i).getLocation().getPathDistance(game.getAttacker().getLocation()) < min)
								&& game.getDefender(i).isVulnerable()) {
							c = i;
							min = game.getDefender(i).getLocation().getPathDistance(game.getAttacker().getLocation());
						}

					}

					return game.getAttacker().getNextDir(game.getDefender(c).getLocation(), true);

				}

				//If gator is close to a power pill and defenders aren't close the gator spins around
				else if ((game.getAttacker().getLocation().getPathDistance(target) < 5 && defender1distance > 10
						&& defender2distance > 10 && defender3distance > 10 && defender4distance > 10)) {
					return game.getAttacker().getReverse();
				}

				return game.getAttacker().getNextDir(target, true);

			}//end try

			//Catches NullPointerException
			catch (NullPointerException e) {
				//If defender vulnerable, get closest vulnerable defender
				if (game.getDefender(0).isVulnerable() || game.getDefender(1).isVulnerable()
						|| game.getDefender(2).isVulnerable() || game.getDefender(3).isVulnerable()) {
					int c = 0;
					int min = 10000;
					for (int i = 0; i < 4; i++)//loop through defenders
					{
						if ((game.getDefender(i).isVulnerable()
								&& game.getDefender(i).getLocation().getPathDistance(game.getAttacker().getLocation())
								< min)) {
							c = i;
							min = game.getDefender(c).getLocation().getPathDistance(game.getAttacker().getLocation());
						}
					}

					return game.getAttacker().getNextDir(game.getDefender(c).getLocation(), true);
				}//end catch

				//If power pills gone, get the closest pellet without changing direction
				if (game.getAttacker().getNextDir(game.getAttacker().getTargetNode(game.getPillList(), true), true)
						== game.getAttacker().getReverse()) {
					if (game.getAttacker().getLocation().isJunction()) {
						return game.getAttacker().getPossibleDirs(false).get(0);
					}

					return game.getAttacker().getDirection();
				}

				return game.getAttacker().getNextDir(game.getAttacker().getTargetNode(game.getPillList(), true), true);
			}

		}
	}

}