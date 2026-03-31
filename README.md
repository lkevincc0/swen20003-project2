# Shadow Dungeon - Project 2
SWEN20003-2025s2-Project 2 - Shadow Dungeon
## Project Structure
```
project-2/
├── pom.xml                     
├── README.md                  
├── .gitignore                  
├── generate_git_log.sh            
├── res/                        
└── src/
    ├── ShadowDungeon.java      # Main Function
    ├── IOUtils.java            
    ├── entities/               # Entity Classes
    ├── rooms/                  # Room Classes
    └── utils/                  # Utility Classes
```

----

## UML
My code is corresponding to the UML diagram in the **project 2A**.
First, main function is responsible by **ShadowDungeon class**.
According to UML switch and add detail in different rooms by different room classes. Entities are also corresponding to UML, which charged by different entity classes. And also have details like obstacle or any physical interaction with player. Especially, enemy manager is an **abstract** class, which is responsible for managing all enemies in the room.

----
