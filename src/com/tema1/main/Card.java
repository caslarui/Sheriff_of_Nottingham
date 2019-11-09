package com.tema1.main;

import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public final class Card {
  private Map<Integer, Integer> cards;

  Card() {
    cards = new HashMap<>();
  }

  public Map<Integer, Integer> getCards() {
    return cards;
  }

  public int countIllegals() {
    int i = 0;
    for (Map.Entry<Integer, Integer> val : cards.entrySet()) {
      if (GoodsFactory.getInstance().getGoodsById(val.getKey()).getType()
              .equals(GoodsType.Illegal)) {
        i += val.getValue();
      }
    }
    return i;
  }

  public Map<java.lang.Integer, java.lang.Integer> getMostProfit() {
    Map<Integer, Integer> ret = new HashMap<>();
    int profit = 0;
    int id = 0;
    int value = 0;
    for (Map.Entry<Integer, Integer> val : this.cards.entrySet()) {
      if (GoodsFactory.getInstance().getGoodsById(val.getKey()).getProfit() > profit) {
        id = val.getKey();
        value = val.getValue();
        profit = GoodsFactory.getInstance().getGoodsById(val.getKey()).getProfit();
      }
      if (GoodsFactory.getInstance().getGoodsById(val.getKey()).getProfit() == profit
              && id < val.getKey()) {
        id = val.getKey();
        value = val.getValue();
        profit = GoodsFactory.getInstance().getGoodsById(val.getKey()).getProfit();
      }
    }
    ret.put(id, value);
    return ret;
  }

  public void mostFrequent() {
    int maxCount = 0;
    Map<Integer, Integer> res = new HashMap<>();
    Map<Integer, Integer> sortedByFrequency = this.cards
            .entrySet()
            .stream()
            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
            .collect(
                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                            LinkedHashMap::new));

    for (Map.Entry<Integer, Integer> val : sortedByFrequency.entrySet()) {
      if (maxCount <= val.getValue()) {
        res.put(val.getKey(), val.getValue());
        maxCount = val.getValue();
      }
    }

    cards = res;
  }

  public void addIllegal(final Card illegalCards) {
    int profit = 0;
    int id = 0;
    for (Map.Entry<Integer, Integer> val : illegalCards.getCards().entrySet()) {
      if (GoodsFactory.getInstance().getGoodsById(val.getKey()).getProfit() > profit) {
        profit = GoodsFactory.getInstance().getGoodsById(val.getKey()).getProfit();
        id = val.getKey();
      }
    }
    if (cards.containsKey(id)) {
      for (Map.Entry<Integer, Integer> val : cards.entrySet()) {
        if (val.getKey() == id) {
          cards.replace(val.getKey(), val.getValue(), val.getValue() + 1);
        }
      }
    } else {
      cards.put(id, 1);
    }
  }

  public int size() {
    int size = 0;
    for (Map.Entry<Integer, Integer> card : this.cards.entrySet()) {
      size += card.getValue();
    }
    return size;
  }
}
