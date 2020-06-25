//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.runelite.client.plugins.tzhaartimers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("tzhaartimers")
public interface TzhaarTimersConfig extends Config {
	String CONFIG_GROUP = "tzhaartimers";
	String CONFIG_TIME = "time";
	String CONFIG_STARTED = "started";
	String CONFIG_LASTTIME = "lasttime";

	@ConfigItem(
		keyName = "tzhaarTimers",
		name = "Display elapsed time in the Fight Caves and Inferno",
		description = "Display elapsed time in the Fight Caves and Inferno",
		hidden = true
	)
	default boolean tzhaarTimers() {
		return true;
	}
}
