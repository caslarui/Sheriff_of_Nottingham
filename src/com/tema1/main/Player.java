package com.tema1.main;

import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;

import java.util.*;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.logging.Level;


public class Player {
    private String strategy;
    private boolean isSheriff;
    private Card bag = new Card();
    private Card illegalCards = new Card();
    private Card legalCards = new Card();
    private int pocket;
    private int assetType;
    private int playerId;

    public Player() {
        strategy = null;
        isSheriff = false;
        pocket = 80;
    }

    public Player(String strategy, int id) {
        this.strategy = strategy;
        this.isSheriff = false;
        this.pocket = 80;
        this.playerId = id;
    }

    public int getPocket() {
        return this.pocket;
    }

    public Card getBag() {
        return this.bag;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public String getStrategy() {
        return this.strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public boolean isSheriff() {
        return isSheriff;
    }

    public final boolean getSheriff() {
        return this.isSheriff;
    }

    public void setSheriff(boolean sheriff) {
        isSheriff = sheriff;
    }

    public void setPlayerCards(List<Integer> gameCards) {
        for (int card : gameCards) {
            if(this.illegalCards.size() + this.legalCards.size() == 10)
                break;
            if(GoodsFactory.getInstance().getGoodsById(card).getType().equals(GoodsType.Illegal))
                this.illegalCards.getCards().put(card, Collections.frequency(gameCards.subList(0, 10), card));
            else
                this.legalCards.getCards().put(card, Collections.frequency(gameCards.subList(0, 10), card));

        }

        gameCards.subList(0, 10).clear();
    }

    public boolean isLying(){
        for (Map.Entry<Integer, Integer> card : this.bag.getCards().entrySet()){
            if(card.getKey() == assetType && bag.countIllegals() == 0)
                return false;
        }
        return true;
    }

    public void addToPocket(int coins){
        pocket += coins;
    }

    public List<Integer> confiscCards() {
        List<Integer> confiscatedCards = new ArrayList<Integer>();
        for (Map.Entry<Integer, Integer> card : this.bag.getCards().entrySet()){
            if(card.getKey() != assetType) {
                for(int i = 0; i < card.getValue(); i++){
                    confiscatedCards.add(card.getKey());
                }
            }
        }
        for(int i = 0; i < confiscatedCards.size(); i++) {
            if(bag.getCards().containsKey(confiscatedCards.get(i)))
                bag.getCards().remove(confiscatedCards.get(i));
        }
        return confiscatedCards;
    }

    public void setBasicBag() {
        if(this.legalCards.size() == 0) {
            if (this.illegalCards.size() > 0) {
                Map<Integer, Integer> card = illegalCards.getMostProfit();
//                System.out.println(card);
                bag.getCards().put((Integer)card.keySet().toArray()[0], 1);
                assetType = 0;
            }
        }
        if(this.legalCards.size() > 0) {
            legalCards.mostFrequent();
            bag.getCards().putAll(legalCards.getMostProfit());
            assetType = (Integer)bag.getCards().keySet().toArray()[0];
        }
    }

    void setBribedBag() {
        int penalty = 0;
        Map<Integer, Integer> card;

        if (this.pocket < 5 || this.illegalCards.size() == 0) {
            setBasicBag();
            return;
        }
        if (this.illegalCards.size() > 0) {
            while(this.bag.size() <= 8 && penalty <= this.pocket) {
                if(this.illegalCards.size() == 0)
                    break;
                if(this.bag.size() == 8)
                    break;
                card = this.illegalCards.getMostProfit();
                int key = (Integer) card.keySet().toArray()[0];
                int value = (Integer)card.values().toArray()[0];
                int count = 0;
                while(count <= value) {
//                System.out.println(bag.size());
                    if(bag.size() == 8)
                        break;
                    if(count == value) {
                        this.illegalCards.getCards().remove(key, value-count);
                        break;
                    }
                    penalty += GoodsFactory.getInstance().getGoodsById(key).getPenalty();
                    if(penalty >= this.pocket)
                        break;
                    if(this.bag.getCards().containsKey(key))
                        this.bag.getCards().replace(key, count + 1);
                    else
                        this.bag.getCards().put(key,1);
                    this.illegalCards.getCards().replace(key, value - count, value - count- 1);
                    count++;
                }
//                System.out.println(this.bag.size());
            }
        }
        if (this.legalCards.size() > 0) {
            while(this.bag.size() <= 8 &&  penalty <= this.pocket) {
                if(this.bag.size() == 8)
                    break;
                if(this.legalCards.size() == 0)
                    break;
                card = this.legalCards.getMostProfit();
                int key = (Integer) card.keySet().toArray()[0];
                int value = (Integer)card.values().toArray()[0];
                int count = 0;
                while(count <= value) {
                    if(bag.size() == 8)
                        break;
                    if(count == value) {
                        this.legalCards.getCards().remove(key, value-count);
                        break;
                    }
                    penalty += GoodsFactory.getInstance().getGoodsById(key).getPenalty();
                    if(penalty >= this.pocket)
                        break;
                    if(this.bag.getCards().containsKey(key))
                        this.bag.getCards().replace(key, count + 1);
                    else
                        this.bag.getCards().put(key,1);
                    this.legalCards.getCards().replace(key, value - count, value - count- 1);
                    count++;
                }
            }
        }
        this.assetType = 0;
//        System.out.println("Pocket : "+ this.pocket + "\t\tPenalty : " + penalty + "\n");
    }

    public void clearBag() {
        this.illegalCards.getCards().clear();
        this.legalCards.getCards().clear();
        this.bag.getCards().clear();
    }

    public void setBag(int round) {
        System.out.println(this.toString());
        if (strategy.equals("basic")) {
            setBasicBag();
            return;
        }
        if (strategy.equals("greedy")) {
            setBasicBag();
            if(round % 2 == 0) {
                if(bag.size() < 8 && this.illegalCards.size() > 0) {
                    bag.addIllegal(this.illegalCards);
                }
                return ;
            }
            return;
        }
        if (strategy.equals("bribed")) {
            setBribedBag();
        }
    }

    public int getPlayerPenalty(){
        int penalty = 0;
        if(isLying()){
            for (Map.Entry<Integer, Integer> card : this.bag.getCards().entrySet()) {
                if (card.getKey() != assetType)
                    penalty += card.getValue()
                            * GoodsFactory.getInstance().getGoodsById(card.getKey()).getPenalty();
            }

        }
        if(!isLying()){
            for (Map.Entry<Integer, Integer> card : this.bag.getCards().entrySet()) {
                penalty += card.getValue()
                        * GoodsFactory.getInstance().getGoodsById(card.getKey()).getPenalty();

            }
        }

        return penalty;
    }

    public int getCountById(int cardId) {
        for (Map.Entry<Integer, Integer> card : this.bag.getCards().entrySet()){
            if(card.getKey() == cardId)
                return card.getValue();
        }
        return 0;
    }

//    public void compute
    @Override
    public String toString() {
        return "\t" + strategy +" ["+ this.playerId + "]\t: " + this.pocket + " $$$" + "\n\tLegal:\t\t"+this.legalCards.getCards().toString() +
                "\n\tIllegals:\t"+this.illegalCards.getCards().toString() +
                "\n\tBag:\t\t" + this.bag.getCards().toString()+ "\n";

    }
}
