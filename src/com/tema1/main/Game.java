package com.tema1.main;

import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;
import static com.tema1.main.MagicNumbers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public final class Game {
  private int rounds;
  private List<Player> players = new ArrayList<Player>();
  private List<Integer> allCards;
  private List<HashMap<Integer, Integer>> cardsByPlayer = new ArrayList<>();

  public Game() {
    rounds = -1;
    players = null;
    allCards = null;
  }

  public Game(final int rounds, final List<String> strategy, final List<Integer> cards) {
    this.rounds = rounds;
    this.allCards = cards;
    for (int i = 0; i < strategy.size(); i++) {
      players.add(new Player(strategy.get(i), i));
    }
  }

  public int getRounds() {
    return rounds;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public List<Integer> getallCards() {
    return allCards;
  }


  /**pentru fiecare jucator elibereaza sacul. **/
  private void clearPlayersBag() {
    for (Player player : this.players) {
      player.clearBag();
    }
  }

  /**afiseaza scorul final. **/
  public void finalScore() {
    players.sort(Comparator.comparing(Player::getPocket).reversed());
    for (Player player : players) {
      System.out.println(player.getPlayerId() + " " + player.getStrategy().toUpperCase()
              + " " + player.getPocket());
    }

  }

  /**functia de baza ce ruleaza propriu-zis jocul. **/
  public void startGame() {
    int timesSherrif = 0;
    for (int i = 0; i < INITIAL_CARDS; i++) {
      cardsByPlayer.add(new HashMap<>());
    }
    for (int round = 1; round <= this.rounds; ++round) {
      for (int subRound = 0; subRound < this.players.size(); ++subRound) {
        this.players.get(subRound).setSheriff(true);
        if (getPlayerById(players.size() - 1).isSheriff()) {
          timesSherrif++;
        }
        for (Player player : this.players) {
          if (player.getSheriff()) {
            continue;
          }
          player.setPlayerCards(this.allCards);
          player.setBag(round);

        }
        inspection();
        countPriceBag();
        this.players.get(subRound).setSheriff(false);
        updateCardsByPlayer();
        clearPlayersBag();
        if (timesSherrif == SMALL_BRIBERY) {
          addBonuses();
          return;
        }
      }
    }
    addBonuses();
  }

  /**pentru fiecare jucator determina cate carti de fiecare Id are. **/
  private void updateCardsByPlayer() {
    HashMap<Integer, Integer> cardsById;
    for (int i = 0; i < INITIAL_CARDS; i++) {
      cardsById = cardsByPlayer.get(i);
      for (Player player : this.players) {
        if (cardsById.isEmpty()) {
          cardsById.put(player.getPlayerId(), player.getCountById(i));
        }
        if (cardsById.containsKey(player.getPlayerId())) {
          for (Map.Entry<Integer, Integer> cardPlayer : cardsById.entrySet()) {
            if (cardPlayer.getKey() == player.getPlayerId()) {
              cardsById.replace(player.getPlayerId(), cardPlayer.getValue(),
                      cardPlayer.getValue() + player.getCountById(i));
            }
          }
        } else {
          cardsById.put(player.getPlayerId(), player.getCountById(i));
        }
      }
    }
  }

  /**returneaza jucatorul pentru id-ul primit. **/
  private Player getPlayerById(final int id) {
    for (Player player : this.players) {
      if (player.getPlayerId() == id) {
        return player;
      }
    }
    return null;

  }

  private void addBonuses() {
    HashMap<Integer, Integer> cardsById;
    int kingId = -1;
    int kingV = 0;
    int queenId = -1;
    int queenV = 0;
    for (int i = 0; i < INITIAL_CARDS; i++) {
      cardsById = cardsByPlayer.get(i);
      Map<Integer, Integer> sortedByFrequency = cardsById
              .entrySet()
              .stream()
              .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
              .collect(
                      toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                              LinkedHashMap::new));
      kingId = (int) sortedByFrequency.keySet().toArray()[0];
      queenId = (int) sortedByFrequency.keySet().toArray()[1];
      kingV = (int) sortedByFrequency.values().toArray()[0];
      queenV = (int) sortedByFrequency.values().toArray()[1];
      if (queenV == kingV) {
        if (queenId < kingId) {
          int aux = queenId;
          queenId = kingId;
          kingId = aux;
        }
      }

      if (kingV != 0) {
        getPlayerById(kingId).addToPocket(getKingBonus(i));
      }
      if (queenV != 0) {
        getPlayerById(queenId).addToPocket(getQueenBonus(i));
      }
    }
  }

  public int getKingBonus(final int idAsset) {
    switch (idAsset) {
      case APPLE:
        return KB_APPLE;
      case CHEESE:
        return KB_CHEESE;
      case BREAD:
        return KB_BREAD;
      case CHICKEN:
        return KB_CHICKEN;
      case TOMATO:
        return KB_TOMATO;
      case CORN:
        return KB_CORN;
      case POTATO:
        return KB_POTATO;
      case WINE:
        return KB_WINE;
      case SALT:
        return KB_SALT;
      case SUGAR:
        return KB_SUGAR;
      default:
        return 0;
    }
  }

  public int getQueenBonus(final int idAsset) {
    switch (idAsset) {
      case APPLE:
        return QB_APPLE;
      case CHEESE:
        return QB_CHEESE;
      case BREAD:
        return QB_BREAD;
      case CHICKEN:
        return QB_CHICKEN;
      case TOMATO:
        return QB_TOMATO;
      case CORN:
        return QB_CORN;
      case POTATO:
        return QB_POTATO;
      case WINE:
        return QB_WINE;
      case SALT:
        return QB_SALT;
      case SUGAR:
        return QB_SUGAR;
      default:
        return 0;
    }
  }

  /**adauga cartile la o mapa pentru a calcula mai tarziu clasamentul. **/
  private void addToUpdatedList(final int assetId, final int assetCount, final int playerId) {
    HashMap<Integer, Integer> cardsById = cardsByPlayer.get(assetId);
    if (cardsById.isEmpty()) {
      cardsById.put(playerId, assetCount);
      return;
    }
    if (cardsById.containsKey(playerId)) {
      for (Map.Entry<Integer, Integer> cardPlayer : cardsById.entrySet()) {
        if (cardPlayer.getKey() == playerId) {
          cardsById.replace(playerId, cardPlayer.getValue(),
                  cardPlayer.getValue() + assetCount);
        }
      }
    } else {
      cardsById.put(playerId, assetCount);
    }
  }

  /**calculeaza profitul ce il incaseaza jucatorul. **/
  private void countPriceBag() {
    for (Player player : this.players) {
      for (Map.Entry<Integer, Integer> card : player.getBag().getCards().entrySet()) {
        if (isIllegal(card.getKey())) {
          int bonus = getGoodProfit(card.getKey()) * card.getValue();
          switch (card.getKey()) {
            case SILK:
              addToUpdatedList(CHEESE, 3 * card.getValue(), player.getPlayerId());
              bonus += (3 * getGoodProfit(CHEESE)) * card.getValue();
              break;
            case PEPPER:
              addToUpdatedList(CHICKEN, 2 * card.getValue(), player.getPlayerId());
              bonus += 2 * getGoodProfit(CHICKEN) * card.getValue();
              break;
            case BARREL:
              addToUpdatedList(BREAD, 2 * card.getValue(), player.getPlayerId());
              bonus += 2 * getGoodProfit(BREAD) * card.getValue();
              break;
            case BEER:
              addToUpdatedList(WINE, 4 * card.getValue(), player.getPlayerId());
              bonus += 4 * getGoodProfit(WINE) * card.getValue();
              break;
            case SEAFOOD:
              addToUpdatedList(TOMATO, 2 * card.getValue(), player.getPlayerId());
              addToUpdatedList(POTATO, 3 * card.getValue(), player.getPlayerId());
              addToUpdatedList(CHICKEN, card.getValue(), player.getPlayerId());

              bonus += (2 * getGoodProfit(TOMATO) + 3 * getGoodProfit(POTATO)
                                                  + getGoodProfit(CHICKEN)) * card.getValue();
              break;
            default:
              bonus += 0;
          }
          player.addToPocket(bonus);
        } else {
          player.addToPocket(getGoodProfit(card.getKey()) * card.getValue());
        }
      }
    }
  }

  /**verifica sacul jucatorului. **/
  private void verify(final Player player, final Player sheriff) {
    int penalty = 0;
    if (sheriff.getPocket() == 0) {
      return;
    }
    if (player.isLying()) {
      penalty = player.getPlayerPenalty();
      player.addToPocket(-penalty);
      sheriff.addToPocket(penalty);
      allCards.addAll(player.confiscCards());
      return;
    }
    if (!(player.isLying())) {
      penalty = player.getPlayerPenalty();
      player.addToPocket(penalty);
      sheriff.addToPocket(-penalty);
    }
  }

  /**verifica daca bunul cu id-ul primit este illegal. **/
  private boolean isIllegal(final int id) {
    return GoodsFactory.getInstance().getGoodsById(id).getType().equals(GoodsType.Illegal);
  }

  private int getGoodProfit(final int id) {
    return GoodsFactory.getInstance().getGoodsById(id).getProfit();
  }

  /**pentru fiecare strategie descrie inspectia. **/
  private void inspectAll(final Player sheriff) {

    if (sheriff.getStrategy().equals("greedy")) {

      for (Player player : players) {
        if (player.isSheriff()) {
          continue;
        }

        if (player.getStrategy().equals("bribed") && player.isLying()) {
          if ((player.getBag().countIllegals() == 1)
                  || (player.getBag().countIllegals() == 2)) {
            sheriff.addToPocket(SMALL_BRIBERY);
            player.addToPocket(-SMALL_BRIBERY);
            continue;
          }
          if (player.getBag().countIllegals() > 2) {
            sheriff.addToPocket(BIG_BRIBERY);
            player.addToPocket(-BIG_BRIBERY);

          }

        } else {
          verify(player, sheriff);
        }
      }
    }

    if (sheriff.getStrategy().equals("basic")) {
      for (Player player : players) {
        if (player.isSheriff()) {
          continue;
        }
        if (sheriff.getPocket() > 16) {
          verify(player, sheriff);
        } else {
          break;
        }
      }
    }

    if (sheriff.getStrategy().equals("bribed")) {
      int index = sheriff.getPlayerId();
      int left;
      int right;
      if (index == 0) {
        left = players.size() - 1;
        right = index + 1;
      } else if (index == players.size() - 1) {
        left = players.size() - 2;
        right = 0;
      } else {
        left = index - 1;
        right = index + 1;
      }

      if (right == left) {
        verify(players.get(right), sheriff);
        return;
      }
      for (Player player : this.players) {
        if (player.isSheriff()) {
          continue;
        }
        if (player.getPlayerId() == left) {
          verify(player, sheriff);
          continue;
        }
        if (player.getPlayerId() == right && sheriff.getPocket() >= 16) {
          verify(player, sheriff);
          continue;
        }
        if (player.getStrategy().equals("bribed") && player.isLying()) {
          if ((player.getBag().countIllegals() == 1)
                  || (player.getBag().countIllegals() == 2)) {
            sheriff.addToPocket(SMALL_BRIBERY);
            player.addToPocket(-SMALL_BRIBERY);
            continue;
          }
          if (player.getBag().countIllegals() > 2) {
            sheriff.addToPocket(BIG_BRIBERY);
            player.addToPocket(-BIG_BRIBERY);

          }
        }
      }

    }
  }

  /**determina care jucator e seriful curent si apeleaza inspectia generala. **/
  public void inspection() {
    Player sheriff = new Player();
    for (Player player : this.players) {
      if (player.isSheriff()) {
        sheriff = player;
      }
    }
    inspectAll(sheriff);
  }
}
