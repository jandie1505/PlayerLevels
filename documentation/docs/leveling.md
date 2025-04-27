# Leveling
One of the two major features of the plugin is leveling.

## How it works
Players can earn XP. Using the xp formula, this xp is "transformed" into levels. If a player reaches enough xp, the player levels up.
That's mostly everything about the leveling system.

## Player interaction
Players can see their current leveling progress using the `/level`-command.  
If enabled in config, players can also see the level of other players by doing `/level info <player>`.
  
You can customize the messages of those outputs in the config.
  
If the expansion is installed, you can also use placeholders using PlaceholderAPI to show players their levels.

## Toplist
The plugin has a level toplist. Players can view the toplist using the `/level top` command.
  
You can specify the length of the toplist in the configuration.

If the expansion is installed, you can also show the toplist in placeholders.

## Leveling as a player
The plugin has no built-in way for players to earn xp.
This choice has been made on purpose, since this plugin is designed to be integrated into other plugins.
  
To earn XP, other plugins like your minigames need to give XP to the players using the API.  
If you are a developer who wants to integrate PlayerLevels into their plugin, read the API documentation.

## Management
PlayerLevels has many management commands. Here are some, you will find all of them in the command reference:
- Get player information: `/level manage players info <player>`
- Set level: `/level manage players level set <player> <value>`
- Set xp: `/level manage players xp set <player> <value>`
- Give xp: `/level manage players xp give <player> <amount>`
