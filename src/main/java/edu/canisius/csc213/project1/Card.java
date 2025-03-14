package edu.canisius.csc213.project1;

public class Card {

    public enum Suit { HEARTS, DIAMONDS, CLUBS, SPADES }
    public enum Rank { 
        TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, 
        JACK, QUEEN, KING, ACE 
    }

    private final Suit suit; 
    private final Rank rank; 

    public Card(Suit suit, Rank rank) {
        this.suit = suit; 
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit; 
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; 
        if (obj == null || getClass() != obj.getClass()) return false; 
        Card card = (Card) obj; 
        return rank == card.rank && suit == card.suit; 
    }

    @Override
    public int hashCode() { 
        int result = suit.hashCode();
        result = 31 * result + rank.hashCode(); 
        return result; 
    }
}

