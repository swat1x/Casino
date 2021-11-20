package ru.swat1x.casino.game.jackpot;

import lombok.Value;

@Value
public class Bet {

    String player;
    Integer bet;
//
//    public Bet(String player, Integer bet){
//        this.player = player;
//        this.bet = bet;
//    }
//
//    public String getPlayer() {
//        return player;
//    }
//
//    public Integer getBet() {
//        return bet;
//    }

    public double getChanceFrom(double max){
        return (getBet()*100)/max;
    }
}
