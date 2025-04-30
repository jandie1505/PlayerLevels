# API: Players

### Getting a player
In PlayerLevels, players are represented by a Leveler.  
You can obtain a Leveler by using the LevelingManager.

```java
Player player;
LevelingManager levelManager = api.getLevelManager();

// Get a cached Leveler
Leveler cachedLeveler = levelManager.getLeveler(player.getUniqueId());

// If the leveler is not loaded, you can load it from the database.
// Normally, the Leveler of all online players is loaded.
// Since loadLeveler loads data from a database asynchronously, it returns a CompletableFuture of an Leveler.
CompletableFuture<Leveler> future = levelManager.loadLeveler(player.getUniqueId());

future.thenAccept(leveler -> {
    // Do stuff
    
    // If you need to use the Bukkit API, you need to go back into the main thread
    new BukkitRunnable() {
        @Override
        public void run() {
            // Do Bukkit API stuff
        }
    }.runTask(plugin);
});
```

### Modifying player data
#### Level and XP
The Leveler contains the LevelerData, which stores the players leveling information.

You can use this data to modify the player's level and xp:
```java
// Get the leveler's data
LevelerData data = leveler.getData();

// Get/Set Level
int level = data.level();
data.level(10);

// Get/Set XP
double xp = data.xp();
data.xp(10);
```

#### Received reward entries
The LevelerData also stores which rewards the player has already received.  
You can modify this information:
```java
// Get the leveler's data
LevelerData data = leveler.getData();

// Get all received reward entries
Map<String, ReceivedReward> receivedRewards = data.getReceivedRewards();

// Get one received reward entry
ReceivedReward reward = data.getReceivedReward("my_reward");

// Set the the reward blocked (the player will not receive the reward when it's blocked)
boolean blocked = reward.blocked();
reward.blocked(true);

// Set the level where the player has received the reward.
// The plugin uses this information to check if the player has already received the reward.
int receivedLevel = reward.level();
reward.level(10);

// Reset the reward entry to the default reward entry.
// A default reward entry will remove it from the database on the next sync.
reward.reset();

// Deleting an entry
data.removeReceivedReward("my_reward");
```

#### Processing a player
When PlayerLevels processes a player, this means that it checks if the player has enough xp to level up,
checks if any reward can be applied to the player, updates the cached name and cleans up unused received reward entries when enabled.
  
Normally, players are automatically processed in a specific interval.
  
If you have changed the level or xp of a player, you should process the player manually, because if you don't,
the player could see incorrect values for the next few minutes.

```java
Leveler leveler;
leveler.processAsynchronously();
```

#### Cache, database and synchronization
The plugin stores the player data in a MariaDB database.  
Since it would be very resource-heavy for the database when every command will send a request to it,
PlayerLevels caches the data of all online players.
  
To keep the data in the database up-to-date, PlayerLevels synchronizes the data every few minutes.  
When the player leaves, the data is also pushed to the database (and the player is removed from the cache).
  
You can also sync the data with the database manually:
```java
Leveler leveler;
leveler.syncAsynchronously();
```

This will return a result, which allows you to see if the sync was successful.
  
Please note that the database always has priority.
If there have been changes both in the database and locally, the data from the database is always adopted.
If this happened, you will see it in the returned result.

### Deleting a player
If you want to, you can also delete a player completely from the database and cache:
```java
UUID playerId;
LevelingManager levelingManager;
levelingManager.erasePlayerAsynchronously(playerId);
```

It is recommended that the player is offline when you delete them,
because if not, the plugin would just create a new Leveler instance.
