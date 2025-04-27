# Rewards
The second of the two major features is the reward system.

## How it works
Rewards are "actions" which are applied to a player when that player reaches a specific level.  
There are several types of rewards, and more can be added using the API.  
Rewards can either be applied a single time when reaching a specific level or multiple times in a specific interval.
  
Rewards can be configured in the `rewards.yml` configuration file.

## Components of a reward
This section explains what a reward consists of.

### All rewards
Every reward has these components.

#### Reward ID
This is the unique identifier of a reward.  
It is not shown to the user and has to be unique.

#### Reward Name
The name of the reward. This name is normally shown to the user when unlocking the reward or in a list of all rewards.

#### Server ID
The server ID on which the reward should unlock. Rewards then will only unlock on servers with this specific ID.  
If there is no server ID set, the reward will unlock on all servers.
  
This feature is intended for networks where not all plugins are available on all servers.
  
Example:
A player levels up on a server of Gamemode A.
This levelup contains a reward for Gamemode B, but the plugin of Gamemode B is not available on the server of Gamemode A.
If no server ID is set, giving the reward would fail.
Therefore, you can set the server ID of that reward to the servers of Gamemode B.
PlayerLevels will then wait giving the Player the reward until that player joins onto a server of Gamemode B.

#### Reward Apply-Type
The apply-type of a reward determines when a reward is applied.
  
There are two apply types:
- Milestone Reward: Those rewards will only be given once when the player reaches a specific level.
- Interval rewards: Those rewards will be applied in a specific interval, with an optional staring and ending point.

You will find out more about this in the next section.

#### Description
The reward description is provided by the reward type you have selected, but most of the reward types let you set as you want.  
Some reward types allow the usage of placeholders in the description. This allows you for example the amount of points you just earned.
  
If you are a developer, this is not a description text.
This is a RewardDescriptionProvider, with which you can dynamically provide reward descriptions on a per-player basis.

#### Player Online Requirement
This sets if the player needs to be online for the reward can be given.
  
It is mostly irrelevant because it is up to the reward type and players are not loaded when being offline under normal circumstances.  
But this option exists, and now you know it.

#### Reward Type (Reward Executor)
The reward type is the actual thing that is done when the player unlocks the reward.  
You can find more information about this in the section about the default reward types.
  
If you are a developer, this is not a reward type.
It is a reward executor, which contains the code that is executed when the player unlocks the reward.

### Milestone Rewards
Only milestone rewards have these components.

#### Level
The level where the milestone reward is applied.
  
For example when the level of a milestone reward is set to 10 and a player reaches level 10, the reward is applied.  
After this, the reward is not applied again (except the specific reward uses a custom condition).

#### Custom Condition
Milestone rewards can have custom conditions.
Those custom conditions can determine unter which circumstances can be given again, even if the reward has already been given.
  
Having this depends on the reward type you are using. Most reward types don't need this.
  
If you are a developer, you can make use of this feature for your custom rewards.

### Interval Rewards
Only interval rewards have these components.

#### Interval
The interval in which the reward is given to the player.
  
For example, if the interval is set to 10, the reward will be given to players on level 11, 21, 31, 41, etc... (when the start is set to level 1).

#### Start
The level where the interval is started.
  
For example, if you have a reward with an interval of 10 and the start is set to 20,
the reward will be given the first time on level 20, and then on 30, 40, 50, etc...

#### Limit
The level over which the reward is no longer applied.

For example, if the interval is set to 10 and the limit to 50, the reward will only unlock on the levels 11, 21, 31 and 41, but NOT on 51, 61, etc...

## Configuration

### Milestone reward
```yml
test_milestone_reward:      # <-- The reward ID. Has to be unique.
  name: Test Reward         # <-- The reward name that is displayed to the players.     
  apply_type: milestone     # <-- The apply-type. In this case, it's 'milestone', because we want to add a milestone reward.
  level: 10                 # <-- The level the reward is applied on.
  type: my_reward_type      # <-- The reward type.
  # [Reward-specific settings will come here]
```

### Interval reward
```yml
test_milestone_reward:      # <-- The reward ID. Has to be unique.
  name: Test Reward         # <-- The reward name that is displayed to the players.     
  apply_type: interval      # <-- The apply-type. In this case, it's 'interval', because we want to add an interval reward.
  start: 10                 # <-- The start point of the intervals.
  interval: 1               # <-- The interval in which the reward is applied.
  limit: -1                 # <-- The limit after which the reward will no longer be applied.
  type: my_reward_type      # <-- The reward type.
  # [Reward-specific settings will come here]
```

## Reward types

### Default types

#### Command
A command reward is the simplest reward type.  
It just executes a command when the reward is applied.

You can configure it like this:
```yml
test_command_reward:
  # [...]
  type: command                                                                               # <-- Set command as the reward type
  command: "say Reward: {reward_name}, Player: {player_name}, Level: {player_reward_level}"   # <-- The command to run
  sender_type: console                                                                        # <-- The sender type (console or player)
  description: "My wonderful reward description"                                              # <-- The reward description.
```

This reward type can be added as a milestone and as an interval reward.

#### LuckPerms
This reward type gives a permission to the player when the reward is unlocked.
  
You can configure it like this:
```yml
test_luckperms_reward:
  # [...]
  type: luckperms                 # <-- Set luckperms as the reward type.
  permission: "my_permission"     # <-- The permission that should be given to the player.
  description: "My Description"   # <-- The reward description.
```

This reward can only be added as a milestone reward.
  
This reward uses a custom apply-condition.
If the player has unlocked this reward, PlayerLevels will check if the player has the permission.
If the player does not have the permission, it will be given (again), even if the player has unlocked the reward previously and not in this moment.

#### PlayerPoints
This reward type gives a player some points using PlayerPoints.

You can configure it like this:
```yml
test_playerpoints_reward:
  # [...]
  type: playerpoints                        # <-- Use playerpoints as the reward type
  formula: level * 10                       # <-- The formula that determines how much points the player will get. Available Variables: level, xp
  description: "<gray>+ <points> points"    # <-- The reward description. Supports minimessage and the <points> placeholder.
```

This reward type can be added as a milestone and as an interval reward.

### Custom rewards
Other plugins can add custom rewards using the API.
  
If a plugin registers a custom reward type, you can just configure it like the default rewards:
```yml
test_custom_reward:
  # [...]
  type: my_custom_type
  # [Options of the custom reward]
```
  
If you are a developer who wants to add a custom reward type, you can find out more in the developer documentation.
