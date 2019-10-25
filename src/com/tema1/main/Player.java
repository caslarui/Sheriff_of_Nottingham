package com.tema1.main;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Player {
    private String strategy;
    private List<Integer> PlayerCards;
    private  boolean isSheriff;

    public Player() {
        strategy = null;
        PlayerCards = null;
        isSheriff = false;
    }

    public Player(String strategy, List<Integer> playerCards) {
        this.strategy = strategy;
        PlayerCards = playerCards;
        isSheriff = false;
    }

    public Player(String strategy) {
        this.strategy = strategy;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public List<Integer> getPlayerCards() {
        return PlayerCards;
    }

    public void setPlayerCards(List<Integer> AllCards) {
        this.PlayerCards.addAll(AllCards.subList(0,10));
        AllCards.subList(0,10).clear();
        System.out.println(this.PlayerCards);
    }

    public final void  setSheriff(boolean key) {
        this.isSheriff = key;
    }

    public final boolean getSheriff() {
        return this.isSheriff;
    }


    @Override
    public String toString() {
//        String cards = PlayerCards.toString();
//        System.out.println(PlayerCards);
        return "" + strategy + "\t:";

    }
}