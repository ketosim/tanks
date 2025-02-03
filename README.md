# Tank Battle Game (Java + Processing)

A 2D multiplayer tank battle game where players take turns controlling tanks, aiming and firing projectiles at each other to destroy opponents and win levels. Developed using the **Java programming language** with **Processing** for graphics and **Gradle** for dependency management. It is part of my OOP course assignment. 

## Features
- **Multiplayer Mode**: Players take turns to control their tanks.
- **Tank Movement**: Move across dynamic terrain using arrow keys.
- **Projectile Mechanics**: Aim and fire projectiles affected by power, angle, wind, and gravity.
- **Destructible Terrain**: Terrain can be altered dynamically during gameplay.
- **Power-ups**: Use items like repair kits, fuel boosts, and larger projectiles to gain an advantage.
- **Scoreboard**: Tracks scores across levels and highlights the winner at the end.

## Gameplay Overview
- Each player starts with a tank that has health, fuel, and a set power level.
- Players take turns aiming and firing projectiles to hit opponents’ tanks.
- The terrain is destructible and dynamically reshaped when hit by explosions.
- Wind affects the projectile trajectory, creating a strategic challenge.
- The player with the highest score after all levels is declared the winner.

## Controls
| Action             | Key               | Description                                      |
|-------------------|------------------|--------------------------------------------------|
| Move Tank Left     | Left Arrow       | Move the tank left across the terrain            |
| Move Tank Right    | Right Arrow      | Move the tank right across the terrain           |
| Aim Turret Left    | Up Arrow         | Rotate turret counterclockwise (left)            |
| Aim Turret Right   | Down Arrow       | Rotate turret clockwise (right)                  |
| Increase Power     | W                | Increase projectile power level                  |
| Decrease Power     | S                | Decrease projectile power level                  |
| Fire Projectile    | Spacebar         | Fire a projectile and end the current player’s turn |
| Use Power-up       | R/F/P/X          | Repair kit, extra fuel, parachutes, or larger projectiles |

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/ketosim/tanks.git
   cd tanks
   ```

2. Build the project using **Gradle**:
   ```bash
   gradle build
   ```

3. Run the game:
   ```bash
   gradle run
   ```

## Game Requirements
- **Java Version**: 8 or later
- **Processing**: Core and data libraries (used for rendering graphics)
- **Dependencies**: Gradle-managed

## Configuration Files
The game uses configuration files for customizable settings:
- **config.json**: Defines player colors, terrain layouts, backgrounds, and tree sprites.
- **Level files**: Defines terrain height, player starting positions, and obstacles.

## Gameplay Video
For a quick demonstration of the game in action

[![Watch the video](https://img.youtube.com/vi/OSJUIY-3Kmg/0.jpg)](https://www.youtube.com/watch?v=OSJUIY-3Kmg)


## Testing
The project includes unit tests using **JUnit** with over 80% code coverage. To run tests:
```bash
gradle test
```

## License
This project is licensed under the MIT License. See the LICENSE file for more details.

