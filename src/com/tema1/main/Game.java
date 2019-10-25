package com.tema1.main;

import java.util.ArrayList;
import java.util.List;

public final class Game {
    private int rounds;
    private List<Player> players = new ArrayList<Player>();
    private List<Integer> AllCards;

    public Game() {
        rounds = -1;
        players = null;
        AllCards = null;
    }

    public Game(int rounds, List<String> Strategy, List<Integer> cards) {
        this.rounds = rounds;
        this.AllCards = cards;
        for (int i = 0; i < Strategy.size(); i++) {
            players.add( new Player(Strategy.get(i)) );
        }
    }

    public int getRounds() {
        return rounds;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Integer> getAllCards() {
        return AllCards;
    }

    public final void startGame() {
        for (int round = 0; round < this.rounds; ++round) {
            for (int subRound = 0; subRound < this.players.size(); ++subRound) {
                this.players.get(subRound).setSheriff(true);
                for (Player player : this.players) {
                    if (player.getSheriff())
                        continue;
                    player.setPlayerCards(this.AllCards);
                }
                System.out.println("Runda : " + round +  " | Subrunda : "+subRound+ "\n " + this.getPlayers().toString());
                this.players.get(subRound).setSheriff(false);
            }
        }
    }
}
