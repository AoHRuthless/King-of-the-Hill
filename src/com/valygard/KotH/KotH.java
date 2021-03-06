/**
 * KotH.java is part of King of the Hill.
 */
package com.valygard.KotH;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.valygard.KotH.command.CommandManager;
import com.valygard.KotH.economy.EconomyManager;
import com.valygard.KotH.framework.Arena;
import com.valygard.KotH.framework.ArenaManager;
import com.valygard.KotH.listener.GlobalListener;
import com.valygard.KotH.matchmaking.KotHRatingSystem;
import com.valygard.KotH.messenger.KotHLogger;
import com.valygard.KotH.messenger.Msg;
import com.valygard.KotH.util.ConfigUtil;

/**
 * @author Anand
 * 
 */
public class KotH extends JavaPlugin {
	// Vault
	private Economy econ;

	// Classes
	private ArenaManager am;
	private CommandManager cm;
	private EconomyManager em;
	private KotHRatingSystem matchmaking;

	private File cfgFile;
	private FileConfiguration cfg;

	public static final int ECONOMY_ID = -69;

	public void onEnable() {
		cfgFile = new File(getDataFolder(), "config.yml");
		cfg = new YamlConfiguration();

		reloadConfig();

		// Set the header and save
		cfg.options().header(getHeader());
		saveConfig();

		// Define all variables, such as class instances
		initializeVariables();

		// Load all arenas and classes
		am.initialize();

		// Load dependencies
		loadVault();

		// Load the messages file.
		loadMessagesFile();

		// Register the command base
		registerCommands();

		// Register our listeners
		registerListeners();
	}

	public void onDisable() {
		// End all arenas
		for (Arena arena : am.getArenas()) {
			if (arena.isRunning()) {
				for (Player p : arena.getPlayersInArena()) {
					arena.getScoreboard().removePlayer(p);
				}
				arena.forceEnd();
				continue;
			}
			for (Player p : arena.getPlayersInLobby()) {
				arena.removePlayer(p, false);
				p.closeInventory();
			}
		}
	}

	private void initializeVariables() {
		am = new ArenaManager(this);
		cm = new CommandManager(this);
		em = new EconomyManager(this);
		matchmaking = new KotHRatingSystem(am);

		KotHLogger.setLogger(this);
	}

	private void registerCommands() {
		getCommand("koth").setExecutor(cm);
		getCommand("kingofthehill").setExecutor(cm);
	}

	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new GlobalListener(this),
				this);
	}

	public boolean has(Player p, String s) {
		return p.hasPermission(s);
	}

	public boolean has(CommandSender sender, String s) {
		if (sender instanceof ConsoleCommandSender || sender instanceof ProxiedCommandSender
				|| sender instanceof BlockCommandSender) {
			return true;
		}
		return has((Player) sender, s);
	}

	private void loadVault() {
		Plugin vault = getServer().getPluginManager().getPlugin("Vault");
		if (vault == null) {
			KotHLogger.getLogger().warn("Economy rewards cannot function without vault.");
			return;
		}

		ServicesManager manager = this.getServer().getServicesManager();
		RegisteredServiceProvider<Economy> e = manager
				.getRegistration(net.milkbowl.vault.economy.Economy.class);

		if (e != null) {
			econ = e.getProvider();
			KotHLogger.getLogger().info("Vault v" + vault.getDescription().getVersion()
					+ " has been found! Economy rewards enabled.");
		} else {
			KotHLogger.getLogger().warn("Vault found, but no economy plugin detected ... Economy rewards will not function!");
		}
	}

	/**
	 * The idea for this was not created by me (AoH_Ruthless). The original
	 * author is 'gomeow', and the idea for storing messages in a file was taken
	 * from the Original post at
	 * <http://forums.bukkit.org/threads/language-files.149837/> However, I
	 * (AoH_Ruthless) tweaked it heavily to suit my own needs.
	 * 
	 */
	private void loadMessagesFile() {
		// Create if missing
		File file = new File(getDataFolder(), "messages.yml");
		try {
			if (file.createNewFile()) {
				KotHLogger.getLogger().info("messages.yml created.");
				YamlConfiguration yaml = Msg.toYaml();
				yaml.save(file);
				return;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			KotHLogger.getLogger().error("Could not create messages.yml!");
			KotHLogger.getLogger().error("The plugin cannot work without messages; disabling plugin.");
			setEnabled(false);
		}

		// Otherwise, load the messages from the file
		try {
			YamlConfiguration yaml = new YamlConfiguration();
			yaml.load(file);
			ConfigUtil.addMissingRemoveObsolete(file, Msg.toYaml(), yaml);
			Msg.load(yaml);
		}
		catch (Exception e) {
			e.printStackTrace();
			KotHLogger.getLogger().error("Could not load messages.yml!");
			KotHLogger.getLogger().error("The plugin cannot work without messages; disabling plugin.");
			setEnabled(false);
		}
	}

	@Override
	public FileConfiguration getConfig() {
		return cfg;
	}

	@Override
	public void saveConfig() {
		try {
			cfg.save(cfgFile);
		}
		catch (IOException e) {
			getLogger().severe(
					"Could not save config.yml due to: " + e.getMessage());
		}
	}

	@Override
	public void reloadConfig() {
		if (!cfgFile.exists()) {
			getLogger().info("Creating new config.yml ...");
			saveDefaultConfig();
		}
		Scanner scan = null;
		try {
			scan = new Scanner(new File(getDataFolder(), "config.yml"));

			int row = 0;
			String line = "";

			while (scan.hasNextLine()) {
				line = scan.nextLine();
				row++;

				if (line.indexOf("\t") != -1) {
					StringBuilder sb = new StringBuilder();
					sb.append("Tab found in config-file on line # ")
							.append(row).append("!");
					sb.append('\n')
							.append("Never use tabs. Always use spaces.");
					throw new IllegalArgumentException(sb.toString());
				}
			}
			cfg.load(cfgFile);
		}
		catch (FileNotFoundException ex) {
			throw new IllegalStateException(
					"Config-file unsuccessfully created!");
		}
		catch (IOException e) {
			getLogger().severe(
					"There was an error reading the config-file:" + "\n"
							+ e.getMessage());
		}
		catch (InvalidConfigurationException e) {
			throw new RuntimeException(
					"\n\n>>>\n>>> Error in your config! \n>>> SnakeYaml says:\n>>>\n\n"
							+ e.getMessage());
		}
		finally {
			if (scan != null) {
				scan.close();
			}
		}
	}

	private String getHeader() {
		String s = System.getProperty("line.separator");

		return "King of the Hill v"
				+ getDescription().getVersion()
				+ " - configuration file"
				+ s
				+ "To report bugs, give feedback, or suggest improvements, please use the issue tracker:"
				+ s
				+ "<https://github.com/AoHRuthless/King-of-the-Hill/issues>"
				+ s + s + "Happy Configuring.";
	}

	public ArenaManager getArenaManager() {
		return am;
	}

	public EconomyManager getEconomyManager() {
		return em;
	}

	public KotHRatingSystem getRatingSystem() {
		return matchmaking;
	}

	public File getPluginFile() {
		return getFile();
	}

	public Economy getEconomy() {
		return econ;
	}
}
