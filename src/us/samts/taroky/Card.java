package us.samts.taroky;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Card {
    private final int id;
    private String name;
    private int pointValue;
    private Suit suit;
    private BufferedImage img;
    private static BufferedImage CARDBACK;

    public enum Suit {
        TRUMP,
        SPADES,
        CLUBS,
        HEARTS,
        DIAMONDS
    }
    public Card(int cardID) {
        id = cardID < 55 && cardID > 0 ? cardID : 0;
        if (id == 0) {
            throw new Error("Card.id.invalidIDNumber: An invalid ID number has been passed");
        }
        formCard();
    }
    public int getId() {return id;}
    public int getPointValue() {
        return pointValue;
    }
    public String getName() {
        return name;
    }
    public Suit getSuit() {
        return suit;
    }
    private void formCard() {
        String n = "";
        suit = Suit.TRUMP;
        switch (id) {
            case 1: n = "I";
                pointValue = 5;
                break;
            case 2: n = "II";
                pointValue = 1;
                break;
            case 3: n = "III";
                pointValue = 1;
                break;
            case 4: n = "IIII";
                pointValue = 1;
                break;
            case 5: n = "V";
                pointValue = 1;
                break;
            case 6: n = "VI";
                pointValue = 1;
                break;
            case 7: n = "VII";
                pointValue = 1;
                break;
            case 8: n = "VIII";
                pointValue = 1;
                break;
            case 9: n = "IX";
                pointValue = 1;
                break;
            case 10: n = "X";
                pointValue = 1;
                break;
            case 11: n = "XI";
                pointValue = 1;
                break;
            case 12: n = "XII";
                pointValue = 1;
                break;
            case 13: n = "XIII";
                pointValue = 1;
                break;
            case 14: n = "XIV";
                pointValue = 1;
                break;
            case 15: n = "XV";
                pointValue = 1;
                break;
            case 16: n = "XVI";
                pointValue = 1;
                break;
            case 17: n = "XVII";
                pointValue = 1;
                break;
            case 18: n = "XVIII";
                pointValue = 1;
                break;
            case 19: n = "XIX";
                pointValue = 1;
                break;
            case 20: n = "XX";
                pointValue = 1;
                break;
            case 21: n = "XXI";
                pointValue = 5;
                break;
            case 22: n = "Škýz";
                pointValue = 5;
                break;
            default:
                if (id>0) {
                    switch ((id - 22) % 8) {
                        case 0 -> {
                            n = "Ace of ";
                            pointValue = 1;
                        }
                        case 1 -> {
                            n = "Two of ";
                            pointValue = 1;
                        }
                        case 2 -> {
                            n = "Three of ";
                            pointValue = 1;
                        }
                        case 3 -> {
                            n = "Four of ";
                            pointValue = 1;
                        }
                        case 4 -> {
                            n = "Jack of ";
                            pointValue = 2;
                        }
                        case 5 -> {
                            n = "Rider of ";
                            pointValue = 3;
                        }
                        case 6 -> {
                            n = "Queen of ";
                            pointValue = 4;
                        }
                        case 7 -> {
                            n = "King of ";
                            pointValue = 5;
                        }
                    }
                    if (id <= 30) {
                        n += "Spades";
                        suit = Suit.SPADES;
                    } else if (id <= 38) {
                        n += "Clubs";
                        suit = Suit.CLUBS;
                    } else if (id <= 46) {
                        n += "Hearts";
                        suit = Suit.HEARTS;
                    } else if (id <= 54) {
                        n += "Diamonds";
                        suit = Suit.DIAMONDS;
                    }
                }
        }
        name = n;
        try {
            img = ImageIO.read(Objects.requireNonNull(Card.class.getResource("/resources/images/" + name + ".jpg")));
        } catch (IOException ignored) {
        }
    }
    public BufferedImage getImg() {
        return img;
    }
    public static BufferedImage getCardBack() {
        if (null==CARDBACK) {
            try {
                CARDBACK = ImageIO.read(Objects.requireNonNull(Card.class.getResource("/resources/images/TarokyBack.jpg")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return CARDBACK;
    }
    public String toString() {
        return name;
    }
}