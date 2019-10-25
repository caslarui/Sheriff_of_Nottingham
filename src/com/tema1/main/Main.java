package com.tema1.main;

import com.tema1.goods.GoodsFactory;

public final class Main {
    private Main() {
        // just to trick checkstyle
    }

    public static void main(final String[] args) {
        GameInputLoader gameInputLoader = new GameInputLoader(args[0], args[1]);
        GameInput gameInput = gameInputLoader.load();



        Game joc = new Game(gameInput.getRounds(), gameInput.getPlayerNames(), gameInput.getAssetIds());

        System.out.println(joc.getRounds());
//        System.out.println(joc.getPlayers().toString());
        System.out.println((joc.getAllCards()));
        joc.startGame();
//        System.out.println(joc.getPlayers().toString());






        //TODO implement homework logic
    }
}
