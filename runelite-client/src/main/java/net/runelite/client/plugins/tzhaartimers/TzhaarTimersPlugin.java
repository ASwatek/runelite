//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.runelite.client.plugins.tzhaartimers;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.util.Text;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
	name = "Tzhaar Timers",
	description = "Display elapsed time in the Fight Caves and Inferno",
	tags = {"inferno", "fight", "caves", "cape", "timer", "tzhaar"}
)
public class TzhaarTimersPlugin extends Plugin {
	private static final Logger log = LoggerFactory.getLogger(TzhaarTimersPlugin.class);
	private static final String START_MESSAGE = "Wave: 1";
	private static final String WAVE_MESSAGE = "Wave:";
	private static final String DEFEATED_MESSAGE = "You have been defeated!";
	private static final String INFERNO_COMPLETE_MESSAGE = "Your TzKal-Zuk kill count is:";
	private static final String FIGHT_CAVES_COMPLETE_MESSAGE = "Your TzTok-Jad kill count is:";
	private static final String INFERNO_PAUSED_MESSAGE = "The Inferno has been paused. You may now log out.";
	private static final String FIGHT_CAVE_PAUSED_MESSAGE = "The Fight Cave has been paused. You may now log out.";
	private static final String WAVE9 = "Wave: 9";
	private static final String WAVE18 = "Wave: 18";
	private static final String WAVE25 = "Wave: 25";
	private static final String WAVE35 = "Wave: 35";
	private static final String WAVE42 = "Wave: 42";
	private static final String WAVE50 = "Wave: 50";
	private static final String WAVE57 = "Wave: 57";
	private static final String WAVE60 = "Wave: 60";
	private static final String WAVE63 = "Wave: 63";
	private static final String WAVE66 = "Wave: 66";
	private static final String WAVE67 = "Wave: 67";
	private static final String WAVE68 = "Wave: 68";
	private static final String WAVE69 = "Wave: 69";
	@Inject
	private InfoBoxManager infoBoxManager;
	@Inject
	private Client client;
	@Inject
	private TzhaarTimersConfig config;
	@Inject
	private ItemManager itemManager;
	@Inject
	private ConfigManager configManager;
	private TzhaarTimers timer;
	private Instant startTime;
	private Instant originalTime;
	private Instant lastTime;
	private boolean started;
	private boolean loggingIn;
	private LocalTime time;

	public TzhaarTimersPlugin() {
	}

	@Provides
	TzhaarTimersConfig getConfig(ConfigManager configManager) {
		return (TzhaarTimersConfig)configManager.getConfig(TzhaarTimersConfig.class);
	}

