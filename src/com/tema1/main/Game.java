package com.tema1.main;

import java.util.*;
import com.tema1.goods.GoodsFactory;
import com.tema1.goods.GoodsType;

import static java.util.stream.Collectors.toMap;

public final class Game {
    private int rounds;
    private List<Player> players = new ArrayList<Player>();
    private List<Integer> AllCards;
    private List<HashMap<Integer, Integer>> cardsByPlayer = new ArrayList<>();

    public Game() {
        rounds = -1;
        players = null;
        AllCards = null;
    }

    public Game(int rounds, List<String> Strategy, List<Integer> cards) {
        this.rounds = rounds;
        this.AllCards = cards;
        for (int i = 0; i < Strategy.size(); i++) {
            players.add( new Player(Strategy.get(i), i));
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

    private void clearPlayersBag() {
        for(Player player : this.players) {
            player.clearBag();
        }
    }

    public void finalScore() {
        players.sort(Comparator.comparing(Player::getPocket).reversed());
        for(Player player : players)
            System.out.println(player.getPlayerId() + " " + player.getStrategy().toUpperCase() +
                    " " + player.getPocket());
    }

    public final void startGame() {
        int timesSherrif = 0;
        for(int i = 0; i < 10; i++) {
            cardsByPlayer.add( new HashMap<>());
        }
        for (int round = 1; round <= this.rounds; ++round) {
            for (int subRound = 0; subRound < this.players.size(); ++subRound) {
                this.players.get(subRound).setSheriff(true);
                if(getPlayerById(players.size() - 1).isSheriff())
                    timesSherrif++;
                for (Player player : this.players) {
                    if (player.getSheriff())
                        continue;
                    System.out.println("Runda : " + round + "\t subrunda : " + subRound);
                    player.setPlayerCards(this.AllCards);
//                    printPlayers();
                    player.setBag(round);

                }
                printPlayers();
                Inspection();
                countPriceBag();
                this.players.get(subRound).setSheriff(false);
                System.out.println("Dupa Inspectie");
                printPlayers();
//                System.out.println(cardsByPlayer);
                updateCardsByPlayer();
//                System.out.println(cardsByPlayer);
                clearPlayersBag();
                if (timesSherrif == 5) {
                    addBonuses();
                    return ;
                }
            }
//                printPlayers();
        }
                addBonuses();
    }

    private void updateCardsByPlayer() {
        HashMap<Integer, Integer> cardsById;
        for(int i = 0; i < 10; i++) {
            cardsById = cardsByPlayer.get(i);
            for(Player player : this.players) {
                    if(cardsById.isEmpty()) {
                        cardsById.put(player.getPlayerId(), player.getCountById(i));
                    }
                    if(cardsById.containsKey(player.getPlayerId())) {
                        for(Map.Entry<Integer, Integer> cardPlayer : cardsById.entrySet()) {
                            if(cardPlayer.getKey() == player.getPlayerId())
                                cardsById.replace(player.getPlayerId(),cardPlayer.getValue(), cardPlayer.getValue() + player.getCountById(i));
                        }
                    } else {
                        cardsById.put(player.getPlayerId(), player.getCountById(i));
                    }
            }
        }
    }

    private Player getPlayerById(int id) {
        for(Player player : this.players)
            if(player.getPlayerId() == id)
                return player;
        return null;

    }

    private void addBonuses() {
        HashMap<Integer, Integer> cardsById;
        int kingId = -1;
        int kingV = 0;
        int queenId = -1;
        int queenV = 0;
        for (int i = 0; i < 10; i++) {
            cardsById = cardsByPlayer.get(i);
            Map<Integer, Integer> sortedByFrequency = cardsById
                    .entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .collect(
                            toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                    LinkedHashMap::new));
            kingId = (int)sortedByFrequency.keySet().toArray()[0];
            queenId = (int)sortedByFrequency.keySet().toArray()[1];
            kingV = (int)sortedByFrequency.values().toArray()[0];
            queenV = (int)sortedByFrequency.values().toArray()[1];
            if(queenV == kingV) {
                if (queenId < kingId) {
                    int aux = queenId;
                    queenId = kingId;
                    kingId = aux;
                }
            }

            if(kingV != 0)
                getPlayerById(kingId).addToPocket(getKingBonus(i));
            if(queenV != 0)
                getPlayerById(queenId).addToPocket(getQueenBonus(i));
        }
    }
    public int getKingBonus(int idAsset) {
        switch (idAsset){
            case 0: return 20;
            case 1: return 19;
            case 2: return 18;
            case 3: return 17;
            case 4: return 16;
            case 5: return 15;
            case 6: return 14;
            case 7: return 13;
            case 8: return 12;
            case 9: return 11;
            default: return 0;
        }
    }
    public int getQueenBonus(int idAsset) {
        switch (idAsset){
            case 0: return 10;
            case 1: return 9;
            case 2: return 10 - 1;
            case 3: return 8;
            case 4: return 7;
            case 5: return 6;
            case 6: return 5;
            case 7: return 4;
            case 8: return 3;
            case 9: return 2;
            default: return 0;
        }
    }

    private void addToUpdatedList(int assetId, int assetCount, int playerId) {
        HashMap<Integer, Integer> cardsById = cardsByPlayer.get(assetId);
        if(cardsById.isEmpty()) {
//        System.out.println(assetId + " " + assetCount );
            cardsById.put(playerId, assetCount);
            return ;
        }
        if(cardsById.containsKey(playerId)) {
            for(Map.Entry<Integer, Integer> cardPlayer : cardsById.entrySet()) {
                if(cardPlayer.getKey() == playerId)
                    cardsById.replace(playerId,cardPlayer.getValue(), cardPlayer.getValue() + assetCount);
            }
        } else {
            cardsById.put(playerId, assetCount);
        }
    }
    private void countPriceBag() {
        for (Player player : this.players) {
            for(Map.Entry<Integer, Integer> card : player.getBag().getCards().entrySet()) {
                if(isIllegal(card.getKey())) {
                    int bonus = getGoodProfit(card.getKey())*card.getValue();
                    switch (card.getKey()) {
                        case 20:
                            addToUpdatedList(1,3*card.getValue(), player.getPlayerId());
                            bonus += (3 * getGoodProfit(1))*card.getValue(); break;
                        case 21 :
                            addToUpdatedList(3,2*card.getValue(), player.getPlayerId());
                            bonus += 2 * getGoodProfit(3) *card.getValue(); break;
                        case 22:
                            addToUpdatedList(2,2*card.getValue(), player.getPlayerId());
                            bonus += 2 * getGoodProfit(2)*card.getValue(); break;
                        case 23:
                            addToUpdatedList(7,4*card.getValue(), player.getPlayerId());
                            bonus += 4 * getGoodProfit(7)*card.getValue(); break;
                        case 24:
                            addToUpdatedList(4,2*card.getValue(),player.getPlayerId());
                            addToUpdatedList(6,3*card.getValue(),player.getPlayerId());
                            addToUpdatedList(3, card.getValue(),player.getPlayerId());

                            bonus += (2 * getGoodProfit(4) + 3 * getGoodProfit(6) + getGoodProfit(3))
                            * card.getValue();
                            break;
                            default:bonus += 0;
                    }
                    player.addToPocket(bonus);
                } else {
                    player.addToPocket(getGoodProfit(card.getKey()) * card.getValue());
                }
            }
        }
    }

    private void printPlayers() {
        for (Player player : this.players) {
            System.out.println(player.toString());
        }
    }

    private void verify(Player player, Player sheriff){
        int penalty = 0;
            if(sheriff.getPocket() == 0)
                return ;
        if(player.isLying()){
            penalty = player.getPlayerPenalty();
            player.addToPocket(-penalty);
            sheriff.addToPocket(penalty);
            AllCards.addAll(player.confiscCards());
//            System.out.println(sheriff.getPocket());
            return;
        }
//        System.out.println(penalty);
        if(!(player.isLying())){
            penalty = player.getPlayerPenalty();
            player.addToPocket(penalty);
            sheriff.addToPocket(-penalty);
        }
    }

    private boolean isIllegal(int id) {
        return GoodsFactory.getInstance().getGoodsById(id).getType().equals(GoodsType.Illegal);
    }

    private int getGoodProfit(int id) {
        return GoodsFactory.getInstance().getGoodsById(id).getProfit();
    }
    private void inspectAll(Player sheriff){

        if(sheriff.getStrategy().equals("greedy")) {

            for (Player player : players) {
                if (player.isSheriff())
                    continue;

                if (player.getStrategy().equals("bribed") && player.isLying()) {
                    if ((player.getBag().countIllegals() == 1) ||
                            (player.getBag().countIllegals() == 2)) {
                        sheriff.addToPocket(5);
                        player.addToPocket(-5);
                        continue ;
                    }
                    if (player.getBag().countIllegals() > 2) {
                        sheriff.addToPocket(10);
                        player.addToPocket(-10);

                    }

                } else {
                    verify(player, sheriff);
                }
            }
        }

        if(sheriff.getStrategy().equals("basic")){
            for (Player player : players) {
                if (player.isSheriff())
                    continue;
                if(sheriff.getPocket() > 16)
                    verify(player,sheriff);
                else
                    break;
            }
        }

        if(sheriff.getStrategy().equals("bribed")){
            int index = sheriff.getPlayerId();
            int left;
            int right;
//            System.out.println("\n A intrat\n");
            if(index == 0) {
                left = players.size() - 1;
                right = index + 1;
            }else if (index == players.size() - 1) {
                left = players.size() - 2;
                right = 0;
            } else {
                left = index - 1;
                right = index + 1;
            }

            if(right == left) {
                verify(players.get(right), sheriff);
                return ;
            }
//            System.out.println("Left - " + left + "\tRight: " + right);
            for (Player player : this.players) {
                if (player.isSheriff())
                    continue;
                if(player.getPlayerId() == left) {
                    verify(player, sheriff);
                    continue;
                }
                if(player.getPlayerId() == right && sheriff.getPocket() >= 16) {
                    verify(player, sheriff);
//                    System.out.println("\n\n\n\nA confiscat\n\n\n\n\n");
                    continue;
                }
                if(player.getStrategy().equals("bribed") && player.isLying()) {
                    if ((player.getBag().countIllegals() == 1) ||
                            (player.getBag().countIllegals() == 2)) {
                        sheriff.addToPocket(5);
                        player.addToPocket(-5);
                        continue ;
                    }
                    if (player.getBag().countIllegals() > 2) {
                        sheriff.addToPocket(10);
                        player.addToPocket(-10);

                    }
                }
            }

        }
    }

    public void Inspection(){
        Player sheriff = new Player();
        for (Player player : this.players) {
            if (player.isSheriff())
                sheriff = player;
        }
        inspectAll(sheriff);
    }
}
