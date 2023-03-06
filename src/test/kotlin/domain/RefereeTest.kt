package domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RefereeTest {
    @Test
    fun `딜러는 18점, 유저1, 유저2의 점수는 19, 15점이다`() {
        // given
        val dealerScore = Score.valueOf(18)
        val userScore = listOf(Score.valueOf(19), Score.valueOf(15))

        // when
        val actual = Referee(dealerScore, userScore).getResult()
        val expected = listOf<GameResult>(GameResult.WIN, GameResult.LOSE)

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `딜러는 22점, 유저1, 유저2의 점수는 19, 15점이다`() {
        // given
        val dealerScore = Score.valueOf(22)
        val userScore = listOf(Score.valueOf(19), Score.valueOf(15))

        // when
        val actual = Referee(dealerScore, userScore).getResult()
        val expected = listOf<GameResult>(GameResult.WIN, GameResult.WIN)

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `딜러는 22점, 유저1, 유저2의 점수는 119, 15점이다`() {
        // given
        val dealerScore = Score.valueOf(22)
        val userScore = listOf(Score.valueOf(119), Score.valueOf(15))

        // when
        val actual = Referee(dealerScore, userScore).getResult()
        val expected = listOf<GameResult>(GameResult.DRAW, GameResult.WIN)

        // then
        assertThat(actual).isEqualTo(expected)
    }
}