	protected void shutDown() throws Exception {
		this.removeTimer();
		this.resetConfig();
		this.startTime = null;
		this.originalTime = null;
		this.lastTime = null;
		this.started = false;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (event.getGroup().equals("tzhaartimers")) {
			if (event.getKey().equals("tzhaarTimers")) {
				this.updateInfoBoxState();
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		switch(event.getGameState()) {
			case LOGGED_IN:
				if (this.loggingIn) {
					this.loggingIn = false;
					this.loadConfig();
					this.resetConfig();
				}
				break;
			case LOGGING_IN:
				this.loggingIn = true;
				break;
			case LOADING:
				if (!this.loggingIn) {
					this.updateInfoBoxState();
				}
				break;
			case HOPPING:
				this.loggingIn = true;
			case LOGIN_SCREEN:
				this.removeTimer();
				this.saveConfig();
		}

	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		if (this.config.tzhaarTimers() && (event.getType() == ChatMessageType.GAMEMESSAGE || event.getType() == ChatMessageType.SPAM)) {
			String message = Text.removeTags(event.getMessage());
			Instant now = Instant.now();
			if (!this.started && message.contains("Wave: 1")) {
				this.started = true;
				now = now.minus(6L, ChronoUnit.SECONDS);
				if (this.checkInFightCaves()) {
					this.createTimer(6570, now, (Instant)null);
				}

				if (this.checkInInferno()) {
					this.createTimer(21295, now, (Instant)null);
				}

				this.startTime = now;
				this.originalTime = now;
				return;
			}

			if (this.started) {
				if (message.contains("Wave:")) {
					if (this.lastTime != null) {
						this.startTime = this.startTime.plus(Duration.between(this.startTime, now)).minus(Duration.between(this.startTime, this.lastTime));
						this.lastTime = null;
					}

					if (this.checkInFightCaves()) {
						this.infoBoxManager.removeInfoBox(this.timer);
						this.createTimer(6570, this.startTime, this.lastTime);
					}

					if (this.checkInInferno()) {
						this.infoBoxManager.removeInfoBox(this.timer);
						this.createTimer(21295, this.startTime, this.lastTime);
					}
				}

				if (message.contains("The Fight Cave has been paused. You may now log out.") || message.contains("The Inferno has been paused. You may now log out.")) {
					if (this.checkInFightCaves()) {
						this.infoBoxManager.removeInfoBox(this.timer);
						this.createTimer(6570, this.startTime, now);
					}

					if (this.checkInInferno()) {
						this.infoBoxManager.removeInfoBox(this.timer);
						this.createTimer(21295, this.startTime, now);
					}

					this.lastTime = now;
				}

				if (message.contains("Wave: 9") || message.contains("Wave: 18") || message.contains("Wave: 25") || message.contains("Wave: 35") || message.contains("Wave: 42") || message.contains("Wave: 50") || message.contains("Wave: 57") || message.contains("Wave: 60") || message.contains("Wave: 63") || message.contains("Wave: 66") || message.contains("Wave: 67") || message.contains("Wave: 68") || message.contains("Wave: 69")) {
					this.printMessage();
				}

				if (message.contains("You have been defeated!") || message.contains("Your TzKal-Zuk kill count is:") || message.contains("Your TzTok-Jad kill count is:")) {
					this.removeTimer();
					this.resetConfig();
					this.startTime = null;
					this.lastTime = null;
					this.started = false;
				}
			}
		}

	}

	private void printMessage() {
		if (this.originalTime != null) {
			Instant now = Instant.now();
			this.time = LocalTime.ofSecondOfDay(now.getEpochSecond() - this.originalTime.getEpochSecond());
			if (this.time.getHour() > 0) {
				this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>Any splitters: </col=ff0000>" + this.time.format(DateTimeFormatter.ofPattern("HH:mm")), (String)null);
			} else {
				this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "<col=ff0000>Any splitters: </col=ff0000>" + this.time.format(DateTimeFormatter.ofPattern("mm:ss")), (String)null);
			}

		}
	}

	private void updateInfoBoxState() {
		if (this.timer != null) {
			if (!this.checkInFightCaves() && !this.checkInInferno() || !this.config.tzhaarTimers()) {
				this.removeTimer();
				this.resetConfig();
				this.startTime = null;
				this.lastTime = null;
				this.started = false;
			}

		}
	}

	private boolean checkInFightCaves() {
		return this.client.getMapRegions() != null && Arrays.stream(this.client.getMapRegions()).filter((x) -> {
			return x == 9551;
		}).toArray().length > 0;
	}

	private boolean checkInInferno() {
		return this.client.getMapRegions() != null && Arrays.stream(this.client.getMapRegions()).filter((x) -> {
			return x == 9043;
		}).toArray().length > 0;
	}

	private void removeTimer() {
		this.infoBoxManager.removeInfoBox(this.timer);
		this.timer = null;
	}

	private void createTimer(int id, Instant time, Instant lTime) {
		this.timer = new TzhaarTimers(this.itemManager.getImage(id), this, time, lTime);
		this.infoBoxManager.addInfoBox(this.timer);
	}

	private void loadConfig() {
		this.startTime = (Instant)this.configManager.getConfiguration("tzhaartimers", "time", Instant.class);
		Boolean temp = (Boolean)this.configManager.getConfiguration("tzhaartimers", "started", Boolean.class);
		if (temp != null) {
			this.started = temp;
		}

		this.lastTime = (Instant)this.configManager.getConfiguration("tzhaartimers", "lasttime", Instant.class);
	}

	private void resetConfig() {
		this.configManager.unsetConfiguration("tzhaartimers", "time");
		this.configManager.unsetConfiguration("tzhaartimers", "started");
		this.configManager.unsetConfiguration("tzhaartimers", "lasttime");
	}

	private void saveConfig() {
		if (this.startTime != null) {
			this.resetConfig();
			if (this.lastTime == null) {
				this.lastTime = Instant.now();
			}

			this.configManager.setConfiguration("tzhaartimers", "time", this.startTime);
			this.configManager.setConfiguration("tzhaartimers", "started", this.started);
			this.configManager.setConfiguration("tzhaartimers", "lasttime", this.lastTime);
			this.startTime = null;
			this.lastTime = null;
			this.started = false;
			this.originalTime = null;
		}

	}

	public TzhaarTimers getTimer() {
		return this.timer;
	}
}
