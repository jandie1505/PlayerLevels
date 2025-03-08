package net.jandie1505.playerlevels.commands.utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OptionParser {

    public static Result parse(@NotNull String[] arguments) {
        List<String> args = new ArrayList<>(Arrays.asList(arguments));
        Map<@NotNull String, @NotNull String> options = new HashMap<>();

        Iterator<String> iterator = args.iterator();
        while (iterator.hasNext()) {
            String arg = iterator.next();

            // Parse long options with their values
            if (arg.startsWith("--")) {
                arg = arg.substring(2);

                String[] split = arg.split("=", 2);
                options.put(split[0], split.length > 1 ? split[1] : "");

                iterator.remove();
                continue;
            }

            if (arg.startsWith("-")) {
                arg = arg.substring(1);

                for (char c : arg.toCharArray()) {
                    options.put(String.valueOf(c), "");
                }

                iterator.remove();
                continue;
            }

        }

        return new Result(args.toArray(String[]::new), options);
    }

    public record Result(String[] args, Map<@NotNull String, @NotNull String> options) {

        public boolean hasOption(@NotNull String... option) {

            for (String s : option) {
                if (this.options.containsKey(s)) {
                    return true;
                }
            }

            return false;
        }

    }

}
