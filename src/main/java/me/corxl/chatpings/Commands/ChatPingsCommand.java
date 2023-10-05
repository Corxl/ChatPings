package me.corxl.chatpings.Commands;

import me.corxl.chatpings.ChatPings;
import me.corxl.chatpings.Util.Pages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ChatPingsCommand implements CommandExecutor {

    private HashMap<String, Pages> playerPages;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equals("chatpings")) return false;
        if (sender instanceof ConsoleCommandSender) return false;
        Player p = (Player) sender;
        if (command.getPermission() != null && !p.hasPermission(command.getPermission())) return false;
        if (args.length == 0) {
            handleOptions(p);
            return true;
        }else if (args.length == 1) {
            String arg = args[0];
            if (arg.equalsIgnoreCase("enable") || arg.equalsIgnoreCase("disable")) {
                handleToggle(p, arg.equalsIgnoreCase("enable"));
            } else if (arg.equalsIgnoreCase("clear")) {
                handleClear(p);
            } else if (arg.equalsIgnoreCase("donotshow")) {
                handleDoNotShow(p);
            }
            else {
                handleIncorrectInput(p);
            }
            return true;
        } else {
            if (args[0].equalsIgnoreCase("set")) {
                if (args.length != 4){
                    handleIncorrectSetInput(p);
                    return true;
                }

                handleSet(p, args);
                return true;
            }
        }
        return false;
    }
    public void handleOptions(Player p) {
        String message = "\n&l&7----" + ChatPings.PREFIX + " Options&l&7----\n";
        TextComponent options = Component.text(ChatPings.toColor(message));
        TextComponent space = Component.text("  ")
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, ""));

        TextComponent enable = Component.text(ChatPings.toColor(" &9[&aEnable&9] "))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chatpings enable"))
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(ChatPings.toColor("&a&lEnables chat pings"))));

        TextComponent disable = Component.text(ChatPings.toColor("&9[&cDisable&9]  "))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chatpings disable"))
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(ChatPings.toColor("&c&lDisables chat pings"))));

        TextComponent clear = Component.text(ChatPings.toColor(" &9[&rClear&9] "))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chatpings clear"))
                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(ChatPings.toColor("&f&lClears chat ping settings"))));

        options = options
                .append(Component.newline())
                .append(enable)
                .append(space)
                .append(disable)
                .append(clear)
                .append(Component.newline())
                .append(Component.text(ChatPings.toColor("\n&l&7------------------------\n")));

        p.sendMessage(options);
    }

    private void handleSet(Player p, String[] args) {
        try {
            Sound s = Sound.valueOf(args[1].toUpperCase());
            float volume = Float.parseFloat(args[2]);
            float pitch = Float.parseFloat(args[3]);

            PersistentDataContainer c = p.getPersistentDataContainer();
            c.set(ChatPings.chatSound, PersistentDataType.STRING, s.toString());
            c.set(ChatPings.chatSoundVolume, PersistentDataType.FLOAT, volume);
            c.set(ChatPings.chatSoundPitch, PersistentDataType.FLOAT, pitch);

            p.sendMessage(ChatPings.toColor(ChatPings.PREFIX + " &7Your ping sound is now: &6" + s.name()));
        } catch (IllegalArgumentException e) {
            handleIncorrectSetInput(p);
        }
    }
    private void handleSoundDisplay(Player p) {
        List<Sound> sounds = Arrays.stream(Sound.values()).collect(Collectors.toList());
        List<TextComponent> soundComponents = sounds.stream().map((s) -> soundComponent(s)).collect(Collectors.toList());
        Pages soundPage = new Pages(6, soundComponents);
        p.sendMessage(soundPage.displayPageContent());
    }

    private TextComponent soundComponent(Sound s) {
        String sound = s.toString();
        return Component.empty().append(
                Component.text(ChatPings.toColor("| &9[&c▶&9] "))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chatpings playsound " + sound))
                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(ChatPings.toColor("&7Play Sound"))))
                ).append(Component.text(ChatPings.toColor(" &9[&f✄&9]"))
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chatpings playsound " + sound))
                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(ChatPings.toColor("&7Play Sound"))))
                ).append(Component.text(ChatPings.toColor("&6" + sound)));
    }

    private void handleToggle(Player p, boolean enabled) {
        boolean sameSetting = p.getPersistentDataContainer().get(ChatPings.chatPingKey, PersistentDataType.BOOLEAN) == enabled;
        String handleType = enabled ? "&aEnabled" : "&cDisabled";
        if (sameSetting) {
            p.sendMessage(ChatPings.toColor(ChatPings.PREFIX + " &7Pings are already " + handleType + "!"));
            return;
        }

        p.getPersistentDataContainer().set(ChatPings.chatPingKey, PersistentDataType.BOOLEAN, enabled);
        p.sendMessage(ChatPings.toColor(ChatPings.PREFIX + " &7You have " + handleType + " &7chat pings!"));
    }

    private void handleDoNotShow(Player p) {
        p.getPersistentDataContainer().set(ChatPings.doNotShowKey, PersistentDataType.BOOLEAN, true);
        p.sendMessage(ChatPings.toColor(ChatPings.PREFIX + " &6You will no longer see this prompt."));
        p.sendMessage(ChatPings.toColor(ChatPings.PREFIX + " Type &7/chatpings &6to modify your options."));
    }

    private void handleClear(Player p) {
        ChatPings.setPlayerToDefault(p);
        // notify player
        p.sendMessage(ChatPings.toColor( ChatPings.PREFIX + " &7Your settings have been reset."));
    }

    private void handleIncorrectInput(Player p) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7Incorrect use of &c/chatpings&r\n&9/chatpings <&7 enable &9|&7 disable &9|&7 clear &9|&7 set &9> "));
    }

    private void handleIncorrectSetInput(Player p) {
        handleIncorrectInput(p);
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&9/chatpings set <SOUND> <VOLUME-[0-5]> <PITCH-[0-3]>"));
    }
}
