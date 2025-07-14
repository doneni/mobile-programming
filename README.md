# 짝 맞추기 게임 / Mobile Programming 팀 프로젝트
## 패키지 구조
```bash
com.example.game <br>
├── model
│   ├── GameManager
│   ├── Board
│   ├── Card
│   ├── ItemCard
│   ├── Player
│   ├── GameState
│   ├── Difficulty (Enum)
│   ├── CardType (Enum)
│   ├── ItemType (Enum)
│   └── itemeffects
│       ├── ItemEffect (인터페이스)
│       ├── TurnExtensionEffect
│       ├── DoubleScoreEffect
│       ├── RemoveOpponentItemEffect
│       ├── RevealCardEffect
│       ├── StealItemEffect
│       ├── BombMinusOneEffect
│       └── BombLoseAllItemsEffect
├── view
│   ├── MainActivity
│   ├── GameActivity
│   ├── ResultActivity
│   ├── ItemDialogFragment
│   ├── adapters
│   │   ├── CardAdapter
│   │   └── ItemAdapter
├── controller
│   └── GameController
├── network
│   ├── NetworkService (인터페이스)
│   ├── NetworkServiceImpl
│   └── DataReceivedListener (인터페이스)
├── events
    ├── GameEventListener
    ├── GameErrorListener
    └── OnItemSelectedListener
```
## 클래스 설명
### 1. Model 계층
- `GameManager`
  - 게임 핵심 로직 관리
    - 게임 상태, 플레이어 턴, 보드 상태 등
- `Board`
  - 게임 보드를 나타냄
    - 카드 위치, 상태 관리
- `Card`
  - 개별 카드를 나타냄
    - 카드의 종류, 식별자, 이미지 리소스 등
- `ItemCard`
  - 아이템 카드를 나타냄(Card 상속)
    - 아이템 타입(7종)
- `Player`
  - 플레이어 정보
    - 이름, 점수, 보유 아이템 목록
- `GameState`
  - 현재 게임 상태
    - 네트워크 통신 시 게임 상태 직렬화하여 전송
- `GameResult`
  - 게임 결과
    - 승자 정보, 플레이어들의 점수

### 1.1 Model 계층 Enum 클래스
- `Difficulty`
  - 게임 난이도 정의
  - `EASY`, `NORMAL`, `HARD`
- `CardType`
  - 카드 종류 정의
  - `NORMAL`, `ITEM`, `BOMB`
- `ItemType`
  - 아이템의 종류를 정의

### 1.2 Model 계층 하위 패키지
- `ItemEffect`
  - 아이템 효과 정의 인터페이스
  - 각 효과에 따른 구현 클래스 7개 존재

### 2. View 계층(Android Component)
- `MainActivity`
  - 앱의 진입점
    - 게임 모드 선택 및 설정 화면 제공
- `GameActivity`
  - 게임 진행화면 제공
    - 게임 보드, 플레이어 점수, 아이템 사용 버튼 등을 포함
- `ResultActivity`
  - 게임 결과 화면 제공
    - 승자 정보, 점수, 재시작 or 메인 화면으로 돌아감
    - 재시작 or 메인 화면으로 돌아가는 것은 임의로 추가해 보았습니다.
- `ItemDialogFragment`
  - 아이템 사용 선택을 위한 다이얼로그

### 2.1 View 계층 하위 패키지
- `adapters`
  - UI 요소의 데이터 바인딩을 위한 클래스
  - `CardAdapter`
    - 게임 보드의 카드 표시를 위한 어댑터
  - `ItemAdapter`
    - 아이템 목록 표시를 위한 어댑터

### 3. Controller 계층
- 사용자 입력 처리, 모델 업데이트, 모델 상태변화 뷰에 반영
- `GameEventListener`, `GameErrorListener`, `OnItemSelectedListener`, `DataReceivedListener` 인터페이스를 implement 함
- `GameController`
  - 모델과 뷰 사이 중재자
  - 게임 주요 로직 처리
  - 사용자 입력 -> 모델 업데이트
  - 모델 상태변화 -> 뷰에 전달

### 4. Network 계층(네트워크 통신 관련 패키지)
- 데이터의 전송과 수신을 담당하는 별도 계층
  - MVC 패턴의 어느 한 계층에 직접적으로 매핑되는 계층은 아님
