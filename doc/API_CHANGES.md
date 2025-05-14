## OOGASalad API Changes
### Team Platformers
### Names Reyan Shariff, Hsuan-Kai Liao, Daniel Rodriguez-Florida, Jack Reagan, Calvin Chen, Logan Dracos, Justin Aronwald, Christian Bepler.

### Changes Made

#### API #1: Game/InputHandler

* Method changed: getInputMapping -> getCurrentInputKeys

    * Why was the change made?
      * Before we think a lot about how the different inputs should be taken in to the game and handled
        separately, but we ended up using the same input for different things at once (in one frame). 
        Therefore, we decided to change it to getCurrentInputKeys, which returns directly give the 
        input keys in this frame. With this, we can handle the input in a more flexible and 
        straightforward way.

    * Major or Minor (how much they affected your team mate's code)
      * Minor. The change was made before we actually started implementing the game, so it didn't affect
        any of our main code.

    * Better or Worse (and why)
      * Better. The change was made to make the input handling more flexible and straightforward.
        It allows us to handle the input in a more flexible way, which is better for our game.

#### API #2: Bahavior/Action/Constraint

* Method changed: GameComponent-based Logic -> Behavior-based Logic

    * Why was the change made?
      * Before Sprint 2, we are a bit confused and tried to use GameComponent to make games directly,
        but it ended up being too hard-coded, and we couldn't let the users to do the same thing we
        did. Therefore, we decided to change it to use the behavior control, which means we are 
        explicting showing different constraints and actions for the user (and us) to choose from to
        form the game. This allows us to think as a way

    * Major or Minor (how much they affected your team mate's code)
      * Major. This changes the way we originally thoughts about making games
        and how we are going to implement the game. It also forces us to think more about "giving" power
        to the users rather than "using" power on our own.

    * Better or Worse (and why)
      * Absolutely better. This change allows us to give power directly to the users, and every time
        we think of a new functionality to implement in the game, the user can use them later on.
        Since we're in a No-Code environment, we need to think about how to give the users enough
        flexibility to make their own games, and this change allows us to do that.

#### API #3: GameScene/GameComponent

* Method changed: awake/start initialization methods -> awake only

    * Why was the change made?
      * Before we think we need two types of initialization for users. One is awake() for specific 
        referencing and the start() is for general setup. Actually the design is learnt from the
        Unity C# scripting, but we find out that it is not necessary to have two types of initialization
        methods since we are only going to have a front-end for the users to setup the game settings.

    * Major or Minor (how much they affected your team mate's code)
      * Between Major and Minor. Because before most of the code we use for initalization for GameComponents
        are inside the start() method, we need to change the code to put them into awake() method. But
        the refactor is just a simple rename. So it changes a lot of code, but the change is not that
        difficult.

    * Better or Worse (and why)
      * Hard to say. This simplifies the logic itself but it also makes the initialization less
        flexible. We think this is a good change since we are not going to have two types of 
        initialization methods, but it may happen when there are future features that require two types
        of initialization methods.

#### API #4: Parser/Serializer

* Method changed: Hardcoded/Switch Statement -> Polymorphic Serializers

    * Why was the change made?
      * Before we have a lot of hardcoded code and switch statements to parse the Json files for
        Serializable Fields, but we think it is not elegant enough. Therefore, we decided to 
        change it to use polymorphic serializers, which means we are going to have a lot of 
        different serializers for different types of fields, and they can be parsed dynamically. 
        This allows us to add new types of objects easily.

    * Major or Minor (how much they affected your team mate's code)
      * Major. This actually changes the whole parser API and the way we parse the Json files.
        But due to the fact that we separate model and view really clearly, we don't need to
        change a lot of code in the model or view.

    * Better or Worse (and why)
      * Absolutely better. This change allows us to add new types of objects easily and
        makes the code more elegant. It also allows us to have a lot of different serializers
        for different types of fields, which is better for our game.

#### API #5: Saving

* Method changed: saving only to Json -> pack resources for Json

  * Why was the change made?
    * Before we only save the Json files, but we think it is not enough. Therefore, we decided to
      pack all the resources needed for Json, which means we are going to have all the resources
      saved under the resources folder in the same directory with the json file. 
      This allows the users to send their games to other easily and swap their sprites easily.

  * Major or Minor (how much they affected your team mate's code)
    * Minor. This only affects the output part of the IO. We don't need to change a lot of code
      in the model or view. But we need to change the way we load the resources in the game (like 
      handle relative path or absolute path).

  * Better or Worse (and why)
    * Better. This change allows the users to send their games to other easily and swap their
      sprites easily. It also makes it possible to upload the entire games to the social hub.