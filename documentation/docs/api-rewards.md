# API: Rewards
This part of the API can be used to manage rewards.

### Get the RewardsManager
Rewards are stored in the RewardsManager.  
You can get the RewardsManager from the API object:

```java
PlayerLevelsAPI api;
RewardsManager manager = api.getRewardsManager();
```

### Managing Rewards

#### Getting rewards
From the RewardsManager, you can get, create and delete rewards:

```java
// Get all registered rewards
Map<String, Reward> rewards = manager.getRewards();

// Get one reward with a specific ID
Reward reward = manager.getReward("my_reward");
```

#### Adding or removing rewards directly
**Adding or removing a reward directly is not recommended, since it can unexpected behavior.  
[If you are creating custom rewards, register a RewardCreator to the RewardsRegistry as described in another section below.](#custom-reward-types-and-rewardregistry)**

##### Removing
Removing a reward is easy:

```java
manager.removeReward("my_reward");
```

##### Adding
Adding a reward directly is complicated and should be avoided. As mentioned above, use the RewardsRegistry.

###### 1. RewardConfig
First, you need a RewardConfig:
```java
RewardConfig rewardConfig = new RewardConfig("my_reward", null, "My Reward");
```

###### 2. RewardData
Then, you need a RewardData. And this is where it gets complicated.
  
You can either completely create it on your own, or use a predefined reward.
  
Creating RewardData manually:
```java
RewardExecutor executor = (reward, leveler, level) -> {
    // What your reward should do when its applied
};

RewardCondition condition = null;
RewardDescriptionProvider descriptionProvider = null;
boolean requiresOnlinePlayer = true;

// Create an interval reward data
IntervalRewardData intervalData = new IntervalRewardData(executor, condition, descriptionProvider, true, 1, 1, -1);

// Create a milestone reward
MilestoneRewardData milestoneData = new MilestoneRewardData(executor, condition, descriptionProvider, true, 10);
```

Using a predefined RewardData:
```java
MilestoneRewardData milestoneData = CommandReward.createMilestone("command", true, CommandReward.SenderType.CONSOLE, null, 1);
IntervalRewardData intervalData = CommandReward.createInterval("command", true, CommandReward.SenderType.CONSOLE, 1, 1, -1);
```

###### 3. Registering the reward
If you have both your RewardConfig and RewardData, you can register your reward.
```java
RewardsManager manager;
manager.addMilestoneReward(rewardConfig, milestoneData);
manager.addIntervalReward(rewardConfig, intervalData);
```

### Custom reward types and RewardRegistry
Using the RewardRegistry and custom reward types is the recommended way for creating custom rewards.
  
#### Creating a custom reward type
To create a custom reward type, you need to create a new class, import the necessary interfaces and implement their methods.
  
A typical class looks like this:
```java
public class MyCustomReward implements RewardExecutor, RewardDescriptionProvider {
    
    // Here you create your reward functionality.
    // You can also implement RewardCondition to add a reward custom condition.
    
    private MyCustomReward() {
        // Your constructor
    }
    
    @Override
    public boolean onApply(@NotNull Reward reward, @NotNull Leveler leveler, int level) {
        // Here comes your code which is executed when the reward is applied (given) to a player.
        // Example:
        Player player = Bukkit.getPlayer(leveler.getPlayerUUID());
        if (player == null) return false;
        player.sendRichMessage("<gold>You have received the following reward for reaching level " + level + ": " + reward.getName());
        return true;
    }
    
    @Override
    public @Nullable Component getDescription(int level) {
        // Here comes your code for getting the description which is most likely be displayed to the user when receiving the reward.
        // Example:
        return MiniMessage.miniMessage().deserialize(
                "Receiving a wonderful message for reaching level <level>",
                TagResolver.resolver("level", Tag.inserting(Component.text(level)))
        );
    }
    
    // Since your reward functionality is now implemented, you need methods for creating the RewardData.
    // If your reward type only works as a MilestoneReward or IntervalReward, you can remove on of those methods.
    // For example, if you have a custom reward condition (implementing RewardCondition), the reward should not be
    // creatable as an IntervalReward for safety reasons.

    // This is called when the reward is created as MilestoneReward
    public static MilestoneRewardData createMilestone(int level) {
        MyCustomReward reward = new MyCustomReward();
        return new MilestoneRewardData(reward, null, reward, true, level);
    }

    // This is called when the reward is created as IntervalReward
    public static IntervalRewardData createInterval(int start, int interval, int limit) {
        MyCustomReward reward = new MyCustomReward();
        return new IntervalRewardData(reward, null, reward, true, start, interval, limit);
    }
    
    // Now, the reward could be created and registered directly to the RewardManager.
    // But as explained in the previous section, this is not a good idea.
    // So we now need to create a RewardCreator which can then be registered to the RewardRegistry.
    // The RewardCreator converts the yml config to a RewardData.
    
    public static class Creator implements RewardCreator {
        
        public Creator() {}

        @Override
        public @Nullable MilestoneRewardData createMilestoneReward(@NotNull DataStorage data) {
            return createMilestone(data.optInt("level", 0));
        }

        @Override
        public @Nullable IntervalRewardData createIntervalReward(@NotNull DataStorage data) {
            return createInterval(data.optInt("start", 1), data.optInt("interval", 1), data.optInt("limit", -1));
        }
        
    }
    
    // You also don't need to implement both methods here if your reward only supports Milestone or Interval.
    
    // You have now created your custom reward type.
    // To use it, you need to register it to the RewardRegistry.
    
}
```

#### RewardRegistry
The RewardRegistry stores RewardCreators which are then converting the yml from the `rewards.yml` to a RewardData.
  
You can use the RewardRegistry like that:
```java
PlayerLevelsAPI api = PlayerLevelsAPIProvider.getApi();
RewardsRegistry registry = api.getRewardsRegistry();

registry.registerCreator("my_custom_reward", new MyCustomReward.Creator());
```
  
You can now create a reward with your custom reward type in the `rewards.yml`:
```yml
test_milestone_reward:
  name: Milestone Test
  apply_type: milestone
  type: my_custom_reward  # <-- Your custom reward type
  level: 10
  # If you have added more attributes in your RewardCreator, you can add them here.
```

```yml
test_interval_reward:
  name: Interval Test
  apply_type: interval
  type: my_custom_reward  # <-- Your custom reward type
  start: 1
  interval: 1
  limit: -1
  # If you have added more attributes in your RewardCreator, you can add them here.
```
