package kr.co.example.mobileprogramming.model;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import kr.co.example.mobileprogramming.events.GameErrorListener;
import kr.co.example.mobileprogramming.events.GameEventListener;
import kr.co.example.mobileprogramming.model.itemeffects.ItemEffect;

public class GameManager {
    private int currentRound;
    private int totalRounds;
    private Difficulty difficulty;
    private Board board;
    private Player player1;
    private Player player2;  // null in single-player mode
    private Player currentPlayer;
    private GameState gameState;
    private List<Pair<Integer, Card>> selectedCards;
    private boolean extendTurnFlag = false;
    private GameEventListener gameEventListener;
    private GameErrorListener gameErrorListener;

    public GameManager(Difficulty difficulty, int totalRounds, int currentRound, Player player1, Player player2) {
        this.difficulty = difficulty;
        this.totalRounds = totalRounds;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
        this.currentRound = currentRound;
        this.selectedCards = new ArrayList<>();

        this.gameState = new GameState(null, player1, player2, currentPlayer, currentRound);
    }

    public void startGame() {
        if (gameEventListener != null) {
            gameEventListener.onGameStarted();
        }
    }

    public void initializeBoard(List<Card> boardCards) {
        this.board = new Board(boardCards);
    }

    public boolean selectCard(int position) {
        Card selectedCard = board.getCardAt(position);

        if (selectedCard == null || selectedCard.isFlipped() || selectedCards.size() >= 2) {
            return false;
        }

        selectedCards.add(new Pair<>(position, selectedCard));
        selectedCard.flip();
        if (gameEventListener != null) {
            gameEventListener.onCardFlipped(position, selectedCard);
        }

        if (selectedCards.size() == 2) {
            processTurn();
        }

        return true;
    }

    private void processTurn() {
        if (selectedCards.size() != 2) return;

        Card card1 = selectedCards.get(0).second;
        Card card2 = selectedCards.get(1).second;

        if (card1.getId() == card2.getId()) {
            // 매칭 성공
            currentPlayer.addCorrect();
            card1.setMatched(true);
            card2.setMatched(true);

            int scoreToAdd = 1; // 기본은 일반 카드 매칭시 1점
            if (card1.getType() == CardType.ITEM) {
                // 아이템 카드라면 3점 부여
                scoreToAdd = 3;
                ItemCard itemCard = (ItemCard) card1;
                ItemEffect itemEffect = itemCard.createItemEffect();
                currentPlayer.addItemEffect(itemEffect);
                if (gameEventListener != null) {
                    gameEventListener.onItemAcquired(itemCard);
                }
            }
            currentPlayer.addScore(scoreToAdd);

            if(currentPlayer.getName().equals("Player 1")) {
                gameState.setPlayer1(currentPlayer);
            }
            else {
                gameState.setPlayer2(currentPlayer);
            }
            if (gameEventListener != null) {
                gameEventListener.onMatchFound(selectedCards.get(0).first, selectedCards.get(1).first);
            }
            selectedCards.clear();
        } else {
            // 매칭 실패
            currentPlayer.addWrong();
            new android.os.Handler().postDelayed(() -> {
                if (selectedCards.isEmpty()) {
                    return;
                }

                card1.setFlipped(false);
                card2.setFlipped(false);
                Log.d("hi", "card1 " + card1.isFlipped() + " card2 " + card2.isFlipped());

                if(currentPlayer.getName().equals("Player 1")) {
                    gameState.setPlayer1(currentPlayer);
                }
                else {
                    gameState.setPlayer2(currentPlayer);
                }

                if (gameEventListener != null) {
                    Log.d("hi", "flip back");
                    gameEventListener.onCardFlipped(selectedCards.get(0).first, card1);
                    gameEventListener.onCardFlipped(selectedCards.get(1).first, card2);
                }

                // 2인용이면 턴 전환
                if (player2 != null) {
                    switchTurn();
                }

                selectedCards.clear();
            }, 500);
        }
    }

    private void switchTurn() {
        Log.d("hi", "switchturn" + gameState.getCurrentPlayer().getName());
        if(extendTurnFlag) {
            extendTurnFlag = false;
            return;
        }

        if (player2 != null) {
            if(currentPlayer.getName().equals("Player 1")) {
                gameState.setPlayer1(currentPlayer);
                gameState.setCurrentPlayer(player2);
            }
            else {
                gameState.setPlayer2(currentPlayer);
                gameState.setCurrentPlayer(player1);
            }
            if (gameEventListener != null) {
                gameEventListener.onTurnChanged(currentPlayer);
            }
        } else {
            // 1인용 모드는 턴 전환 없음
            currentPlayer = player1;
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }

    public void notifyCardFlipped(int position, Card card) {
        if (gameEventListener != null) {
            gameEventListener.onCardFlipped(position, card);
        }
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public void nextRound() {
        if (currentRound < totalRounds) {
            currentRound++;
        }
    }

    public GameResult getGameResult() {
        int player1Score = player1.getScore();
        int player2Score = (player2 != null) ? player2.getScore() : 0;
        Player winner = player1;
        if (player2 != null && player2Score > player1Score) {
            winner = player2;
        }
        return new GameResult(winner, player1Score, player2Score);
    }

    public boolean isAllCardsMatched() {
        for (Card card : board.getCards()) {
            if (!card.isMatched()) {
                return false;
            }
        }
        return true;
    }

    public GameState getGameState() {
        return gameState;
    }
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
    public void updateGameState() {
        if(gameState != null) {
            gameState.setBoard(board);
            gameState.setCurrentPlayer(currentPlayer);
            gameState.setCurrentRound(currentRound);
        }
    }
    public Board getBoard() {
        return board;
    }

    public void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = listener;
    }

    public void setGameErrorListener(GameErrorListener listener) {
        this.gameErrorListener = listener;
    }

    public void setExtendTurnFlag(boolean extendTurnFlag) {
        this.extendTurnFlag = extendTurnFlag;
    }

    public void updateGameState(GameState gameState) {
        Log.d("hi", "updateGameState " + gameState.getCurrentPlayer().getName());
        this.board = gameState.getBoard();
        this.player1 = gameState.getPlayer1();
        this.player2 = gameState.getPlayer2();
        this.currentPlayer = gameState.getCurrentPlayer();
        this.currentRound = gameState.getCurrentRound();

        // 상태 변경 후 UI 업데이트를 위한 이벤트 호출
        if (gameEventListener != null) {
            gameEventListener.onGameStateUpdated();
        }
    }

    public GameState toGameState() {
        return new GameState(board, player1, player2, currentPlayer, currentRound);
    }

    public Player getOpponent(Player currentPlayer) {
        if (player2 == null) {
            return null;
        }
        return (currentPlayer == player1) ? player2 : player1;
    }

    public List<Pair<Integer, Card>> getSelectedCards() {
        return selectedCards;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setPlayers(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}
