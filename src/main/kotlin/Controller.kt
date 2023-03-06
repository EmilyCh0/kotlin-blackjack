import domain.Answer
import domain.Answer.Companion.YES
import domain.Cards
import domain.Dealer
import domain.Deck
import domain.GameResult
import domain.Referee
import domain.Score
import domain.User
import domain.UserNameContainer
import domain.card.Card
import view.GameResultView
import view.LoginView
import view.PlayGameView

class Controller(
    private val loginView: LoginView = LoginView(),
    private val playGameView: PlayGameView = PlayGameView(),
    private val gameResultView: GameResultView = GameResultView(),
    private val deck: Deck = Deck.create(NUMBER_OF_CARD_SET),
) {

    fun run() {
        val userNames = readUserNames()
        val dealerCards = deck.getCardPair()
        val userCards = deck.getCardPairs(userNames.size)
        val users = createUsers(userNames, userCards)
        val dealer = Dealer(cards = Cards(dealerCards))

        playGame(userNames, dealer, users)
        gameEnd(dealer, users)
    }

    private fun readUserNames(): List<String> {
        val userNameContainer = repeatWithRunCatching { getUserNames() }
        return userNameContainer.names
    }

    private fun getUserNames(): UserNameContainer = UserNameContainer(loginView.requestPlayerName())

    private fun createUsers(userNames: List<String>, userCards: List<List<Card>>): List<User> {
        val userNamesAndCards = userNames.zip(userCards)
        return userNamesAndCards.map { userNameAndCard ->
            User.create(userNameAndCard)
        }
    }

    private fun playGame(userNames: List<String>, dealer: Dealer, users: List<User>) {
        playGameView.printNoticeSplitCard(userNames)
        playGameView.printPlayerCard(dealer, users)

        requestGetCommand(users)
        dealerPickNewCardIfNeeded(dealer)
    }

    private fun requestGetCommand(users: List<User>) {
        users.forEach { user ->
            repeatGetCommand(user)
        }
    }

    private fun repeatGetCommand(user: User) {
        val answer = getAnswer(user)
        if (answer.value == YES) return userPickNewCard(user)
        return userPickNoCard(user)
    }

    private fun userPickNewCard(user: User) {
        user.cards.addCard(deck.getNewCard())
        playGameView.printUserCard(user)
        repeatGetCommand(user)
    }

    private fun userPickNoCard(user: User) {
        playGameView.printUserCard(user)
    }

    private fun getAnswer(user: User): Answer =
        repeatWithRunCatching { Answer(playGameView.requestOneMoreCard(user)) }

    private fun dealerPickNewCardIfNeeded(dealer: Dealer) {
        if (!dealer.isOverSumCondition()) {
            playGameView.printDealerPickNewCard()
            val newCard = deck.getNewCard()
            dealer.cards.addCard(newCard)
        }
    }

    private fun gameEnd(dealer: Dealer, users: List<User>) {
        gameResultView.printCardResult(dealer, users)
        gameResultView.printFinalResult(getGameResult(dealer, users), users)
    }

    private fun getGameResult(dealer: Dealer, users: List<User>): List<GameResult> {
        val referee: Referee = Referee(
            Score.valueOf(dealer.cards.actualCardValueSum()),
            users.map { user ->
                Score.valueOf(user.cards.actualCardValueSum())
            },
        )
        return referee.getResult()
    }

    private fun <T> repeatWithRunCatching(action: () -> T): T {
        return runCatching(action).getOrElse { error ->
            println(error.message.toString())
            repeatWithRunCatching(action)
        }
    }

    companion object {
        private const val NUMBER_OF_CARD_SET = 1
    }
}
