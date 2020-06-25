//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package net.runelite.client.plugins.tzhaartimers;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import net.runelite.client.ui.overlay.infobox.InfoBox;

public class TzhaarTimers extends InfoBox {
	private final Instant startTime;
	private LocalTime time;
	private Instant lastTime;

	public TzhaarTimers(BufferedImage image, TzhaarTimersPlugin plugin, Instant startTime, Instant lastTime) {
		super(image, plugin);
		this.startTime = startTime;
		this.lastTime = lastTime;
	}

	public String getText() {
		if (this.startTime == null) {
			return "";
		} else {
			Duration elapsed;
			if (this.lastTime == null) {
				elapsed = Duration.between(this.startTime, Instant.now());
				this.time = LocalTime.ofSecondOfDay(elapsed.getSeconds());
			} else {
				elapsed = Duration.between(this.startTime, this.lastTime);
				this.time = LocalTime.ofSecondOfDay(elapsed.getSeconds());
			}

			return this.time.getHour() > 0 ? this.time.format(DateTimeFormatter.ofPattern("HH:mm")) : this.time.format(DateTimeFormatter.ofPattern("mm:ss"));
		}
	}

	public Color getTextColor() {
		return Color.WHITE;
	}

	public String getTooltip() {
		StringBuilder builder = new StringBuilder();
		builder.append("Elapsed time: ");
		builder.append(this.time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
		return builder.toString();
	}
}
