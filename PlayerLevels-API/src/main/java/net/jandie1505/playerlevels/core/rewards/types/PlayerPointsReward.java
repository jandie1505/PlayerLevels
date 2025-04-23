package net.jandie1505.playerlevels.core.rewards.types;

import net.chaossquad.mclib.storage.DataStorage;
import net.jandie1505.playerlevels.api.core.level.Leveler;
import net.jandie1505.playerlevels.api.core.reward.Reward;
import net.jandie1505.playerlevels.core.PlayerLevelsAPIProvider;
import net.jandie1505.playerlevels.core.rewards.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is a reward that give a player a specific amount of points when applied.<br/>
 * You can get the data using {@link #createMilestone(String, String, int)}  or {@link #createInterval(String, String, int, int, int)}.<br/>
 * The reward uses a formula which has the variables level and xp. Level is the level the player leveled up to, the xp are the xp of that level.<br/>
 * A formula can look like this: level*2
 */
public class PlayerPointsReward implements RewardExecutor, RewardDescriptionProvider {
    @NotNull private final String formula;
    @Nullable private final String description;

    private PlayerPointsReward(@NotNull String formula, @Nullable String description) {
        this.formula = formula;
        this.description = description;
    }

    // ----- APPLY -----

    @Override
    public boolean onApply(@NotNull Reward reward, @NotNull Leveler player, int level) {

        try {
            Class.forName("org.black_ixx.playerpoints.PlayerPoints");
        } catch (ClassNotFoundException e) {
            return false;
        }

        PlayerPointsAPI api = PlayerPoints.getInstance().getAPI();
        if (api == null) return false;

        int points;
        try {
            points = this.calculateValues(level);
        } catch (Exception e) {
            return false;
        }

        // Don't give anything when 0, but still successful since no error occurred
        if (points <= 0) {
            return true;
        }

        api.give(player.getPlayerUUID(), points);
        return true;
    }

    private int calculateValues(int level) {

        double xp;
        double totalXp;
        try {
            xp = PlayerLevelsAPIProvider.getApi().getLevelManager().getXPForNextLevel(level - 1, level);
            totalXp = PlayerLevelsAPIProvider.getApi().getLevelManager().getXPForLevel(level);
        } catch (Exception e) {
            xp = 0;
            totalXp = 0;
        }

        if (xp <= 0) xp = 0;
        if (totalXp <= 0) totalXp = 0;

        Expression expression = new ExpressionBuilder(formula)
                .variables("level", "xp", "total_xp")
                .build()
                .setVariable("level", level)
                .setVariable("xp", xp)
                .setVariable("total_xp", totalXp);

        return (int) expression.evaluate();
    }

    @Override
    public @NotNull Component getDescription(int level) {

        if (this.description != null) {
            return MiniMessage.miniMessage().deserialize(
                    this.description,
                    TagResolver.resolver("level", Tag.inserting(level >= 0 ? Component.text(level) : Component.empty())),
                    TagResolver.resolver("points", Tag.inserting(level >= 0 ? Component.text(this.calculateValues(level)) : Component.empty()))
            );
        } else {
            return level >= 0 ? Component.text("+ " + this.calculateValues(level) + " points") : Component.empty();
        }
    }

    // ----- CREATE -----

    /**
     * Creates a milestone reward data for this PlayerPoints reward.<br/>
     * The reward can be registered at {@link net.jandie1505.playerlevels.api.core.reward.RewardsManager#addMilestoneReward(RewardConfig, MilestoneRewardData)}.
     * @param formula the formula that is used to give the points
     * @param description reward description (minimessage tags 'level' and 'points' work)
     * @param level the level the reward should be applied
     * @return milestone reward data
     */
    public static MilestoneRewardData createMilestone(
            @NotNull String formula,
            @Nullable String description,
            int level
    ) {
        PlayerPointsReward reward = new PlayerPointsReward(formula, description);
        return new MilestoneRewardData(reward, null, reward, true, level);
    }

    /**
     * Creates an interval reward data for this PlayerPoints reward.<br/>
     * The reward data can be registered at {@link net.jandie1505.playerlevels.api.core.reward.RewardsManager#addIntervalReward(RewardConfig, IntervalRewardData)}.
     * @param formula the formula that is used to give the points
     * @param description reward description (minimessage tags 'level' and 'points' work)
     * @param start interval start
     * @param interval interval in levels the player should get the reward applied
     * @param limit the reward will not be applied for levels higher/equal than this
     * @return interval reward data
     */
    public static IntervalRewardData createInterval(
            @NotNull String formula,
            @Nullable String description,
            int start,
            int interval,
            int limit
    ) {
        PlayerPointsReward reward = new PlayerPointsReward(formula, description);
        return new IntervalRewardData(reward, null, reward, true, start, interval, limit);
    }

    // ----- OTHER STUFF -----

    /**
     * Returns the formula the points are calculated from.
     * @return formula
     */
    public @NotNull String getFormula() {
        return formula;
    }

    /**
     * Returns the stored description string (since {@link #getDescription(int)} returns the converted description.
     * @return "raw" description
     */
    public @Nullable String getRawDescription() {
        return this.description;
    }

    // ----- CREATOR -----

    /**
     * Creates a new PlayerPointsReward from config.
     */
    public static class Creator implements RewardCreator {

        /**
         * Creates the creator.
         */
        public Creator() {}

        @Override
        public @Nullable MilestoneRewardData createMilestoneReward(@NotNull DataStorage data) {

            String formula = data.optString("formula", null);
            if (formula == null) throw new IllegalArgumentException("formula is null");

            return createMilestone(
                    formula,
                    data.optString("description", null),
                    data.optInt("level", 1)
            );
        }

        @Override
        public @Nullable IntervalRewardData createIntervalReward(@NotNull DataStorage data) {

            String formula = data.optString("formula", null);
            if (formula == null) throw new IllegalArgumentException("formula is null");

            return createInterval(
                    formula,
                    data.optString("description", null),
                    data.optInt("start", 1),
                    data.optInt("interval", 1),
                    data.optInt("limit", 1)
            );
        }
    }

}
