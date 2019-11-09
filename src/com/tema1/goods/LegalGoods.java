package com.tema1.goods;

public class LegalGoods extends Goods {
  private final int kingBonus;
  private final int queenBonus;

  public LegalGoods(final int id, final int profit, final int penalty, final int kingBonus,
                    final int queenBonus) {
    super(id, GoodsType.Legal, profit, penalty);

    this.kingBonus = kingBonus;
    this.queenBonus = queenBonus;
  }

  public final int getKingBonus() {
    return kingBonus;
  }

  public final int getQueenBonus() {
    return queenBonus;
  }
}
