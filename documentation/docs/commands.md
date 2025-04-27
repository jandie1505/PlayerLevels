# Command Reference
The plugin has many commands. Here is an overview for all of them.

## Structure
This is the full command structure of the plugin:

`/levels`

- `info` (get level of other players)
- `top` (show toplist)
- `milestones` (show list of milestone rewards)
- `manage` (management commands for moderators/admins)
  - `players` (manage players)
    - `info` (get player info)
    - `level` (see/modify player level)
    - `xp` (see/modify player xp)
    - `rewards` (see/modify received rewards)
    - `cached-name` (see/reset cached name)
    - `sync` (sync with database)
    - `process` (update player's values)
    - `erase` (erase player from database)
  - `rewards` (manage rewards)
    - `info` (get info about reward)
    - `list` (show all rewards)
    - `enable` (enable/disable rewards)
    - `reload` (reload rewards)
- `debug` (debug commands for admins)
  - `cache` (modify the cache)
  - `config` (change config values)
  - `database` (manage database connection)
  - `messages` (change message values)
  - `server-info` (get server information)
  - `reload` (reload config/messages)
  
As you can see, this is a huge number of commands. But you most likely only need a small amount of them.

## Player commands
These commands are for normal players:

| Command                | Permission                             | Use case                       |
|------------------------|----------------------------------------|--------------------------------|
| `/level`               | none                                   | Show own leveling progress.    |
| `/level info <player>` | `playerlevels.command.info.view_other` | Show level of other players.   |
| `/level top [page]`    | `playerlevels.command.toplist`         | View the toplist.              |
| `/level milestones`    | `playerlevels.command.rewards`         | View a list of all milestones. |

## Management commands
### Players
These commands are there for managing players:

| Action                             | Command                                                                                                                                                                                  |
|------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Player Info                        | `/levels manage players info <player> [--use-cache]`                                                                                                                                     |
| Manage level                       | `/levels manage players level (get <player>\|set <level> <level>) [--use-cache\|--push=(true\|false)\|--no-process]`                                                                     |
| Manage XP                          | `/levels xp (get <player>\|set <player> <xp>\|give <player> <xp>\|take <player> <xp>) [--use-cache\|--push=(true\|false)\|--no-process]`                                                 |
| Manage player's received rewards   | `/levels manage players rewards <player> (list\|get <reward>\|set <reward> (blocked\|level) <value>\|reset <reward>\|delete <reward>) [--use-cache\|--push=(true\|false)\|--no-process]` |
| Manage Cached Name                 | `/levels manage players cached-name <player> (get\|clear) [--use-cache\|--push=(true\|false)\|--no-process]`                                                                             |
| Manually process player            | `/levels manage players update <player> [--use-cache\|--push=(true\|false)]`                                                                                                             |
| Manually sync player with database | `/levels manage players sync <player>`                                                                                                                                                   |
| Erase player from database         | `/levels manage players erase <player>`                                                                                                                                                  |

All those commands have the permission `playerlevels.manage_players`.

You might have already noticed that those commands support some command options:

| Option                 | Description                                                                                                                                        |
|------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| `--use-cache`          | Most management commands will synchronize with the database before doing their stuff. If you don't want that, use this option.                     |
| `--push=(true\|false)` | Most management commands will push their changes to the database immediately when they are done. If you don't want that, set this option to false. |
| `--no-process`         | Most management commands will process (update) the player when they are done. Use this option if you don't want that.                              |

### Rewards
Those commands are for there for managing rewards:

| Action                            | Description                                 |
|-----------------------------------|---------------------------------------------|
| List rewards                      | `/levels manage rewards list`               |
| Get reward info                   | `/levels manage rewards info <id>`          |
| Reload rewards from `rewards.yml` | `/levels manage rewards reload`             |
| Enable/Disable reward             | `/levels rewards enable <id> (true\|false)` |

All those commands have the permission `playerlevels.manage_rewards`.

## Debug commands
These commands are for debugging purposes:

| Action                      | Description                                                                                                        |
|-----------------------------|--------------------------------------------------------------------------------------------------------------------|
| Show and modify cache:      | `/levels debug cache (list\|refresh\|drop <player> [--no-process\|--confirm]\|drop-all [--no-process\|--confirm])` |
| Modify config:              | `/levels debug config (list [section]\|get <key>\|set <key> <type> <value>)`                                       |
| Manage database connection: | `/levels debug database (connect\|disconnect\|info)`                                                               |
| Modify messages config:     | `/levels debug messages (list [section]\|get <key>\|set <key> <type> <value>)`                                     |
| Reload config from files:   | `/levels debug reload (config\|messages> [--clear=(true\|false)\|--merge-defaults=(true\|false)]`                  |
| View server info:           | `/levels debug server-info`                                                                                        |

The permission of those commands is `playerlevels.debug`.
Don't use them if you don't know what you're doing.

Overview of the available command options:

| Option                          | Description                                                                                                               |
|---------------------------------|---------------------------------------------------------------------------------------------------------------------------|
| `--no-process`                  | Same as in player management command.                                                                                     |
| `--confirm`                     | Confirms dangerous operations.                                                                                            |
| `--clear`                       | For reload command: Clears the config before merging the config file values into it.                                      |
| `--merge-default=(true\|false)` | For reload command: If the default values should be merged into the config before merging the config file values into it. |
