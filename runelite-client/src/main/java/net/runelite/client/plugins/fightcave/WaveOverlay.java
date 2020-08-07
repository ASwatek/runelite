package net.runelite.client.plugins.fightcave;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.components.table.TableAlignment;
import net.runelite.client.ui.overlay.components.table.TableComponent;

class WaveOverlay extends Overlay
{
    private static final Color HEADER_COLOR = ColorScheme.BRAND_BLUE;

    private final FightCaveConfig config;
    private final FightCavePlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private WaveOverlay(final FightCaveConfig config, final FightCavePlugin plugin)
    {
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_RIGHT);
        determineLayer();
    }

    private static Collection<String> buildWaveLines(final Map<WaveMonster, Integer> wave)
    {
        final List<Map.Entry<WaveMonster, Integer>> monsters = new ArrayList<>(wave.entrySet());
        monsters.sort(Map.Entry.comparingByKey());
        final List<String> outputLines = new ArrayList<>();

        for (Map.Entry<WaveMonster, Integer> monsterEntry : monsters)
        {
            final WaveMonster monster = monsterEntry.getKey();
            final int quantity = monsterEntry.getValue();
            final String line = FightCavePlugin.formatMonsterQuantity(monster, quantity);

            outputLines.add(line);
        }

        return outputLines;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isValidRegion() || plugin.getCurrentWave() < 0 || config.waveDisplay() == WaveDisplayMode.NONE)
        {
            return null;
        }

        panelComponent.getChildren().clear();

        final int currentWave = plugin.getCurrentWave();
        final int waveIndex = currentWave - 1;

        if (config.waveDisplay() == WaveDisplayMode.CURRENT
                || config.waveDisplay() == WaveDisplayMode.BOTH)
        {
            final Map<WaveMonster, Integer> waveContents = FightCavePlugin.getWAVES().get(waveIndex);

            addWaveInfo("Wave " + plugin.getCurrentWave(), waveContents);
        }

        if ((config.waveDisplay() == WaveDisplayMode.NEXT
                || config.waveDisplay() == WaveDisplayMode.BOTH)
                && currentWave != FightCavePlugin.MAX_WAVE)
        {
            final Map<WaveMonster, Integer> waveContents = FightCavePlugin.getWAVES().get(waveIndex + 1);

            addWaveInfo("Next wave", waveContents);
        }
        panelComponent.getChildren().add(TitleComponent.builder().text("Rotation " + plugin.rotation).color(HEADER_COLOR).build());

        return panelComponent.render(graphics);
    }

    private void addWaveInfo(final String headerText, final Map<WaveMonster, Integer> waveContents)
    {
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(headerText)
                .color(HEADER_COLOR)
                .build());


        TableComponent tableComponent = new TableComponent();
        tableComponent.setColumnAlignments(TableAlignment.CENTER);

        for (String line : buildWaveLines(waveContents))
        {
            tableComponent.addRow(line);
        }

        if (!tableComponent.isEmpty())
        {
            panelComponent.getChildren().add(tableComponent);
        }
    }

    public void determineLayer()
    {
        if (config.mirrorMode())
        {
            setLayer(OverlayLayer.AFTER_MIRROR);
        }
    }
}
