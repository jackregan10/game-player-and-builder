# Strategy 1: Exceptions for Parser/Writing

# Strategy 2: Checking for Multiple Players

## API Tests

1. (Negative Test): load JSON file that break JSON rules

Json Files made outside of the editor could be ill formatted

Expected Outcome: Parser API would throw an exception causing a pop-up on the view to appear stating
“Invalid json file” \- Make these messages as descriptive as possible

How are model supports testing: User can try to load a JSON file that breaks JSON syntax

2. (Negative Test): load JSON file that does not follow our specified list of Objects with
   components structure

Expected Outcome: Parser API would throw an exception causing a pop-up on the view to appear stating
“Invalid json file” \- Make these messages as descriptive as possible

How are model supports testing: User can upload a JSON file that has multiple player objects or
contains other inconsistencies.

3. Load Json File with invalid component parameters (ex. Negative max health)

Expected Outcome: Throws an exception (if it is not the player it might be recoverable by just
running anyway)

4. Load Json File with overlapping components that should block each other

Expected Outcome: Exception is thrown and a pop-up happens

5. Load Json File where no object has a win component

Expected Outcome: Exception is thrown

6. Load Json File where an object has multiple copies of the same component   
   Expected Outcome, Exception is thrown

7. Load Json file where the player object is assigned the win component  
   Expected outcome: Exception is thrown

8. : load correctly formatted JSON file. Compare to a premade correct GameScene object

Expected Outcome: equality check returns true (An overloaded equality operator in GameScene will be
needed so it does not only check pointers)

9. : Write correctly formatted GameScene Object

Expected Outcome: First test that the Reader works and then you can simply compare the original
object to the one returned by reading the written file

10. Write GameScene object of already existing File

Expected Outcome: The writer will overwrite the old file

11. : Test if gameStatus=“LOSE” after player health is \<= zero

Expected Outcome: set players health to zero. Then assertEquivalency(“LOSE”,
GameScene.getGameStatus) should return true.

How our model supports testing: setHealth function in the health components makes it easy to test
the behavior of GameScene.

12. : ColliderComponent step function

Expected Outcome: Artificially place it in a Game object and then test that it returns a collision
object with the with the hit object set correctly

13. : PhysicsHandler step

Expected Outcome: check that it correctly behaves depending on it physics is enabled/disabled

14. : GameObject.removeComponent(String className)

Expected Outcome: The corresponding component should be removed or nothing happens if the component
was not there

15. : GameObject.addComponent(String className)

Expected Outcome: The component is added if it was not there or nothing happens if it was already
present

16. GameObject.addComponent(String className) Invalid Class Name

Expected Outcome: An exception is thrown

17. : GameObject.removeComponent(String className)

Expected Outcome: An exception is thrown

18. : Test the focus method in display

Expected Outcome: We will have a pointer to the currentCenter point in our global coordinate system.
The test will check to see if the player’s coordinates equal the current center. It should return
true.

We will have a global coordinate system for the entire level and only show a part of it on the
screen. There will be a pointer to the center point which will gauge where the center of the screen
is as the scrolling occurs.

19. : InputAction execute

Expected Outcome: All of the callback functions in the IA are called

20. InputAction addCallback

Expected Outcome: The function should be added to the IAs list of callbacks. This can be tested by
checking the list.

21. : Mapping Context execute action

Expected Outcome: The execute function for the correct IA is called

22. : Game Scene add Object

Expected Outcome: A new Object is added to the Game Scene

23. : Game Scene remove Object

Expected Outcome: An object is removed from the Game Scene

24. : Test if GameScene has the correct number of GameObjects

Expected Outcome: Upload a JSON file with 10 objects, assertEquivalency(
GameScene.getImmutableObjectList.size(), 10\) should return true.

25. : Test if GameObject has the correct components  
    Expected Outcome: Create a GameObject class and add the healthComponent and the physics
    component. assertTrue(True, object.hasComponent(healthComponent)) and assertTrue(True,
    object.hasComponent(physicsComponent)) should both return true.


26. Test if player wins the game  
    Expected Outcome: Artificially place it in a Game object and then test that it returns a
    collision object with the with the win component. assertEqual(GameStatus, WIN) should return
    true.


27. Display.loadView()

	Expected outcome: The correct display is shown 

28. Game player pause button

    Expected Outcome: If the user is actively playing a game and click the pause button, the pause
    screen should be loaded


29. Exit Game Button  
    Expected Outcome: CurrentScene should point to the splash screen after the exit button is
    clicked.

30. Pause screen continue button

Expected Outcome: If the user clicks continue in the pause screen, the game should start back up
from where the user left off

31. Exit Builder Editor Button

    Expected Outcome: If CurrentScene should point to the splash screen and user on the front end
    should see it go back to the splash screen.

32. Hide Object Panel Button

Expected Outcome: The side panel goes off screen and then there is a small button to bring it back
to full view