- `NetworkService`
  - 네트워크 통신을 위한 메서드 정의
- `NetworkServiceImpl`
  - NetworkService 인터페이스 구현
- `DataReceivedListener`
  - 네트워크에서 데이터 수신 시 호출되는 콜백 메서드 정의

### 5. 이벤트 및 리스너 계층
- Model, View, Controller 사이의 이벤트 전달을 위한 매커니즘 제공
- `GameEventListener`
  - 게임 이벤트를 처리하기 위한 콜백 메서드 정의
- `GameErrorListener`
  - 게임 중 발생하는 에러나 예외 상황을 처리하는 콜백 메서드 정의
- `OnItemSelectedListener`
  - 아이템 선택 이벤트 처리를 위한 콜백 메서드 정의

## 클래스 다이어그램(약식)
```bash
[GameController] implements [GameEventListener], [GameErrorListener], [OnItemSelectedListener], [DataReceivedListener]
     |
     |-- Uses --> [GameManager]
     |               |
     |               |-- Contains --> [Board]
     |               |                   |
     |               |                   |-- Contains List of --> [Card]
     |               |                                    |
     |               |                                    |-- [ItemCard] extends [Card]
     |               |
     |               |-- Manages --> [Player]
     |                                    |
     |                                    |-- Holds List of --> [ItemEffect]
     |                                                    |
     |                                                    |-- Implemented by:
     |                                                       - [TurnExtensionEffect]
     |                                                       - [DoubleScoreEffect]
     |                                                       - [RemoveOpponentItemEffect]
     |                                                       - [RevealCardEffect]
     |                                                       - [StealItemEffect]
     |                                                       - [BombMinusOneEffect]
     |                                                       - [BombLoseAllItemsEffect]
     |
     |-- Communicates with --> [NetworkService] implemented by [NetworkServiceImpl]
     |                             |
     |                             |-- Notifies [DataReceivedListener] (GameController)
     |
     |-- Updates --> [GameActivity]
                          |
                          |-- Displays --> [Board], [Player], [ItemDialogFragment]
                          |
                          |-- Uses --> [CardAdapter], [ItemAdapter]
                          |
                          |-- Launches --> [ItemDialogFragment]
                                              |
                                              |-- Notifies [OnItemSelectedListener] (GameController)
```

## 전체적인 데이터 흐름 요약
- 사용자 입력은 View(`GameActivity`)에서 Controller(`GameController`)로 전달됨
- Controller는 Model(`GameManager`, `Board` 등)에 해당 요청을 전달, 게임 로직 처리
- Model은 상태 변화에 따라 이벤트 리스너(`GameEventListener`, `OnItemSelectedListener` 등) 를 통해 Controller에 이벤트 전달
- Controller는 받은 이벤트를 View에 전달하여 UI를 업데이트
- 네트워크 통신은 Controller와 `NetworkService`사이에서 이루어짐
  - `GameState`객체를 통해 게임 상태를 동기화

### 예시1: 카드 선택 시의 흐름
1. 사용자가 `GameActivity`에서 카드를 선택
2. `GameActivity`는 `GameController.onCardSelected(position)`을 호출
3. `GameController`는 `GameManager.flipCard(position)`을 호출
4. `GameManager`는 `Board.flipCard(position)`을 호출하여 카드 상태를 변경
5. `Board`는 카드 상태를 변경하고 결과를 `GameManager`에 반환
6. `GameManager`는 카드가 정상적으로 뒤집혔다면 `GameEventListener.onCardFlipped(position, card)`를 호출
7. `GameController`는 `GameActivity.updateCard(position, card)`를 호출하여 UI를 업데이트
8.  추가적으로, 카드 짝이 맞는다면 `GameEventListener.onMatchFound(position1, position2)`가 호출.
9. `GameController`는 `GameActivity.showMatch(position1, position2)`를 호출.


