package me.northcode.SQLHelp;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class SQLHelp extends JavaPlugin {

	public static SQLHelp plugin;
	public final Logger logger = Logger.getLogger("Minecraft");

	private SQLFunctions sql;

	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();

		if (getServer().getPluginManager().getPlugin("SQLibrary") == null) {
			log(Level.SEVERE, "SQLHelp requires SQLibrary!");
			getServer().getPluginManager().disablePlugin(this);
		}

		log(Level.INFO, "version " + pdfFile.getVersion()
				+ " has been enabled!");
		getConfig().options().copyDefaults(true);
		saveConfig();

		sql = new SQLFunctions(this, "SQLHelp", getConfig().getString(
				"MySQL.host"), getConfig().getInt("MySQL.port"), getConfig()
				.getString("MySQL.database"), getConfig().getString(
				"MySQL.username"), getConfig().getString("MySQL.password"));
		sql.createTable();
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log(Level.INFO, "version " + pdfFile.getVersion()
				+ " has been disabled!");
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		Player player = (Player) sender;
		if (commandLabel.equalsIgnoreCase("help")) {
			String page = "";

			if (args.length == 0) {
				page = "main";
			} else {
				for (String a : args) {
					if (a != args[args.length - 1]) {
						page += a + "/";
					} else {
						page += a;
					}
				}
			}

			List<String> pages = null;
			try {
				pages = sql.select(page);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (getConfig().getBoolean("HelpDisplay.header.enabled")) {
				page = page.replace("/",
						getConfig().getString("HelpDisplay.subPageSeperator"));

				String header = getConfig().getString("HelpDisplay.header.text");
				header = replaceColor(header);
				header = header.replace("%subpage%", page);

				player.sendMessage(header);
			}
			
			if (getConfig().getBoolean("HelpDisplay.footer.enabled")) {
				
			}
			
			if (pages != null) {
				try {
					String[] content = pages.get(0).split("/n");
					for (String a : content) {
						player.sendMessage(replaceColor(a));
					}
				} catch (Exception e) {
					player.sendMessage(replaceColor(getConfig().getString("HelpDisplay.notFoundMessage")));
				}
			}

		} else if (commandLabel.equalsIgnoreCase("getSQL")) {
			player.sendMessage(getConfig().getString("MySQL.host"));
			player.sendMessage(Integer.toString(getConfig()
					.getInt("MySQL.port")));
			player.sendMessage(getConfig().getString("MySQL.database"));
			player.sendMessage(getConfig().getString("MySQL.username"));
			player.sendMessage(getConfig().getString("MySQL.password"));
		} else {
			return false;
		}
		return true;
	}

	private void log(Level level, String msg) {
		PluginDescriptionFile pdfFile = this.getDescription();
		logger.log(level, "[" + pdfFile.getName() + "] " + msg);
	}

	private String replaceColor(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}
}
