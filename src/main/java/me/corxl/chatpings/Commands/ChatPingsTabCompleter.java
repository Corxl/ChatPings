package me.corxl.chatpings.Commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChatPingsTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            String[] options = new String[]{"enable", "disable", "set"};
            return Arrays.stream(options).collect(Collectors.toList());
        } else if (args[0].equalsIgnoreCase("set")) {
            if (args.length == 2) {
                return Arrays.stream(Sound.values()).map(s -> s.name()).filter(name -> name.startsWith(args[1]) && !name.contains("LOOP")).collect(Collectors.toList());
            }
            else if (args.length == 3 || args.length == 4) {
                return new ArrayList<>(List.of("1.0f"));
            }
        }

        return null;
    }
}