### 예시2: 아이템 사용 시의 흐름
1. 사용자가 `GameActivity`에서 아이템 사용 버튼을 누름
2. `GameActivity`는 `GameController.onItemUseRequested()`를 호출
3. `GameController`는 `GameActivity.showItemDialog(items)`를 호출하여 다이얼로그를 표시
4. 사용자가 `ItemDialogFragment`에서 아이템을 선택
5. `ItemDialogFragment`는 `OnItemSelectedListener.onItemSelected(itemType)`을 호출
6. `GameController`는 현재 플레이어의 `Player.useItem(itemType, gameManager)`를 호출
7. `Player`는 해당 `ItemEffect.applyEffect(gameManager, player)`를 호출
8. 아이템 효과가 적용되면 `GameManager`는 `GameEventListener`를 통해 이벤트를 발생
9. `GameController`는 이벤트를 받아 `GameActivity`의 UI를 업데이트


### 예시3: 네트워크 데이터 수신 시 흐름
1. 상대방이 게임 상태를 변경하고 `NetworkService.sendData(gameState)`를 호출
2. `NetworkServiceImpl`은 데이터를 네트워크를 통해 전송
3. 로컬의 `NetworkServiceImpl`은 데이터를 수신하고 `DataReceivedListener.onDataReceived(gameState)`를 호출
4. `GameController`는 `GameManager.updateGameState(gameState)`를 호출하여 게임 상태를 업데이트
5. `GameControlle`r는 `GameActivity.refreshUI()`를 호출하여 UI를 갱신


