# PlayerLevels
PlayerLevels is a leveling plugin for Paper designed to be used on Velocity or BungeeCord networks.

### Requirements
- PaperMC server with version 1.19 or higher
- MariaDB or MySQL database

### Installation
1. Download the plugin and put it into your `plugins` directory.
2. (Re)start the server.
3. Open `config.yml` and enter your database credentials. If you don't know how to do this, read the next chapter.
4. Restart the server again or reload the config using `/levels debug reload config`
5. If you see no error in the console, the database connection should be established successfully. You can use `/levels debug database info` to check it if you want to make sure.
6. Start using the plugin

### Simple database setup
This guide is for Linux. If you use a Windows server and, you are on your own (and btw, you shouldn't use Windows for servers).
1. Install MariaDB: `sudo apt install mariadb-server mariadb-client` or `sudo dnf install mariadb-server`.
2. Let it start on OS startup: `sudo systemctl enable --now mariadb`
3. Start MariaDB setup: `sudo mariadb_secure_installation`.
4. Answer the questions you're getting asked. Don't create security risks here. Disallow remote root login, select a password, remove test database, etc...
5. Open MariaDB console: `sudo mariadb -u root`. As the root user on the OS, you can log into MariaDB without a root password.
6. Create a database for PlayerLevels: `CREATE DATABASE playerlevels;`
7. Create a user for PlayerLevels: `CREATE USER 'db_playerlevels'@'localhost' IDENTIFIED BY 'your secure password'`. **Do not use the root user for PlayerLevels!**
8. Grant privileges: `GRANT ALL PRIVILEGES ON db_playerlevels TO 'db_playerlevels'@'localhost';` and `FLUSH PRIVILEGES;`.
9. Enter the credentials. In this example, `db_playerlevels` is your user, `your secure password` your password, `3306` your port and `playerlevels` your database.

If you are using MySQL, it should be mostly the same, but I would recommend you to use MariaDB since my plugin is developed against that.

### Leveling
On of the two main parts of the plugin is - as the name already says - levelling.

##### How it works
Players can earn XP. Using the xp formula, this xp is "transformed" into levels. If a player reaches enough xp, the player levels up.
That's mostly everything about the leveling system.

##### How the plugin handles levels and XP
The plugin stores the level of the player and the amount of xp the player has on the current level.
The total XP is calculated from the sum of the XP of all levels calculated through the xp formula plus the amount of xp the player has on the current level.
So the plugin is "level-centered" and not "xp-centered", if you want to call it this way.

The xp formula can be configured in the `config.yml`:
```yml
# The amount of xp is calculated by replacing the placeholder 'level' with a specific level the xp is required for.
xp-formula: level * 10
```
  
##### How to get XP as a player
There is no built-in way to gain XP, since the plugin is designed to be integrated into other plugins.
Those other plugins then will give the XP to the players.
For example, when you have a minigame plugin, this plugin can add a specific amount of XP to the players for playing, making kills or whatever.
  
There is also a comprehensive management command for moderators.

##### Player information
Players can use `/level` to get their own leveling information, or `/level <player>` to get the level of other players (if they are allowed to).
  
If you are looking for a management command, you need to read further since it's explained later.

##### Leaderboard / Toplist
There is a level leaderboard, which shows the players with the highest levels.

### Rewards
The second main part of the plugin is the rewards-system.

##### How it works
Players can earn rewards when they level up. There are several types of rewards, and more can be added using the API.
  
Rewards can be applied in two different apply types: `milestone` (one-time) or `interval` (recurring).

##### Default reward types
By default, there are 3 reward types:
- Command: Execute a command when the reward is earned
- LuckPerms: Give the player a permission when the reward is earned
- PlayerPoints: Give a specific amount of points to the player when the reward is earned

More types can be added using the API.

##### How to configure rewards
You can configure rewards in the `rewards.yml` in your `plugins` folder:

```yml
test_reward:                          # Reward ID: The plugin uses this id to identify the reward. It has to be unique!
  name: Test Reward                   # Name of the reward
  description: This is a test reward  # Description of the reward
  #server: my_server                  # Add this option to restrict this reward to a specific server on the Velocity/BungeeCord network 
  apply_type: milestone               # The reward apply type: milestone or interval
  level: 10                           # the level this milestone reward is unlocked
  type: command                       # The reward type: command, luckperms, playerpoints, or your custom reward type
  command: 'say {reward_name} has been unlocked by {player_name} on level {player_reward_level}'
  sender_type: console
test_interval:
  name: Say Message
  description: Earn a say message!
  #server: my_server
  apply_type: interval
  start: 1                          # Start level: The level where the reward is earned for the first time
  interval : 1                      # The interval of levels the reward is earned
  limit: -1                         # The limit after which the reward is not earned anymore. Use -1 to disable.
  type: command
  sender_type: console
  command: 'say {reward_name} has been unlocked by {player_name} on level {player_reward_level}'
```

Please make sure that all servers you are using the plugin on will have the same `rewards.yml`.
Otherwise, this can lead to unpredictable behavior.

##### How rewards are stored
The plugin stores which player has received which reward on which level, which means that a player can't get a reward twice.
There are also custom applied conditions, where the plugin can use a different way to check if the reward has already been applied.

##### Player information
To see milestones, players can use the `/level milestones` command.
For interval rewards, there is currently no way for players to show the rewards. This is a planned feature and will be added in the future.

### Management
The plugin has a comprehensive management command which allows to modify nearly every player value.
  
The base command for this is `/levels manage players` for player management and `/levels manage rewards` for rewards management.

#### Players
This section shows how players are managed by the plugin and can be managed by admins.

##### Easy examples
Since the commands look very complicated, here are some easy examples how to use them:
- Get leveling information about a player: `/level manage players info <player name>`
- Set a player's level: `/level manage players level set <player> <level>`
- Give xp to a player: `/level manage players xp give <player> <xp>`
- Delete all data from a player (player should be offline, if not, it resets the player instead of deleting their data): `/levels manage players erase <player>`

This should be enough for simple management tasks. Most of the other stuff, the plugin handles for you.

##### Command Overview (Advanced)
Here is the full overview of all player management commands:

| Action                             | Command                                                                                                                                                                                 |
|------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Player Info                        | `/levels manage players info <player> [--use-cache]`                                                                                                                                    |
| Manage level                       | `/levels manage players level (get <player>\|set <level> <level>) [--use-cache\|--push=(true\|false)\|--no-update]`                                                                     |
| Manage XP                          | `/levels xp (get <player>\|set <player> <xp>\|give <player> <xp>\|take <player> <xp>) [--use-cache\|--push=(true\|false)\|--no-update]`                                                 |
| Manage player's received rewards   | `/levels manage players rewards <player> (list\|get <reward>\|set <reward> (blocked\|level) <value>\|reset <reward>\|delete <reward>) [--use-cache\|--push=(true\|false)\|--no-update]` |
| Manage Cached Name                 | `/levels manage players cached-name <player> (get\|clear) [--use-cache\|--push=(true\|false)\|--no-update]`                                                                             |
| Manually process player            | `/levels manage players update <player> [--use-cache\|--push=(true\|false)]`                                                                                                            |
| Manually sync player with database | `/levels manage players sync <player>`                                                                                                                                                  |
| Erase player from database         | `/levels manage players erase <player>`                                                                                                                                                 |

##### How the plugin handles players (Advanced)
Players are stored in memory and in the database. More specifically, their UUID, their name, their level, their XP and the rewards they have already received are stored.
Some of these things should be recognizable in the management commands.

Players can also be “processed”. This simply means that the system checks whether the player has risen a level and whether they should receive rewards.

The plugin obtains its player data from the database and keeps a copy of it, because accessing the database would take longer. This copy is also called the cache.

To ensure that the player data is always up-to-date, the plugin must synchronize its local data with that of the database.
This happens at regular intervals.

To prevent the local data from becoming too large, only players who are actually online are cached.

With this extensive information, it is probably easier to understand the management commands above.

##### The command options (Advanced)
With the knowledge of the previous section, we can now look at the command options.

| Option                 | Description                                                                                                                                        |
|------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| `--use-cache`          | Most management commands will synchronize with the database before doing their stuff. If you don't want that, use this option.                     |
| `--push=(true\|false)` | Most management commands will push their changes to the database immediately when they are done. If you don't want that, set this option to false. |
| `--no-update`          | Most management commands will process (update) the player when they are done. Use this option if you don't want that.                              |

You can see in the command overview where you can use which option.

#### Rewards

##### Easy examples
- List all rewards: `/levels manage rewards list`
- Show information about a reward: `/levels manage rewards info <id>`

##### Command overview (Advanced)
| Action                            | Description                                 |
|-----------------------------------|---------------------------------------------|
| List rewards                      | `/levels manage rewards list`               |
| Get reward info                   | `/levels manage rewards info <id>`          |
| Reload rewards from `rewards.yml` | `/levels manage rewards reload`             |
| Enable/Disable reward             | `/levels rewards enable <id> (true\|false)` |

##### How the plugin handles rewards (Advanced)
Rewards are stored locally. That means that every server has to have the same `rewards.yml`.
Otherwise, this can lead to unpredictable behavior.
  
Every time a player is processed, the plugin will check if the player fulfills the condition to unlock the reward.
If the player fulfills the condition, the plugin will give the player the reward and stores the level the player has received the reward on inside the player's data.
  
to be continued...