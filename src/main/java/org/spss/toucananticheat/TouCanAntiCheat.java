package org.spss.toucananticheat;

import org.bukkit.plugin.java.JavaPlugin;

public final class TouCanAntiCheat extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("I am toucan");
        OutgoingTest.register();
        this.getServer().getPluginManager().registerEvents(new AntiNuke(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static TouCanAntiCheat getInstance() {
        return getPlugin(TouCanAntiCheat.class);
    }
}
