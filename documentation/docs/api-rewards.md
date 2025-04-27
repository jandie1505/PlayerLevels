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
If you are creating custom rewards, register a RewardCreator to the RewardsRegistry as described in another section below.**

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

### RewardsRegistry and custom reward types
