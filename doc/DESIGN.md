# DESIGN Document for Oogasalad

### Platformers

### Reyan Shariff, Hsuan-Kai Liao, Daniel Rodriguez-Florida, Jack Reagan, Calvin Chen, Logan Dracos, Justin Aronwald, Christian Bepler.

## Team Roles and Responsibilities

* Reyan Shariff: Builder frontend/backend. Wrote Two backend APIS: UndoRedoManager and Builder. The
  UndoRedoManager was meant to handle undo redo operations on the builder editor through monitoring
  the action history. The purpose of the Builder API is to monitor saving games in the editor,
  storing the selected object, and had move object/place object functions. Also worked on the
  clicking and dragging aspect on the front end like moving the objects around on the screen and
  then resizing the object, incorporated the save button, and created the initial logic for scene
  switching.

* Hsuan-Kai Liao

* Daniel Rodriguez-Florido: Creating the parser reader and writer as well as testing. Focused on
  all the front-end work for the social scenes such as FollowScene, SearchProfileScene,
  LeaderboardScene,
  and integrating the backend logic with the front-end. Intent was to be flexible and available
  to help in all parts of the code. Worked closely with Justin and Jack.

* Jack Regan: Creating frontend stylesheets and StyleConfig class that interfaced with social,
  builder
  and Player scenes. Contributed to the development of several scenes within the social UI.
  Refactored
  GameConfig and entire UI to be compatible with language switching. Intent was to make UI scene
  development
  very straightforward with intuitive integration with property files. Early on in the project I
  worked closely
  with Logan to get the early engine version connected to the Player.

* Calvin Chen

* Logan Dracos: My role on the team was mainly focused on implementations to make the games function
  properly. I worked full-stack on several implementations, particualrly with text rendering, where
  I had to dig into the renderer, parser, and view. I also spent the majority of the last sprint
  building out our data storage and text
  display for the purpose of having scoring functionality.
  I worked closely with the ECS system, rendering, and all game objects and components. Below are
  a few relevant files I worked on:
    1. Collider.java
    2. TextRenderer.java
    3. ChangeScene actions
    4. SetText Actions
    5. ScriptableDataStore.java

* Justin Aronwald: Creating the parser and writer, and then integrating it. Creating the structure
  for handling UI. Creating all controllers, services, class-representations, backend logic, and
  everything database-related towards the game including user profile, search
  page, followers, following, edit profile, leaderboard. Creating all authentication-based backend
  logic like log in, sign up, uploading profile pictures, and session management of the current
  user.
  Creating backend logic related to fetching and
  publishing user-generated levels and then displaying them on the profile for further use.
  Refactoring and modification of UI scenes, as well as integrating backend logic into UI scenes.

* Christian Bepler: Backend designer and refactorer. Massive work in the backend setting up various
  key infrastructures and implementations of them. Sorted out our code debt from the first 2
  sprints, becoming quite familiar with the entire backend codebase and a little of the frontend.

## Design Goals

* The first goal is to create a solid architecture that is scalable and future-proof to minimize the
  amount of time needed to refactor code as new games were added.

* The second goal is to create a builder that has highly flexible user functionality while also
  being a no-coding environment. In order to do this the user must be able to freely select which
  objects appear on the screen as well as their components and constraints.

* The third goal is to have functional gameplay for both finite and infinite games across multiple
  platformer games such as but not limited to the Chrome Dinosaur Game, Geometry Dash, and Doodle
  Jump.

#### How were Specific Features Made Easy to Add

* Feature #1: The game engine using the ECS design is aimed to be modular and extensible, allowing
  for easy addition of new features and components. For example, if we want to add a brand new
  particle
  system to our game, the only thing we need to is to add a new component called ParticleHandler and
  put
  the particle logic inside. And then we can add the ParticleHandler component to any entity in the
  game,
  and that game object will have the particle system applied.