## 클래스 간 데이터 흐름
- 클래스 박스 : `[ClassName]`
- 상속 관계: `[Subclass] ---|> [Superclass]`
- 인터페이스 구현: `[ClassName] ---| implements InterfaceName`
- 데이터 흐름: `ClassA <--> ClassB : 데이터/메서드`
- 이벤트 리스터: `ClassA <---> ClassB : 이벤트/콜백`
```bash
[ 사용자 ]
     |
     V
[ GameActivity ] <--------------------------------------------------------------+
     |                                                                          |
     | onCardClicked(position)                                                  |
     V                                                                          |
[ GameController ] <-------------------------------------------------------+    |
     |                             ^                                     ^ |    |
     | onCardSelected(position)    |                                     | |    |
     V                             |                                     | |    |
[ GameManager ]                    |                                     | |    |
     |                             |                                     | |    |
     | flipCard(position)          |                                     | |    |
     V                             |                                     | |    |
[ Board ]                          |                                     | |    |
     |                             |                                     | |    |
     | flipCard(position)          |                                     | |    |
     V                             |                                     | |    |
[ Card ]                           |                                     | |    |
     |                             |                                     | |    |
     | flip()                      |                                     | |    |
     V                             |                                     | |    |
[ Card ] -------------------+      |                                     | |    |
     ^                      |      |                                     | |    |
     | getCardAt(position)  |      |                                     | |    |
     |                      V      |                                     | |    |
[ Board ]                   |      |                                     | |    |
     ^                      |      |                                     | |    |
     |                      |      |                                     | |    |
[ GameManager ]             |      |                                     | |    |
     |                      |      |                                     | |    |
     |  onCardFlipped(position, card) -- GameEventListener --------------+ |    |
     |                                                                          |
     V                                                                          |
[ GameController ] -------------------------------------------------------------+
     |
     | updateCard(position, card)
     V
[ GameActivity ] ---------------------------------------------------------------+
     |                                                                          |
     | updateCard(position, card)                                               |
     V                                                                          |
[ CardAdapter ]                                                                 |
                                                                                |
[ 사용자 ]                                                                       |
     |                                                                          |
     | 아이템 사용 버튼 클릭                                                       |
     V                                                                          |
[ GameActivity ] ---------------------------------------------------------------+
     |                                                                          |
     | onItemUseRequested()                                                     |
     V                                                                          |
[ GameController ] -------------------------------------------------------------+
     |                                                                          |
     | showItemDialog(items)                                                    |
     V                                                                          |
[ GameActivity ] ---------------------------------------------------------------+
     |                                                                          |
     | ItemDialogFragment 생성 및 표시                                            |
     V                                                                          |
[ ItemDialogFragment ] ---------------------------------------------------------+
     |                                                                          |
     | 사용자 아이템 선택                                                          |
     V                                                                          |
[ ItemDialogFragment ]                                                          |
     |                                                                          |
     | onItemSelected(itemType)                                                 |
     V                                                                          |
[ GameController ] -------------------------------------------------------------+
     |                                                                          |
     | useItem(itemType, gameManager)                                           |
     V                                                                          |
[ Player ]                                                                      |
     |                                                                          |
     | applyEffect(gameManager, player)                                         |
     V                                                                          |
[ ItemEffect ]                                                                  |
     |                                                                          |
     | 효과에 따른 게임 상태 변경                                                   |
     V                                                                          |
[ GameManager ]                                                                 |
     |                                                                          |
     | onGameStateUpdated() -- GameEventListener -------------------------------+
     |                                                                          |
     V                                                                          |
[ GameController ]                                                              |
     |                                                                          |
     | refreshUI()                                                              |
     V                                                                          |
[ GameActivity ] ---------------------------------------------------------------+
     |                                                                          |
     | UI 갱신                                                                   |
     V                                                                          |
[ 사용자 ]                                                                       |

[ GameController ] <------------------------------------------------------------+
     |                                                                          |
     | sendData(gameState)                                                      |
     V                                                                          |
[ NetworkService ]                                                              |
     |                                                                          |
     | 데이터 직렬화 및 전송                                                       |
     V                                                                          |
[ Network ]                                                                     |
     |                                                                          |
     | 데이터 수신                                                                |
     V                                                                          |
[ 상대방의 NetworkService ]                                                      |
     |                                                                          |
     | onDataReceived(gameState) -- DataReceivedListener -----------------------+
     V                                                                          |
[ 상대방의 GameController ]                                                      |
     |                                                                          |
     | updateGameState(gameState)                                               |
     V                                                                          |
[ 상대방의 GameManager ]                                                         |
     |                                                                          |
     | onGameStateUpdated() -- GameEventListener -------------------------------+
     V                                                                          |
[ 상대방의 GameController ]                                                      |
     |                                                                          |
     | refreshUI()                                                              |
     V                                                                          |
[ 상대방의 GameActivity ]                                                         |
     |                                                                          |
     | UI 갱신                                                                   |
     V                                                                          |
[ 상대방의 사용자 ]                                                                |

[ GameManager ] <---------------------------------------------------------------+
     |                                                                          |
     | setGameEventListener(gameController)                                     |
     | setGameErrorListener(gameController)                                     |
     V                                                                          |
[ GameController ]                                                              |
                                                                                |
[ GameManager ] <---------------------------------------------------------------+
     |                                                                          |
     | setDataReceivedListener(gameController)                                  |
     V                                                                          |
[ NetworkService ]                                                              |

[ Player ] <--------------------------------------------------------------------+
     |                                                                          |
     | getItems()                                                               |
     V                                                                          |
[ GameManager ]                                                                 |
     |                                                                          |
     | getCurrentPlayer()                                                       |
     V                                                                          |
[ GameController ]                                                              |

[ ItemEffect ] <----------------------------------------------------------+     |
     |                                                                   |     |
     | getItemType()                                                     |     |
     |                                                                   |     |
     +-------------------------------------------------------------------+     |
                                                                                |
[ Player ] <--------------------------------------------------------------------+
     |                                                                          |
     | addItemEffect(itemEffect)                                                |
     V                                                                          |
[ GameManager ]                                                                 |
     |                                                                          |
     | 아이템 획득 로직                                                           |
     V                                                                          |
[ ItemCard ]                                                                    |

[ GameManager ] <---------------------------------------------------------------+
     |                                                                          |
     | getGameResult()                                                          |
     V                                                                          |
[ GameResult ]                                                                  |

[ GameActivity ] ---------------------------------------------------------------+
     |                                                                          |
     | navigateToResultActivity(gameResult)                                     |
     V                                                                          |
[ ResultActivity ]                                                              |
```

