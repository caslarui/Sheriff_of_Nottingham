package com.tema1.main;

import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;
import static com.tema1.main.MagicNumbers.*;

import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Map;



public final class Player {
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
    pocket = DEFAULT_POCKET;
  }

  public Player(final String strategy, final int id) {
    this.strategy = strategy;
    this.isSheriff = false;
    this.pocket = DEFAULT_POCKET;
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

  public boolean isSheriff() {
    return isSheriff;
  }

  public boolean getSheriff() {
    return this.isSheriff;
  }

  public void setSheriff(final boolean sheriff) {
    isSheriff = sheriff;
  }

  public void setPlayerCards(final List<Integer> gameCards) {
    for (int card : gameCards) {
      if (this.illegalCards.size() + this.legalCards.size() == BIG_BRIBERY) {
        break;
      }
      if (GoodsFactory.getInstance().getGoodsById(card).getType().equals(GoodsType.Illegal)) {
        this.illegalCards.getCards().put(card, Collections.frequency(gameCards
                                                          .subList(0, BIG_BRIBERY), card));
      } else {
        this.legalCards.getCards().put(card, Collections.frequency(gameCards
                                                        .subList(0, BIG_BRIBERY), card));
      }

    }

    gameCards.subList(0, BIG_BRIBERY).clear();
  }

  public boolean isLying() {
    for (Map.Entry<Integer, Integer> card : this.bag.getCards().entrySet()) {
      if (card.getKey() == assetType && bag.countIllegals() == 0) {
        return false;
      }
    }
    return true;
  }

  public void addToPocket(final int coins) {
    pocket += coins;
  }

  public List<Integer> confiscCards() {
    List<Integer> confiscatedCards = new ArrayList<Integer>();
    for (Map.Entry<Integer, Integer> card : this.bag.getCards().entrySet()) {
      if (card.getKey() != assetType) {
        for (int i = 0; i < card.getValue(); i++) {
          confiscatedCards.add(card.getKey());
        }
      }
    }
    for (int i = 0; i < confiscatedCards.size(); i++) {
      if (bag.getCards().containsKey(confiscatedCards.get(i))) {
        bag.getCards().remove(confiscatedCards.get(i));
      }
    }
    return confiscatedCards;
  }

  public void setBasicBag() {
    if (this.legalCards.size() == 0) {
      if (this.illegalCards.size() > 0) {
        Map<Integer, Integer> card = illegalCards.getMostProfit();
//                System.out.println(card);
        bag.getCards().put((Integer) card.keySet().toArray()[0], 1);
        assetType = 0;
      }
    }
    if (this.legalCards.size() > 0) {
      legalCards.mostFrequent();
      bag.getCards().putAll(legalCards.getMostProfit());
      assetType = (Integer) bag.getCards().keySet().toArray()[0];
    }
  }

  void setBribedBag() {
    int penalty = 0;
    Map<Integer, Integer> card;

    if (this.pocket < SMALL_BRIBERY || this.illegalCards.size() == 0) {
      setBasicBag();
      return;
    }
    if (this.illegalCards.size() > 0) {
      while (this.bag.size() <= MAX_BAG_SIZE && penalty <= this.pocket) {
        if (this.illegalCards.size() == 0) {
          break;
        }
        if (this.bag.size() == MAX_BAG_SIZE) {
          break;
        }
        card = this.illegalCards.getMostProfit();
        int key = (Integer) card.keySet().toArray()[0];
        int value = (Integer) card.values().toArray()[0];
        int count = 0;
        while (count <= value) {
          if (bag.size() == MAX_BAG_SIZE) {
            break;
          }
          if (count == value) {
            this.illegalCards.getCards().remove(key, value - count);
            break;
          }
          penalty += GoodsFactory.getInstance().getGoodsById(key).getPenalty();
          if (penalty >= this.pocket) {
            break;
          }
          if (this.bag.getCards().containsKey(key)) {
            this.bag.getCards().replace(key, count + 1);
          } else {
            this.bag.getCards().put(key, 1);
          }
          this.illegalCards.getCards().replace(key, value - count,
                                              value - count - 1);
          count++;
        }
      }
    }
    if (this.legalCards.size() > 0) {
      while (this.bag.size() <= MAX_BAG_SIZE && penalty <= this.pocket) {
        if (this.bag.size() == MAX_BAG_SIZE) {
          break;
        }
        if (this.legalCards.size() == 0) {
          break;
        }
        card = this.legalCards.getMostProfit();
        int key = (Integer) card.keySet().toArray()[0];
        int value = (Integer) card.values().toArray()[0];
        int count = 0;
        while (count <= value) {
          if (bag.size() == MAX_BAG_SIZE) {
            break;
          }
          if (count == value) {
            this.legalCards.getCards().remove(key, value - count);
            break;
          }
          penalty += GoodsFactory.getInstance().getGoodsById(key).getPenalty();
          if (penalty >= this.pocket) {
            break;
          }
          if (this.bag.getCards().containsKey(key)) {
            this.bag.getCards().replace(key, count + 1);
          } else {
            this.bag.getCards().put(key, 1);
          }
          this.legalCards.getCards().replace(key, value - count,
                                          value - count - 1);
          count++;
        }
      }
    }
    this.assetType = 0;
  }

  public void clearBag() {
    this.illegalCards.getCards().clear();
    this.legalCards.getCards().clear();
    this.bag.getCards().clear();
  }

  public void setBag(final int round) {
    if (strategy.equals("basic")) {
      setBasicBag();
      return;
    }
    if (strategy.equals("greedy")) {
      setBasicBag();
      if (round % 2 == 0) {
        if (bag.size() < MAX_BAG_SIZE && this.illegalCards.size() > 0) {
          bag.addIllegal(this.illegalCards);
        }
        return;
      }
      return;
    }
    if (strategy.equals("bribed")) {
      setBribedBag();
    }
  }

  public int getPlayerPenalty() {
    int penalty = 0;
    if (isLying()) {
      for (Map.Entry<Integer, Integer> card : this.bag.getCards().entrySet()) {
        if (card.getKey() != assetType) {
          penalty += card.getValue()
                  * GoodsFactory.getInstance().getGoodsById(card.getKey()).getPenalty();
        }
      }

    }
    if (!isLying()) {
      for (Map.Entry<Integer, Integer> card : this.bag.getCards().entrySet()) {
        penalty += card.getValue()
                * GoodsFactory.getInstance().getGoodsById(card.getKey()).getPenalty();

      }
    }

    return penalty;
  }

  public int getCountById(final int cardId) {
    for (Map.Entry<Integer, Integer> card : this.bag.getCards().entrySet()) {
      if (card.getKey() == cardId) {
        return card.getValue();
      }
    }
    return 0;
  }

  @Override
  public String toString() {
    return "\t" + strategy + " [" + this.playerId + "]\t: " + this.pocket + " $$$"
            + "\n\tLegal:\t\t" + this.legalCards.getCards().toString()
            + "\n\tIllegals:\t" + this.illegalCards.getCards().toString()
            + "\n\tBag:\t\t" + this.bag.getCards().toString() + "\n";

  }
}
