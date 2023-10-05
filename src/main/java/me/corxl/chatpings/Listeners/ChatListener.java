package me.corxl.chatpings.Listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.corxl.chatpings.ChatPings;
import me.corxl.chatpings.Commands.ChatPingsCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ChatListener implements Listener {

    private final JavaPlugin plugin;
    private final Random r = new Random();

    public ChatListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // prompt player to select settings. Enable -- Disable -- Do not show again
        if (!event.getPlayer().getPersistentDataContainer().has(ChatPings.chatPingKey, PersistentDataType.BOOLEAN)
                || !event.getPlayer().getPersistentDataContainer().has(ChatPings.doNotShowKey, PersistentDataType.BOOLEAN)) {
            ChatPings.setPlayerToDefault(event.getPlayer());
        }
        if (event.getPlayer().getPersistentDataContainer().get(ChatPings.doNotShowKey, PersistentDataType.BOOLEAN)) return;

        event.getPlayer().sendMessage(ChatPings.toColor( "\n" + ChatPings.PREFIX + " &7Your chat pings are: " +
                (event.getPlayer().getPersistentDataContainer().get(ChatPings.chatPingKey, PersistentDataType.BOOLEAN) ? "&aEnabled" : "&cDisabled") + "&7!"));
        event.getPlayer().sendMessage(
                Component.text().append(
                        Component.text(ChatPings.toColor("\n       &f&n[Options]"))
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chatpings"))
                                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(ChatPings.toColor("&7Displays " + ChatPings.PREFIX + "&7's &nOptions&r&7.")))))
                        .append(Component.text("   "))
                        .append(Component.text(ChatPings.toColor("&6[Do Not Show Again]\n"))
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chatpings donotshow"))
                                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(ChatPings.toColor("&7Stops this message from being shown again.")))))
        );

    }

    @EventHandler
    public void onChatMessage(AsyncChatEvent event) {
        // Notify all players that a chat message was sent.
        List<Player> playersToPing = plugin.getServer().getOnlinePlayers().stream().filter(p -> p.getPersistentDataContainer().has(ChatPings.chatPingKey, PersistentDataType.BOOLEAN) && p.getPersistentDataContainer().get(ChatPings.chatPingKey, PersistentDataType.BOOLEAN)).collect(Collectors.toList());

        String text = ChatColor.stripColor(PlainTextComponentSerializer.plainText().serialize(event.message()));
        if (text.startsWith("@")) {
            String player = text.split(" ")[0].toLowerCase().replace("@", "");
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(player) || p.getDisplayName().toLowerCase().startsWith(player)) {
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 3.0f, 0.6f);
                }
            }
        } else {
            for (Player p : playersToPing) {
                if (p.getPersistentDataContainer().has(ChatPings.chatSound)) {
                    PersistentDataContainer c = p.getPersistentDataContainer();
                    Sound s = Sound.valueOf(c.get(ChatPings.chatSound, PersistentDataType.STRING));
                    float volume = c.get(ChatPings.chatSoundVolume, PersistentDataType.FLOAT);
                    float pitch = c.get(ChatPings.chatSoundPitch, PersistentDataType.FLOAT);
                    float rand = randFloat(pitch-0.2f < 0 ? 0.1f : pitch-0.2f, pitch+0.2f);
                    p.playSound(p.getLocation(), s, volume, rand);
                } else {
                    float rand = randFloat(0.2f, 0.9f);
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 3.0f, rand);

                }
            }
        }



    }

    private float randFloat(float min, float max) {
        if (min >= max) {
            throw new IllegalArgumentException("Invalid range [" + min + ", " + max + "]");
        }
        return (float) (min + Math.random() * (max - min));
    }
}