```bash
[ 사용자 ]
     |
     | 터치 이벤트, 버튼 클릭 등
     V
[ GameActivity ] --------------------------------+
     |                                           |
     | onCardClicked(position)                   |
     | onItemUseButtonClicked()                  |
     V                                           |
[ GameController ] implements GameEventListener, GameErrorListener, OnItemSelectedListener, DataReceivedListener
     |                                           ^
     | 게임 로직 처리 요청                          |
     V                                           |
[ GameManager ] ----------------------------------+
     |                                           |
     | flipCard(position)                        |
     | processItemEffect(itemEffect)             |
     | updateGameState(gameState)                |
     V                                           |
[ Board ]                                        |
     |                                           |
     | flipCard(position)                        |
     V                                           |
[ Card ] / [ItemCard ]                           |
     |                                           |
     | flip()                                    |
     V                                           |
[ Card ] 상태 변경                                 |
     |                                           |
     | 매치 검사 및 아이템 획득 여부 확인              |
     V                                           |
[ GameManager ]                                  |
     |                                           |
     | 게임 이벤트 발생                             |
     |--Event--> [GameEventListener]             |
     |                                           |
     | 게임 에러 발생 시                            |
     |--Event--> [GameErrorListener]             |
     V                                           |
[ GameController ] -------------------------------+
     |                                           |
     | 이벤트 처리 및 뷰 업데이트 요청                |
     V                                           |
[ GameActivity ] --------------------------------+
     |                                           |
     | updateCard(position, card)                |
     | showMatch(position1, position2)           |
     | showItemAcquired(itemCard)                |
     | updatePlayerItems(items)                  |
     | updateCurrentPlayer(player)               |
     | navigateToResultActivity(gameResult)      |
     V                                           |
[ CardAdapter ] / [ItemAdapter ]                 |
     |                                           |
     | UI 갱신                                    |
     V                                           |
[ 화면 ]                                          |
                                                 |
[ GameActivity ]                                 |
     |                                           |
     | showItemDialog(items)                     |
     V                                           |
[ ItemDialogFragment ] --------------------------+
     |                                           |
     | 사용자 아이템 선택                           |
     | onItemSelected(itemType)                  |
     |--Event--> [OnItemSelectedListener]        |
     V                                           |
[ GameController ] -------------------------------+
     |                                           |
     | 아이템 사용 처리                             |
     V                                           |
[ Player ] --------------------------------------+
     |                                           |
     | useItem(itemType, gameManager)            |
     V                                           |
[ ItemEffect ] (인터페이스)                        |
     |                                           |
     | applyEffect(gameManager, player)          |
     V                                           |
[ 구현된 ItemEffect 클래스들 ]                      |
     |                                           |
     | 효과에 따른 게임 및 플레이어 상태 변경          |
     V                                           |
[ GameManager ] ----------------------------------+
     |                                           |
     | 게임 상태 변경 및 이벤트 발생                  |
     |--Event--> [GameEventListener]             |
     V                                           |
[ GameController ] -------------------------------+
     |                                           |
     | 뷰 업데이트 요청                             |
     V                                           |
[ GameActivity ] --------------------------------+
                                                 |
[ GameController ] <--> [ NetworkService ]       |
     |                    implements NetworkService
     | sendData(gameState)                       |
     | setDataReceivedListener(this)             |
     V                                           |
[ NetworkServiceImpl ]                           |
     |                                           |
     | 데이터 직렬화 및 전송                        |
     | 수신된 데이터 역직렬화 및 전달                 |
     V                                           |
[ 상대방의 NetworkService ]                       |
     |                                           |
     | onDataReceived(gameState)                 |
     |--Event--> [DataReceivedListener]          |
     V                                           |
[ 상대방의 GameController ] ----------------------+
     |                                           |
     | updateGameState(gameState)                |
     V                                           |
[ 상대방의 GameManager ]                          |
     |                                           |
     | 게임 상태 업데이트 및 이벤트 발생              |
     |--Event--> [GameEventListener]             |
     V                                           |
[ 상대방의 GameController ] ----------------------+
     |                                           |
     | 뷰 업데이트 요청                            |
     V                                           |
[ 상대방의 GameActivity ]                          |
     |                                           |
     | UI 갱신                                    |
     V                                           |
[ 화면 ]                                          |
                                                 |
[ GameManager ]                                  |
     |                                           |
     | getGameResult()                           |
     V                                           |
[ GameResult ]                                   |
     |                                           |
     | 게임 결과 정보 (승자, 점수 등)                |
     V                                           |
[ GameController ]                               |
     |                                           |
     | navigateToResultActivity(gameResult)      |
     V                                           
[ GameActivity ]                                 |
     |                                           |
     | ResultActivity 호출                        |
     V                                           |
[ ResultActivity ]                               |
     |                                           |
     | 결과 화면 표시                              |
     V                                           |
[ 화면 ]                                        |

```
