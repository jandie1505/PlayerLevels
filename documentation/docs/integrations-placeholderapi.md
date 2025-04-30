# PlaceholderAPI Integration
PlayerLevels supports PlaceholderAPI.

### Installation
PlaceholderAPI support is not integrated in the core plugin.
You need to install a PlaceholderAPI expansion:
  
1. Download the PlaceholderAPI expansion JAR file.
2. Put the expansion file into `plugins/PlaceholderAPI/expansions/`.
3. Restart the server.

### Placeholders
All Placeholders have the Prefix `playerlevels_`.  
So for example, `level` in the table means `playerlevels_levels` on the server.

| Value                                                | Placeholder                  | Description                                                              |
|------------------------------------------------------|------------------------------|--------------------------------------------------------------------------|
| Player Level                                         | `level`                      | The player's level                                                       |
| Player XP (current level)                            | `xp`                         | The XP the player has on the current level                               |
| Player XP (current level, formatted)                 | `xp_formatted`               | `xp`, but formatted (like 1.2K or 2,5M)                                  |
| Player XP (total)                                    | `total_xp`                   | The XP the player has in total (XP for all previous levels + current XP) |
| Player XP (total, formatted)                         | `total_xp_formatted`         | `total_xp`, but formatted (like 3.4K or 5.1M)                            |
| Player UUID                                          | `uuid`                       | The UUId of the player                                                   |
| Player Name (cached name)                            | `name`                       | The cached name of the player                                            |
| Player's next level                                  | `level_next`                 | The next level the player will reach (it's just `level + 1`)             |
| XP of the player's level                             | `xp_current_level`           | The amount of XP of the player's current level (not the player's XP)     |
| XP of the player's level (formatted)                 | `xp_current_level_formatted` | `xp_current_level`, but formatted (like 3.4K or 5.1M)                    |
| XP of the player's next level                        | `xp_next_level`              | The amount of XP of the player's next level (not the player's XP)        |
| XP of the player's next level (formatted)            | `xp_next_level_formatted`    | `xp_next_level`, but formatted (like 3.4K or 5.1M)                       |
| XP between player's level and next level             | `xp_to_next_level`           | The amount of XP of the player's level to the player's next level        |
| XP between player's level and next level (formatted) | `xp_to_next_level_formatted` | `xp_to_next_level`, but formatted (like 3.4K or 5.1M)                    |
| Remaining XP to the next level                       | `xp_remaining`               | The amount of XP the player needs to get to level up                     |
| Remaining XP to the next level (formatted)           | `xp_remaining_formatted`     | `xp_remaining`, but formatted (like 3.4K or 5.1M)                        |

You can also use all player's placeholders for the toplist, by adding `toplist_PLACE_` as prefix for the placeholder.  
So if you want the level of the first player on the toplist, use `playerlevels_toplist_1_level` as a placeholder.