* Feature #2: We are using the SerializableField to handle the serializing and deserializing of the
  fields, and the communication between frontend (builder) and backend (game engine). Specifically,
  if
  we need a new customized type that requires serialization and UI showing, we can just add a new
  serializer
  extends from Serializer<YourClass> and a new UI component extends from
  DeserializedFieldUI<YourClass>,
  and then everything will be handled automatically. The serialization system is designed to be
  flexible
  and extensible.

* Feature #3: The behavior tree system is designed to be modular and extensible, where if we want to
  have
  more powerful behaviors, we can just add a new behavior action/constraint. The behavior tree
  can allow the user to create complex behaviors by combining simple actions and constraints.
  Basically
  our design for the actions and constraints is to make them as low-level as possible, so that the
  user can
  make complex behaviors by combining simple logics.

## High-level Design

#### Core Classes and Abstractions, their Responsibilities and Collaborators

* The Game Class stores the all the game scenes. This is utilized in the builder backend class as
  well as the parser. In the builder class it is used to control adding and switching between scenes
  in the backend for the level editor. It encapsulates information like validating/registering new
  scenes and checking the high score. The Game also is responsible for animation as it calls the
  step function.

* The serializable field class serves the purpose of passing component information from the backend
  to the frontend. This is highly important for the UI widgets on the frontend as the users manually
  enter in their desired inputs. The serialize field class interacts with classes such as the
  boolean input, double input, deserialized input for the level editor. The serializable field also
  interacts with JSON nodes like the BehaviorConstraint node.

* The Parser interface looks through every scene, object, etc and appends it all to a JSON with the
  write function. It also loads in JSON files through the parse method. The actual logic behind the
  parsing is completely abstracted as in the Builder backend class it just calls the write and parse
  methods.

* The GameScene class contains everything needed for a particular level, whether it is finite or
  infinite. It stores all the gameobjects but encapsulates important logic like updating the
  components/objects and also running the subscribed events. It interacts with the builder as well
  as the parser.

## Assumptions or Simplifications

* One assumption we made was that the user would not alter the json file after saving it. We assumed
  that all modifications to their game would be done in the authoring environment.

* Another assumption we made was that users would be logged in with an account when playing/making
  games. This is due to the fact that we did not make a guest profile.

* An additional assumption is that the user would only make games with our given actions and
  constraints. We made the authoring environment under the impression that the user wouldn't want to
  make new ones.

* One more assumption is that the user would remember their password. This is due to the fact that
  we did not include a forget password feature.

## Changes from the Original Plan

* Change #1 - ECS Engine: As discussed in the presentation, a major refactor was made to the
  behaviors, splitting them into actions and constraints, allowing for much more flexibility in the
  design of objects' behaviors

* Change #2 - Game Builder Evolved: While initially focused on a simple drag-and-drop UI, the final
  builder included advanced features like scene switching, undo/redo, and live previews, uploading
  to the social hub, and more, exceeding
  the original no-code vision.

* Change #3 - Broadened Game Type Support: Originally centered on the Dino game with plans for
  expansion, the final implementation delivered both infinite runner and level-based games with
  shared logic. Due to the flexibility of our design, we were able to create a plethora of other
  game options, as can be seen in our final example data file submissions.

* Change #4 - Social Features Fully Delivered: Beyond the planned profiles and customization, the
  final system included user authentication, persistent data, and community-level sharing and
  leaderboards. Originally, we talked about having a custom leaderboard for each single game.
  However, after introducing infinite games, we consolidated into having tabbed leaderboard for
  infinite games, then having a custom leaderboard tab. Also, profiles originally didn't store user
  games, but after realizing that we already store user data, we could just expand our storage to
  store game objects. So now, users can store user-generated levels and view, play, edit, and
  download from profile.

## How to Add New Features

#### Features Designed to be Easy to Add

* Feature #1 - New components via ECS.
  * Create a new class that extends GameComponent. Specify the component tag based on the functionality attributed to the newly defined component (ie. PHYSICS). This tag will determine the order of rendering for the UI. Then add the necessary instance variables as serializable fields as well as their public get/set methods. Lastly create an update method that can override the super class and update the instance variables in accordance with the step function of the game animation.

