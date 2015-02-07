# Grid
Grid game featuring color blocks (similar to 2048) on Android and iOS using LibGDX framework

Game Mode: 
  1. Beat the clock
  2. Classic
  3. Minimize Moves
    Each level is given number maximum moves, number of moves used for bronze, silver, and gold.
    To unlock new levels, require some numbers of bronze, silver, and gold.
    Amount of points are awarded depending on number of move left, time, and bonus points.
    To win a level, the player must eliminate all colored tiles onscreen. To eliminate colored tiles,
      the player must move the tiles so that 3 or more are adjacent to each other. 
    
    Player is given number of tools to help win the round.
    Tools available:
      1. Undo (num) - will undo game state back 1 step backward per 1 usage
      2. Switch* (num)
      3. Activator* (num)
      
      *Usage of this tool counts toward number of move left.
