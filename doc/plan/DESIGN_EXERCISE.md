# Fluxx Lab Discussion

## Lsd25, Jga18, clc162, dr285, rts34, Cgb45, jfr29

### High-Level Design Ideas

The design of the Fluxx card game software must accommodate the dynamically changing rules and
objectives inherent to the game. The system should be highly modular and flexible to handle:

- A changing set of rules that can affect game mechanics at any time.
- Various types of cards (e.g., Rule, Goal, Action, Keeper, Creeper, and Ungoal cards) that impact
  gameplay dynamically.
- The ability to modify win conditions mid-game.
- Support for multiple themes with different sets of cards and unique interactions.
- A turn-based structure where player actions can affect the state of the game drastically.

---

### CRC Card Classes

#### `Game`

```java
public class Game {
    private RuleSet rules;
    private Deck deck;
    private List<Player> players;
    private Goal currentGoal;
    
    public void startGame();
    public void playTurn(Player player);
    public void updateRules(RuleCard rule);
    public void updateGoal(GoalCard goal);
    public void checkWinCondition();
}
```

#### `Player`

```java
public class Player {
    private String name;
    private List<Card> hand;
    
    public void drawCard(Deck deck);
    public void playCard(Card card, Game game);
    public void discardExcessCards(RuleSet rules);
}
```

#### `Card`

```java
public abstract class Card {
    private String name;
    public abstract void applyEffect(Game game, Player player);
}
```

#### `GoalCard`

```java
public class GoalCard extends Card {
    private Condition winCondition;
    
    @Override
    public void applyEffect(Game game, Player player);
}
```

#### `RuleCard`

```java
public class RuleCard extends Card {
    private Rule newRule;
    
    @Override
    public void applyEffect(Game game, Player player);
}
```

#### `ActionCard`

```java
public class ActionCard extends Card {
    private Action action;
    
    @Override
    public void applyEffect(Game game, Player player);
}
```

#### `Deck`

```java
public class Deck {
    private List<Card> cards;
    
    public Card drawCard();
    public void shuffle();
}
```

#### `RuleSet`

```java
public class RuleSet {
    private List<Rule> activeRules;
    
    public void addRule(Rule newRule);
    public void enforceRules(Player player);
}
```

---

### Use Cases

#### A player plays a Goal card, changing the current goal, and wins the game.

```java
GoalCard goal = new GoalCard("New Win Condition");
game.updateGoal(goal);
game.checkWinCondition();
```

#### A player plays an Action card, allowing them to choose cards from another player's hand and play them.

```java
ActionCard action = new ActionCard("Steal a Card");
action.applyEffect(game, currentPlayer);
```

#### A player plays a Rule card, adding to the current rules to set a hand-size limit, requiring all players to immediately drop cards from their hands if necessary.

```java
RuleCard handLimitRule = new RuleCard("Max Hand Size: 3");
game.updateRules(handLimitRule);
game.enforceRules();
```

#### A player plays a Rule card, changing the current rule from Play 1 to Play All, requiring the player to play more cards this turn.

```java
RuleCard playAllRule = new RuleCard("Play All");
game.updateRules(playAllRule);
currentPlayer.playCard(hand.get(0), game);
```

#### A player plays a card, fulfilling the current Ungoal, and everyone loses the game.

```java
UngoalCard ungoal = new UngoalCard("Everyone Loses");
ungoal.applyEffect(game, currentPlayer);
```

#### A new theme for the game is designed, creating a different set of Rule, Keeper, and Creeper cards.

```java
Theme newTheme = new Theme("Pirate Fluxx");
game.setTheme(newTheme);
deck.initializeFromTheme(newTheme);