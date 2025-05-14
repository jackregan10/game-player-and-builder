\# OOGASalad Agile Plan Discussion  
\#\# Platformers  
\#\# Justin Aronwald ([jga18@duke.edu](mailto:jga18@duke.edu)), Christian
Bepler ([cgb45@duke.edu](mailto:cgb45@duke.edu)), Calvin
Chen ([clc162@duke.edu](mailto:clc162@duke.edu)), Logan
Dracos ([lsd25@duke.edu](mailto:lsd25@duke.edu)), Hsuan-Kai
Liao ([hl475@duke.edu](mailto:hl475@duke.edu)), Jack
Regan ([jfr29@duke.edu](mailto:jfr29@duke.edu)), Daniel
Rodriguez-Florido ([dr285@duke.edu](mailto:dr285@duke.edu)), Reyan Shariff (rts34@duke.edu)

\#\#\# Backlog (Feature Requirements)

\* Builder  
\* Frontend

- Drag \+ Drop UI
- Camera
- Customizable Menu

  \* Backend

- JSON Config File Generator
    - generateConfig()

  \* Controller

- updateGameObjectList()
- getEntityList()

\* Player  
\* Frontend

- Display
- loadScene()
- Animator
- step()

  \* Backend

- GameScene

\* Parser  
\* Backend

- writeFile()
- readFile()

\* Social  
\* Frontend

- Leaderboard
- Profile Page
    - editProfile()
- Social Hub
  -Backend

- Database Creation
    - updateDatabase()

\#\#\# Sprint 1

Off Half:

- Focus on understanding code written per day
- Create git issues
    - Could be anywhere from error handling to potential new additions
- Be available for pair programming
-

Elements that need to be able to be built:

- Dinosaur
    - Sprite
    - Gameplay
        - Dinosaur Behavior
            - Implementations of transform, physics, cand ollision components
        - Block Behavior
            - Implementations of transform, physics, collision components
    - Editor
        - Customizable parameters for dinosaur components
        - JSON file generator for dinosaur parameters
        - Entity Controller
            - Responsible to communicate placement of front-end items to the backend to generate the
              json file

\* Calvin  
\* Animation for Gameplay experience

\* UI Screen for Dinosaur

\* Jack & Logan  
\* Create git issues (Jack)  
\* Start drafting tests (partially based on Justin and Danny)  
\* Strongly define functionality for social hub to follow with extension guidelines (API)  
\* Devops setup \- if needed  
\* Dinosaur game \- frontend

\* Justin & Danny  
\* Define all expected behaviors in games, including entities, object components and behaviors,
controls for player, and win/loss conditions  
\* Make decisions on JSON format and create a mock (data storage)  
\* Work on parser  
\* Dinosaur game \- frontend

\* Reyan  
\* Dinosaur Behavior/Component subclasses  
\* UI APIs for the Builder Editor

\* Aaron & Christian  
\* Complete a runnable Entity Component System Game Architecture that allows multiple components and
behaviors to be implemented.

\* Basic abstraction super classes for Components, Behaviors and GameObjects.

\#\#\# Sprint 2

\* Implement Geometry Dash Game Player  
\* Implement Geometry Dash Game Editor  
\* Refactor Sprint 1

\#\#\# Sprint 3

\* Implement User Profile  
\* Implement Social Center  
\* Save Game Data in Web  
\* Explore AI implementation of Looped JSON Creation  
\* Refactor Sprint 1

\#\#\# Sprint 4 (Complete)

\* Implement Doodle Jump Game Player  
\* Implement Doodle Jump Game Editor  
