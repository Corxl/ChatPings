package me.corxl.chatpings;

import me.corxl.chatpings.Commands.ChatPingsCommand;
import me.corxl.chatpings.Commands.ChatPingsTabCompleter;
import me.corxl.chatpings.Listeners.ChatListener;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChatPings extends JavaPlugin {
    public static NamespacedKey chatPingKey;
    public static NamespacedKey doNotShowKey;

    public static NamespacedKey chatSound, chatSoundPitch, chatSoundVolume;
    public static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&9[&aChatPings&9]&r");

    public static boolean enabledByDefault, sendNotificationOnJoin;
    @Override
    public void onEnable() {
        // Plugin startup logic
        chatPingKey = new NamespacedKey(this, "chat_ping");
        doNotShowKey = new NamespacedKey(this, "chat_donotshow");

        chatSound = new NamespacedKey(this, "chat_sound");
        chatSoundPitch = new NamespacedKey(this, "chat_pitch");
        chatSoundVolume = new NamespacedKey(this, "chat_volume");

        this.getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        this.getCommand("chatpings").setExecutor(new ChatPingsCommand());
        this.getCommand("chatpings").setTabCompleter(new ChatPingsTabCompleter());

        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        this.saveConfig();

        this.loadConfig();

        this.getServer().getOnlinePlayers().forEach((p) -> {
            if (!p.getPersistentDataContainer().has(doNotShowKey, PersistentDataType.BOOLEAN) || !p.getPersistentDataContainer().has(chatPingKey, PersistentDataType.BOOLEAN)) {
                setPlayerToDefault(p);
            }
        });

    }

    private void loadConfig() {
        enabledByDefault = this.getConfig().getBoolean("enabled-by-default");
        sendNotificationOnJoin = this.getConfig().getBoolean("send-notification-on-join");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void setPlayerToDefault(Player p) {
        PersistentDataContainer c = p.getPersistentDataContainer();
        c.set(doNotShowKey, PersistentDataType.BOOLEAN, !sendNotificationOnJoin);
        c.set(chatPingKey, PersistentDataType.BOOLEAN, enabledByDefault);
        c.remove(chatSound);
        c.remove(chatSoundVolume);
        c.remove(chatSoundPitch);

    }


    public static String toColor(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