* Feature #2 - Serialization
  * By extending a Game Component, you can add instance variables of said component class as serializable fields. Once this is done, the UI of the builder will automatically register the type of the instance variable as a serializable field and create a new input field within the user interface. Additionally, within the Serializer hierarchy, there are two methods that need to be overridden (deserialize and serialize). Serialize will return the passed object as a JSON Node for storage and the deserialize accepts a JSON Node and returns the desired output type (attributed to the initial serialized field).

* Feature #3 - Behavior constraints and actions
  * By extending BehaviorAction or BehaviorConstraint you can simply pass in the type that the constraint or action should take in and then implement the required methods. For constraint this is mainly a check method that takes in a parameter of the specified type and returns a boolean (true for passed and false for not passed). For actions this is a perform method that takes in a parameter of the specified type and does something. Once that is done the constraint or action will be available for use within the builder.

* Feature #4 - CSS Styling
  * Using the StyleConfig. It is very intuitive to add a new ViewScene to
  the controller
  by using the registerScene() API. This single API call is all that is required to extend CSS to a
  new
  ViewScene. Additionally, the CSS is very flexible and allows for intuitive customization of new
  themes.
  The styling makes use of a base css stylesheet that contains basic spacing styles. Then specific
  theme
  sheets are applied with variables attached to color gradients. To add a new theme, all that is
  required
  is changing the gradient of these variables in a new color theme sheet. Additional styling
  variables can
  also be added for further customization. (See -fx-rotate in freaky.css for an example).

#### Features Not Yet Done

* Feature #1 - Multiple games running simultaneously. Our current UI layout only opens one game at a
  time, since opening a game or the authoring environment removes the Main Menu window. Implementing
  this feature would be relatively simple,

* Feature #2 - Fully CSS-based styling for some UI elements. While many parts of our UI do use
  custom CSS themes, such as the main menu, login, and social pages, some CSS values are hardcoded
  into our code, such as in ListBehaviorComponentFieldInput, ComponentPanel, and
  ListAnimationFieldInput. This would be best implemented by adding the hardcoded CSS values to the
  stylesheets in resources, and then calling existing StyleConfig static methods for applying the
  styling to each JavaFX Scene.

* Feature #3 - Pop-Up Messages for all errors. While we created the ability to create pop-up errors
  via the class PopUpError, we did not finish implementing this class into all error scenarios. This
  feature would be completed by ensuring every user-relevant error is reported in this pop-up format
  in the code.

* Feature #4 - Rotation in the Level Editor. We allow for resizing game objects, however we do not
  currently have the ability to do rotations. We would implement this by adding this functionality
  to the rendering classes which convert Component fields to JavaFX outputs.

* Feature #5 - "Save progress in the game to restart later." While we ran out of time to implement
  this feature, there are a couple of different ways to implement this feature. One would be to save
  the game-in-progress as a JSON with updated Transform-component values to match each object's
  current game position. We would also need to store the current score of each of these games,
  either in the game JSONs or in a separate file that records what games are in progress. Another
  option would be to add a checkpoint feature to our games -- this would require much additional
  code but could be handy for setting respawn points. The current "portal" functionality present in
  the Geometry Dash variant is comparable to this feature. New behavior action(s) would need to be
  created for setting a respawn point, such as a subclass of GameAction which resets a character to
  the X-coordinate of the respawn checkpoint after dying.

* Feature #6 - "Save preferences that persist between program runs." This is a feature that we did
  not think to consider but would be relatively easy to implement. A file containing user preference
  information would be stored. This file would be updated any time the user changes their
  preferences, and the file would be read each time the program begins (defaulting to
  English/standard themeing if the preferences file was corrupted). This would likely be best served
  by a PreferencesHandler class, which would have static methods that could be called to request a
  language, theme, or other preferences. The code in MainMenuScene which retrieves all available
  themes and languages would also be moved into this new PreferencesHandler class. This allows for
  greater flexibility in changing these preferences at any point in the program, rather than in the
  main menu alone.
