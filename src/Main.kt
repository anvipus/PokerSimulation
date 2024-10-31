fun main() {
    val deck = Deck()
    val players = List(5) { PokerHand(deck.dealHand()) }

    players.forEachIndexed { index, hand ->
        println("Player ${index + 1}'s hand: ${hand.cards.joinToString(", ")} with rank ${hand.handRank}")
    }

    val game = PokerGame(players)
    val winner = game.determineWinner()
    println("The winner is: ${winner?.handRank}")
}

enum class Suit(val symbol: Char) {
    SPADES('♠'),
    HEARTS('♥'),
    CLUBS('♣'),
    DIAMONDS('♦')
}

enum class Rank(val value: Int) {
    TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8),
    NINE(9), TEN(10), JACK(11), QUEEN(12), KING(13), ACE(14)
}

data class Card(val suit: Suit, val rank: Rank) {
    override fun toString(): String = "${rank.name} of ${suit.symbol}"
}

class Deck {
    private val cards: MutableList<Card> = mutableListOf()

    init {
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                cards.add(Card(suit, rank))
            }
        }
        shuffle()
    }

    fun shuffle() = cards.shuffle()

    fun dealHand(): List<Card> = cards.take(5).also { cards.removeAll(it) }
}

enum class HandRank {
    ROYAL_FLUSH, STRAIGHT_FLUSH, FOUR_OF_A_KIND, FULL_HOUSE,
    FLUSH, STRAIGHT, THREE_OF_A_KIND, TWO_PAIR, ONE_PAIR, HIGH_CARD
}

class PokerHand(val cards: List<Card>) {
    val handRank: HandRank = evaluateHand()

    private fun evaluateHand(): HandRank {
        return when {
            isRoyalFlush() -> HandRank.ROYAL_FLUSH
            isStraightFlush() -> HandRank.STRAIGHT_FLUSH
            isFourOfAKind() -> HandRank.FOUR_OF_A_KIND
            isFullHouse() -> HandRank.FULL_HOUSE
            isFlush() -> HandRank.FLUSH
            isStraight() -> HandRank.STRAIGHT
            isThreeOfAKind() -> HandRank.THREE_OF_A_KIND
            isTwoPair() -> HandRank.TWO_PAIR
            isOnePair() -> HandRank.ONE_PAIR
            else -> HandRank.HIGH_CARD
        }
    }

    private fun isRoyalFlush(): Boolean {
        val isRoyal = isStraightFlush() && cards.minOf { it.rank.value } == 10
        //println("Checking Royal Flush: $isRoyal")
        return isRoyal
    }

    private fun isStraightFlush(): Boolean {
        val isStraightFlush = isFlush() && isStraight()
        //println("Checking Straight Flush: $isStraightFlush")
        return isStraightFlush
    }

    private fun isFourOfAKind(): Boolean {
        val isFourKind = cards.groupBy { it.rank }.any { it.value.size == 4 }
        //println("Checking Four of a Kind: $isFourKind")
        return isFourKind
    }

    private fun isFullHouse(): Boolean {
        val grouped = cards.groupBy { it.rank }
        val isFullHouse = grouped.count { it.value.size == 3 } == 1 && grouped.count { it.value.size == 2 } == 1
        //println("Checking Full House: $isFullHouse")
        return isFullHouse
    }

    private fun isFlush(): Boolean {
        val isFlush = cards.all { it.suit == cards.first().suit }
        //println("Checking Flush: $isFlush")
        return isFlush
    }

    private fun isStraight(): Boolean {
        val sortedRanks = cards.map { it.rank.value }.sorted()
        val isSequential = (1 until sortedRanks.size).all { sortedRanks[it] - sortedRanks[it - 1] == 1 }

        // Special case for Ace-low straight (A, 2, 3, 4, 5)
        val isAceLowStraight = sortedRanks == listOf(2, 3, 4, 5, 14)

        val isStraight = isSequential || isAceLowStraight
        //println("Checking Straight: $isStraight")
        return isStraight
    }

    private fun isThreeOfAKind(): Boolean {
        val isThreeKind = cards.groupBy { it.rank }.any { it.value.size == 3 }
        //println("Checking Three of a Kind: $isThreeKind")
        return isThreeKind
    }

    private fun isTwoPair(): Boolean {
        val pairCount = cards.groupBy { it.rank }.count { it.value.size == 2 }
        val isTwoPair = pairCount == 2
        //println("Checking Two Pair: $isTwoPair")
        return isTwoPair
    }

    private fun isOnePair(): Boolean {
        val isOnePair = cards.groupBy { it.rank }.count { it.value.size == 2 } == 1
        //println("Checking One Pair: $isOnePair")
        return isOnePair
    }
}



class PokerGame(private val players: List<PokerHand>) {
    fun determineWinner(): PokerHand? {
        return players.maxByOrNull { it.handRank }
    }
}