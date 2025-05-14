# APIs

API's design goals, primary abstractions and their behavior, as well as how they serve developers.

- GameSceneAPI
    - MappingContext mappingContext
    - Public void step(deltaTime): Loops through all GameObjects and calls their step function.
    - Public GameObject getObject(Name)
    - Public UnmodifiableList\<GameObject\> getObjects(Tag)
    - public void removeObject(GameObject)
    - Public void addObject(GameObject)
- GameObject API
    - Public void init()
    - Public void removeComponent(String className) \#Note the assumption that there is at most 1
      instance of a particular component type on an object
    - Public void addComponent(String className)
- Logger API
    - Public String getLatestLog()
- Parser API
    - Public void read()
- Writer API
    - Public void write()
- Components API
    - Public void awake(): called when added
    - Public void start(): called in the very first frame
    - Public void update(): called every frame
    - Public GameComponent getComponent(ComponentClass)
    - Subclasses of components API like Physics
        - InputActionComponent
            - Map\<String, InputAction\>
            - addAction(name, inputAction)
            - bindAction(name, callback)
            - execute(name)
- CurrentScene (passed around as an Immutable)
    - GameScene currentScene \=
    - getCurrentScene()
    - Helpful getter methods
    - loadScene()
    - newScene()
- Controller
    - Communication between front and back end
- Display
    - Holds the Current view
        - GameScene
        - Selection Screen
    - loadView(identifier)
    - newGame(fileName)
- GamePlayer
    - Method for user input
- MappingContext
    - Maps input keys to actions
    - Ex: gameContext
        - GameContext.bindKey(d, MoveRight)
        - {d \-\> MoveRight} where MoveRight is an input action
        - executeAction(int keyCode)
            - Get the IA from the map if it exists
            - Call the execute method of the IA
        - GameObject player \= new GameObject(“Player”)  
          InputAction moveRight \= new InputAction()  
          gameContext.bindkey(‘d’, moveRight)  
          player.getIAComponent().addAction(“MoveRight”, moveRight)  
          player.getIAComponent().bindAction(“MoveRight”, player::moveRight)  
          frame.addKeyListener(x \-\> gameContext.executeAction(x.keyCode())
- With this setup we can easily change out MappingContexts to have different behaviors
- InputAction
    - execute()
        - Execute all callback functions
    - addCallback(callback method)
    - Objects will have reference to IAs instead of copies
- Editor
    - ObjectPanel object
- ObjectPanel
    - Functionality to display placeable actors and place them

Controller  
List\<Objects\>  
Switch platform \---\> new rectangular shape   
