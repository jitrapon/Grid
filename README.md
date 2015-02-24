# Grid
Grid game featuring color blocks (similar to 2048) on Android and iOS using LibGDX framework

Game Mode: 
  1. Beat the clock
  2. Classic
  3. Minimize Moves
    Each level is given number maximum moves, number of moves used for bronze, silver, and gold. Round ends when you eliminate all colored squares. If you do, within certain number of moves, you will receive a trophy each level. 

In classic mode, default number of moves before another random colored square spawns. You start with 4 moves, then 3, 2, and 1. You may collect special items using colored square to temporarily increase moves in-between spawns. 

    To unlock new levels, require some numbers of bronze, silver, and gold.
    Amount of points are awarded depending on number of move left, time, and bonus points.
    To win a level, the player must eliminate all colored tiles onscreen. To eliminate colored tiles,
      the player must move the tiles so that 3 or more are adjacent to each other. 
    
    Player is given number of tools to help win the round.
    Tools available:
      1. Undo [top-right 2] (num) - will undo game state back 1 step backward per 1 usage
      2. Swap* [top-mid-right] (num)
      3. Special* [bottom-mid] (num)
      4. Restart [top-right 1]
      5. MoveLeft [top-mid] (num)
      6. Game option [top-left] (equivalent to Android's Back) 
      	Triggers PAUSE mode
      	- Tutorial
      	- Settings
      	- Exit to MainMenu Screen
      7. Time [above MoveLeft]
      
      *Usage of this tool counts toward number of move left.
      
      BUG:
      Visual glitch:
      	1. When a color group is removed, and at the same turn, another same-colored tile moves in to replace
      		one of the spots, the colored tile doesn't update correctly.
      	2. Adjacent same-colored tiles sometimes don't move in the correct order.
