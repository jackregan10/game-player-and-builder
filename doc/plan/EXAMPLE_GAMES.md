## Example Game 1: Doodle Jump

Doodle Jump is a vertical scrolling platformer in which a user controls a character that is
continuously falling while using blocks to propel itself upwards. The goal of the game is to get as
high as possible while avoiding enemies and obstacles.

### Core Mechanics:

- The character is constantly aiming to jump upward, while platforms dynamically appear.
- Items like trampolines or jetpacks spawn as power-ups for faster movement.
- Enemies or obstacles persist and need to be either avoided or destroyed.

## Example Game 2: Dinosaur Run

Dinosaur Run is a horizontal platformer in which the user controls the up and down movement of the
character. The character advances horizontally continuously and must avoid obstacles. If the
character collides with an obstacle, the user loses health until they either complete the goal of
the level (survive for the length of the timer) or die (lose all their health).

### Core Mechanics:

- A user increases their score for every second they remain alive.
- The user should be able to restart the level or pause the game at any point during gameplay.
- Obstacles are fixed to the moving platform and are touching the floor of the platform.

### Rationale for Inclusion:

We chose to include this game within our planning because the significant difference with Game
Example 1 is its physics component determined by the horizontal gameplay. This creates a shared
physics component through gravity but requires a unique implementation and user input.

---

## Abstractions

### **PlayerController**

- Needed to abstract the player’s movement of jumping, falling, and lateral movements.
- Central for movement and overall game physics.

### **PlatformManager**

- Determines platform generation, positioning, and removal.
- Ensures infinite level progression and scoring capabilities.

### **CollisionDetector**

- Detects collisions with landing on a platform, hitting an enemy, or collecting a power-up.
- Defines much of the in-game interactions.

### **PowerUps**

- Maintains logic for defining and activating power-ups.
- Adds variety to the game and allows for easy maintenance of different power-ups.

### **ObstacleManager**

- Handles logic for enemy and obstacle definition and behavior.
- Introduces challenges to prevent infinite survival.

### **ScrollingManager**

- Handles logic regarding camera movement and screen scrolling.
- Provides the core mechanic of moving the screen sideways.

### **GameLoopManager**

- Manages progression of the animation through time.
- Ensures fluid game progression.

---

## Functional Commonalities & Differences

### **Universal Behaviors**

- **User Score** - Tracks the user's current score, updated based on survival time. The API may
  implement unique scoring logic.
- **User High Score** - Displays the highest score achieved on a level.
- **End Game Screen** - Displays a conclusion to the gameplay.
- **User Health** - Represents the character’s health.
- **User Pause Button** - Allows the user to pause the game and change some game rules.
- **User Restart Button** - Restarts the game level.
- **Character Collision** - Determines outcomes of character interactions with obstacles.
- **Character Physics** - Represents the effects of gravity on character movements.
- **Sliding Game Camera** - Keeps the character centered in view, though the sliding direction
  differs between the games.

### **Unique Behaviors**

- **Horizontal Character Movement** - Used in Example Game 2 for horizontal platform scrolling.
- **Up and Down User Input** - Used in Example Game 2 for jumping.
- **Left to Right User Input** - Used in Example Game 1 for directional movement.
- **Power-ups** - Different between the two games:
    - In *Doodle Jump*, some power-ups automatically boost the character upwards.
    - In *Dinosaur Run*, power-ups might provide temporary invincibility.
- **Game Over Condition**
    - In *Doodle Jump*, the game ends when the user falls off the screen.
    - In *Dinosaur Run*, the game ends when health reaches zero.
