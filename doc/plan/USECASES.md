# API Use Cases and Implementation

This document outlines how the game engine APIs are designed to accomplish various use cases,
including starting a new game, adding or removing obstacles, handling player actions, managing the
camera, determining win/lose states, and saving levels.

## BUILDER-01: New Game

Display.newGame(fileName)

Public void newGame() {  
CurrentScene.newScene()  
}

Public void newScene() {  
currentScene = new GameScene()  
}

## BUILDER-02: Add/Remove Obstacle

GameScene scene = new GameScene  
GameObject newObject = new GameObject  
scene.addObject(newObject)  
scene.removeObject(newObject)

## PLAYER-05: New Game

Class Display   
{  
Parser parser = new Parser  
GameScene scene = parser.parse(FilePath) // returns GameScene object  
scene.step()  
}

Class GameScene   
{  
public void step()   
{  
for (Object gameObject : gameObjects)   
{  
gameObject.step()  
}  
}  
}

*We’ll have previous pointers to the content of the last step as well as a current pointer.*

## PLAYER-01: Pause

Boolean pause  
EventHandler escapeKey = new EventHandler()  
onClick -> CurrentScene.loadScene("PauseMenu")

## PLAYER-08: Main Camera

Class Display {

    setFocus(currentLevel.getPlayer()) {  
        // Sets the camera focus to the player  
        // Updates what would be the global coordinates of the display window  
    }

}

## PLAYER-12 / PLAYER-16: Lose/Win Game

Class Object

step() {  
// Call step for all components  
if (collision) {  
if (collision.hitObject.hasComponent(HurterComponent)) {  
if (this.hasComponent(HealthComponent)) {  
HealthComponent.setHealth(HealthComponent.getHealth() - HurterComponent.getDamage())  
}  
}  
}  
}

*Maybe add an interaction class to manage interactions between components.*

Class HealthComponent extends Component  
{  
getHealth()  
setHealth()  
}

Class HurterComponent extends Component  
{  
int damage  
getDamage()  
}

Class GameScene {  
gameStatus() {  
if (player.HealthComponent.getHealth() <= 0) return LOSE  
for (obj : winObjects) if (obj.WinComponent.active()) return WIN  
return PLAYING  
}  
}

Class Display {

    if (GameScene.gameStatus() == LOSE) {  
        Display.loadScene("Lose Screen")  
    } else if (GameScene.gameStatus() == WIN) {  
        Display.loadScene("Win Screen")  
    }

}

## BUILDER-03: Save Level

Class Editor {  
saveButton.addEventListener(onClick -> levelWriter.write(gameScene))  
}

Class Writer {  
public void write(GameScene scene) {  
// Construct JSON file  
}  
}
