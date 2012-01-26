package com.Evilgeniuses.Hardcore;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class Listeners implements Listener {

	private HardcorePlugin _plugin = null;

	public Listeners(HardcorePlugin plugin) {
		_plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerDeath(PlayerDeathEvent event) {
		// make sure it is a player
		if (!(event instanceof PlayerDeathEvent))
			return;

		Player player = (Player) event.getEntity();
		PlayerDeathEvent de = (PlayerDeathEvent) event;

		// don't add the player more than once to the list
		if (!_plugin.getDeadPlayerList().isPlayerDead(player.getName(), false)) {
			_plugin.getDeadPlayerList().addPlayer(player, de.getDeathMessage());

			// they won't get another farewell message if they die during
			// farewell
			if (_plugin.getHardcoreConfiguration().finalFarewell) {
				doFinalFarewellMessage(player);
			}
		}

		if (_plugin.getHardcoreConfiguration().doThunderAndLightningOnDeath)
			doSoundAndFury(player.getLocation(), player.getWorld());
	}	
	
	
	private void doFinalFarewellMessage(Player player) {
		player.sendMessage(ChatColor.RED
				+ "You have died. You have been granted "
				+ _plugin.getHardcoreConfiguration().finalFarewellSeconds
				+ " seconds to say your final farewell.");

	}
	
	
	public void doSoundAndFury(Location where, World whatWorld) {
		whatWorld.strikeLightningEffect(where);
		whatWorld.setThunderDuration(_plugin.getHardcoreConfiguration().thunderLengthSeconds*20);
		whatWorld.setThundering(true);

	}
	
	
	
	

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerLogin(PlayerLoginEvent event) {

		String playername = event.getPlayer().toString();

		_plugin.log(playername + " is trying to log in.");

		// if you get disconnected during your farewell, you can still reconnect
		// within the farewell counter
		if (_plugin.getDeadPlayerList().isPlayerDead(playername, true)) {
			String livedate = _plugin.getDeadPlayerList()
					.whenWillPlayerLive(playername).toString();
			event.setKickMessage("You will be dead until " + livedate);
			event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
			_plugin.log(playername
					+ " was not allowed to login for being dead until "
					+ livedate + ".");
		}
	}

}