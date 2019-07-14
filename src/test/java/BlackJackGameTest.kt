import Card.Companion.fromByte
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import java.math.BigDecimal

object BlackJackGameTest : Spek({
    group("blackjackgametest") {

        test("Agent calculates utility in hands properly") {

            var shoe = fromCounts(Pair(Card.TWO, 1), Pair(Card.TEN, 1))

            var playerHand = fromCards(Card.TEN, Card.TEN)
            var dealerHand = fromCards(Card.TEN, Card.FIVE)
            var utility = scoreHand(
                    getPreferredValue(playerHand),
                    dealerHand,
                    Card.TEN,
                    shoe,
                    false,
                    false,
                    false,
                    false
            )
            assertThat(utility.toDouble()).isEqualTo(1.0)
            shoe = fromCounts(Pair(Card.TEN, 1), Pair(Card.SIX, 1))
            utility = scoreHand(
                    getPreferredValue(playerHand),
                    dealerHand,
                    Card.TEN,
                    shoe,
                    false,
                    false,
                    false,
                    false
            )
            assertThat(utility.toDouble()).isEqualTo(0.0)
            var playerHand2 = fromCards(Card.NINE, Card.ACE, Card.ACE)
            utility = scoreHand(
                    getPreferredValue(playerHand2),
                    dealerHand,
                    Card.TEN,
                    shoe,
                    false,
                    false,
                    false,
                    false
            )
            assertThat(utility.toDouble()).isEqualTo(0.5)
            dealerHand = playerHand2
            utility = scoreHand(
                    getPreferredValue(playerHand),
                    dealerHand,
                    Card.ACE,
                    shoe,
                    false,
                    false,
                    false,
                    false
            )
            assertThat(utility.toDouble()).isEqualTo(-1.0)
            utility = scoreHand(
                    getPreferredValue(playerHand),
                    dealerHand,
                    Card.ACE,
                    shoe,
                    false,
                    false,
                    true,
                    false
            )
            assertThat(utility.toDouble()).isEqualTo(-1.5)

        }

        test("getactions makes sense") {
            var playerHand = fromCards(Card.ACE, Card.ACE)
            var dealerHand = fromCards(Card.TEN)
            var actions = getAllPossibleActions(
                    playerHand,
                    dealerHand,
                    null,
                    false,
                    false
            )
            assertThat(actions.size).isEqualTo(4)
        }

        test("getUtility returns reasonable values") {
            assertThat(getUtility(20, 2, 2, Card.TWO, false, false, false, false)).isEqualTo(1.0)
            assertThat(getUtility(15, 18, 2, Card.TWO, false, false, false, false)).isEqualTo(-1.0)
            assertThat(getUtility(21, 18, 2, Card.TWO, true, false, false, false)).isEqualTo(1.5)
            assertThat(getUtility(21, 21, 2, Card.TWO, true, false, true, false)).isEqualTo(1.0)
            assertThat(getUtility(18, 21, 2, Card.TWO, false, false, true, false)).isEqualTo(0.0)
        }

        test("scoreHand returns reasonable values") {
            var shoe = makeShoe(6)
            assertThat(
                    scoreHand(20, HandHelper.of(Card.TEN.num.toInt()), Card.TEN, shoe, false, false, false, false).toDouble()
            ).isStrictlyBetween(0.0, 1.0)
            assertThat(scoreHand(4, HandHelper.of(Card.TEN.num.toInt()), Card.TEN, shoe, false, false, false, false).toDouble()).isStrictlyBetween(
                    -1.0,
                    0.0
            )
        }

        test("score hands works") {
            assertThat(
                    scoreHand(
                            21,
                            fromCard(fromByte(1)),
                            fromByte(1),
                            fromHands(6,
                                    fromCards(*listOf(0, 9).map { fromByte(it.toByte()) }.toTypedArray()),
                                    fromCard(fromByte(1))),
                            true,
                            false,
                            false,
                            false
                    )).isEqualTo(BigDecimal(1.5))


            assertThat(scoreHand(
                    21,
                    HandHelper.of(1),
                    fromByte(0),
                    fromHands(6, HandHelper.of(0, 9), HandHelper.of(1)),
                    true,
                    false,
                    false,
                    false
            ).toDouble()).isStrictlyBetween(0.0, 1.5)
            assertThat(
                    scoreHand(
                            21,
                            HandHelper.of(0),
                            fromByte(0),
                            fromHands(6, HandHelper.of(0, 9), HandHelper.of(1)),
                            true,
                            false,
                            true,
                            false
                    ).toDouble()).isStrictlyBetween(0.0, 1.5)
        }
    }

    test("score states") {

        // bj, regular dealer
        var score: BigDecimal
        var player = fromCards(Card.ACE, Card.TEN)
        var dealer = fromCard(Card.FIVE)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.FIVE,
                fromHands(6, player, dealer),
                true,
                false,
                false,
                false
        )
        assertThat(score.toDouble()).isEqualTo(1.5)

        // bj, ace hole, no insurance
        player = fromCards(Card.ACE, Card.TEN)
        dealer = fromCard(Card.ACE)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, player, dealer),
                true,
                false,
                false,
                false
        )
        assertThat(score.toDouble()).isStrictlyBetween(0.0, 1.5)

        // bj, ace hole, insurance
        player = fromCards(Card.ACE, Card.TEN)
        dealer = fromCard(Card.ACE)
        var insScore = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, player, dealer),
                true,
                false,
                true,
                false
        )
        assertThat(insScore.toDouble()).isGreaterThan(score.toDouble())

        // both bj, no insurance
        player = fromCards(Card.ACE, Card.TEN)
        dealer = fromCards(Card.ACE, Card.TEN)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                true,
                false,
                false,
                false
        )
        assertThat(score.toDouble()).isEqualTo(0.0)

        // both bj, insurance
        player = fromCards(Card.ACE, Card.TEN)
        dealer = fromCards(Card.ACE, Card.TEN)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                true,
                false,
                true,
                false
        )
        assertThat(score.toDouble()).isEqualTo(1.0)

        // 21, no ace hole
        player = fromCards(Card.ACE, Card.EIGHT, Card.TWO)
        dealer = fromCard(Card.FIVE)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.FIVE,
                fromHands(6, player, dealer),
                false,
                false,
                false,
                false
        )
        assertThat(score.toDouble()).isStrictlyBetween(0.0, 1.0)

        // 21, ace hole, no insurance
        player = fromCards(Card.ACE, Card.EIGHT, Card.TWO)
        dealer = fromCard(Card.ACE)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, player, dealer),
                false,
                false,
                false,
                false
        )
        assertThat(score.toDouble()).isStrictlyBetween(0.0, 1.0)

        // 21, ace hole, insurance
        player = fromCards(Card.ACE, Card.EIGHT, Card.TWO)
        dealer = fromCard(Card.ACE)
        insScore = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, player, dealer),
                false,
                false,
                true,
                false
        )
        assertThat(insScore.toDouble()).isStrictlyBetween(0.0, 1.0)

        // bust, ace hole, no insurance
        player = fromCards(Card.TEN, Card.NINE, Card.TWO, Card.TWO)
        dealer = fromCard(Card.ACE)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                false,
                false,
                false
        )
        assertThat(score.toDouble()).isEqualTo(-1.0)

        // bust, ace hole, insurance
        player = fromCards(Card.TEN, Card.NINE, Card.TWO, Card.TWO)
        dealer = fromCard(Card.ACE)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                false,
                true,
                false
        )
        assertThat(score.toDouble()).isEqualTo(-1.5)

        // player > dealer, no insurance
        player = fromCards(Card.ACE, Card.NINE)
        dealer = fromCards(Card.ACE, Card.SIX)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                false,
                false,
                false
        )
        assertThat(score.toDouble()).isEqualTo(1.0)

        // player > dealer, insurance
        player = fromCards(Card.ACE, Card.NINE)
        dealer = fromCards(Card.ACE, Card.SIX)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                false,
                true,
                false
        )
        assertThat(score.toDouble()).isEqualTo(0.5)

        // player < dealer, no insurance
        player = fromCards(Card.ACE, Card.SIX)
        dealer = fromCards(Card.ACE, Card.NINE)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                false,
                false,
                false
        )
        assertThat(score.toDouble()).isEqualTo(-1.0)

        // player < dealer, insurance
        player = fromCards(Card.ACE, Card.SIX)
        dealer = fromCards(Card.ACE, Card.NINE)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                false,
                true,
                false
        )
        assertThat(score.toDouble()).isEqualTo(-1.5)

        // dealer bust, no insurance
        player = fromCards(Card.ACE, Card.SIX)
        dealer = fromCards(Card.ACE, Card.NINE, Card.TEN, Card.TEN)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                false,
                false,
                false
        )
        assertThat(score.toDouble()).isEqualTo(1.0)

        // dealer bust, insurance
        player = fromCards(Card.ACE, Card.SIX)
        dealer = fromCards(Card.ACE, Card.NINE, Card.TEN, Card.TEN)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                false,
                true,
                false
        )
        assertThat(score.toDouble()).isEqualTo(0.5)

        // equal, no insurance
        player = fromCards(Card.ACE, Card.SIX)
        dealer = fromCards(Card.ACE, Card.SIX)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                false,
                false,
                false
        )
        assertThat(score.toDouble()).isEqualTo(0.0)

        // equal, insurance
        player = fromCards(Card.ACE, Card.SIX)
        dealer = fromCards(Card.ACE, Card.SIX)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                false,
                true,
                false
        )
        assertThat(score.toDouble()).isEqualTo(-0.5)

        // dealer 21, no ace hole
        player = fromCards(Card.ACE, Card.SIX)
        dealer = fromCards(Card.ACE, Card.EIGHT, Card.TWO)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.EIGHT,
                fromHands(6, dealer, player),
                false,
                false,
                false,
                false
        )
        assertThat(score.toDouble()).isEqualTo(-1.0)

        // dealer 21, ace hole, insurance
        player = fromCards(Card.ACE, Card.SIX)
        dealer = fromCards(Card.ACE, Card.EIGHT, Card.TWO)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                false,
                true,
                false
        )
        assertThat(score.toDouble()).isEqualTo(-1.5)

        // dealer 21, ace hole, equal, no ins
        player = fromCards(Card.ACE, Card.EIGHT, Card.TWO)
        dealer = fromCards(Card.ACE, Card.EIGHT, Card.TWO)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                false,
                false,
                false
        )
        assertThat(score.toDouble()).isEqualTo(0.0)

        // dealer 21, ace hole, equal, ins
        player = fromCards(Card.ACE, Card.EIGHT, Card.TWO)
        dealer = fromCards(Card.ACE, Card.EIGHT, Card.TWO)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                false,
                true,
                false
        )
        assertThat(score.toDouble()).isEqualTo(-0.5)

        // dealer blackjack, player 21, no ins
        player = fromCards(Card.ACE, Card.EIGHT, Card.TWO)
        dealer = fromCards(Card.ACE, Card.TEN)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                false,
                false,
                false
        )
        // TODO: does dealer BJ beat 21??
        assertThat(score.toDouble()).isEqualTo(-1.0)

        // dealer blackjack, player 21, ins

        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                false,
                true,
                false
        )
        assertThat(score.toDouble()).isEqualTo(0.0)

        // dealer blackjack, face ace, player 21, double, no ins
        player = fromCards(Card.ACE, Card.EIGHT, Card.TWO)
        dealer = fromCards(Card.ACE, Card.TEN)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                true,
                false,
                false
        )
        assertThat(score.toDouble()).isEqualTo(-1.0)

        // dealer blackjack, face ten, player 21, double, no ins
        player = fromCards(Card.ACE, Card.EIGHT, Card.TWO)
        dealer = fromCards(Card.ACE, Card.TEN)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.TEN,
                fromHands(6, dealer, player),
                false,
                true,
                false,
                false
        )
        assertThat(score.toDouble()).isEqualTo(-0.5)

        // dealer blackjack, face ace, player 21, double, ins
        player = fromCards(Card.ACE, Card.EIGHT, Card.TWO)
        dealer = fromCards(Card.ACE, Card.TEN)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.ACE,
                fromHands(6, dealer, player),
                false,
                true,
                true,
                false
        )
        // TODO: idk man
        // assertThat(score.toDouble()).isEqualTo(-1.0)

        // dealer win, double
        player = fromCards(Card.SEVEN, Card.EIGHT, Card.TWO)
        dealer = fromCards(Card.EIGHT, Card.TEN)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.TEN,
                fromHands(6, dealer, player),
                false,
                true,
                false,
                false
        )
        assertThat(score.toDouble()).isEqualTo(-1.0)

        // player win, double
        player = fromCards(Card.SEVEN, Card.EIGHT, Card.THREE)
        dealer = fromCards(Card.SEVEN, Card.TEN)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.TEN,
                fromHands(6, dealer, player),
                false,
                true,
                false,
                false
        )
        assertThat(score.toDouble()).isEqualTo(1.0)

        // player win, double
        player = fromCards(Card.ACE, Card.TEN)
        dealer = fromCards(Card.SEVEN, Card.TEN)
        score = scoreHand(
                getPreferredValue(player),
                dealer,
                Card.TEN,
                fromHands(6, dealer, player),
                false,
                false,
                false,
                true
        )
        assertThat(score.toDouble()).isEqualTo(1.0)
    }
})

object HandHelper {
    fun of(vararg ints: Int): Hand {
        return fromCards(*ints.map { fromByte(it.toByte()) }.toTypedArray())
    }
}
