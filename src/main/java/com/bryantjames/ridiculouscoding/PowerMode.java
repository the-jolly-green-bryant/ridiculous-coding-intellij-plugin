package com.bryantjames.ridiculouscoding;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.bryantjames.ridiculouscoding.color.ColorEdges;
import com.bryantjames.ridiculouscoding.listeners.MyTypedActionHandler;
import com.bryantjames.ridiculouscoding.management.ElementContainerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


@State(name = "RidiculousCoding", storages = @Storage("ridiculous.coding.xml"))
public class PowerMode implements PersistentStateComponent<PowerMode>,
  ApplicationComponent {
  private static final List<Integer> HOT_INPUTS = Arrays.asList(
    InputEvent.CTRL_DOWN_MASK,
    InputEvent.ALT_DOWN_MASK,
    InputEvent.SHIFT_DOWN_MASK
  );
  private boolean hotkeyHeatup = true;
  private long bamLife = 1000;
  private double gravityFactor = 21.21;
  private double sparkVelocityFactor = 4.36;
  private int sparkSize = 3;
  private int frameRate = 30;
  private int maxFlameSize = 100;
  private int maxFlameLife = 2000;
  private int heatupTime = 10000;
  private Map<KeyStroke, Long> lastKeys = new HashMap<>();

  private int keyStrokesPerMinute = 300;
  private final double hotkeyWeight = keyStrokesPerMinute * 0.05;
  private double heatupFactor = 1.0;
  private int sparkLife = 3000;
  private int sparkCount = 10;
  private int shakeRange = 4;
  private boolean enabled = true;
  private boolean caretActionEnabled = true;
  private boolean shakeEnabled = false;
  private boolean bamEnabled = true;
  private boolean flamesEnabled = true;
  private boolean sparksEnabled = true;
  private boolean isCustomFlameImages = false;
  private boolean isCustomBamImages = false;
  private int redFrom = 200;
  private int redTo = 255;
  private int greenFrom = 0;
  private int greenTo = 255;
  private int blueFrom = 0;
  private int blueTo = 103;
  private int colorAlpha = 164;
  private double heatupThreshold = 0.0;
  private ElementContainerManager elementContainerManager;

  private File customFlameImageFolder;
  private File customBamImageFolder;

  public static PowerMode getInstance() {
    try {
      return ApplicationManager
        .getApplication()
        .getComponent(PowerMode.class);
    } catch (Throwable e) {
      logger().error(
        "error getting component: " + e.getMessage(),
        e
      );
    }

    throw new PluginDisabledException("Plugin instance is null!");
  }

  public static Logger logger() {
    return Logger.getInstance(PowerMode.class);
  }

  public ColorEdges obtainColorEdges() {
    ColorEdges edges = new ColorEdges();
    edges.setAlpha(getColorAlpha());
    edges.setRedFrom(getRedFrom());
    edges.setRedTo(getRedTo());
    edges.setGreenFrom(getGreenFrom());
    edges.setGreenTo(getGreenTo());
    edges.setBlueFrom(getBlueFrom());
    edges.setBlueTo(getBlueTo());
    return edges;
  }

  public int getColorAlpha() {
    return colorAlpha;
  }

  public int getRedFrom() {
    return redFrom;
  }

  public void setRedFrom(int redFrom) {
    if (redFrom <= redTo) {
      this.redFrom = redFrom;
    }
  }

  public int getRedTo() {
    return redTo;
  }

  // TODO - I don't like how repetitive these colors are, maybe derive class?
  public void setRedTo(int redTo) {
    if (redTo >= redFrom) {
      this.redTo = redTo;
    }
  }

  public int getGreenFrom() {
    return greenFrom;
  }

  public void setGreenFrom(int greenFrom) {
    if (greenFrom <= greenTo) {
      this.greenFrom = greenFrom;
    }
  }

  public int getGreenTo() {
    return greenTo;
  }

  public void setGreenTo(int greenTo) {
    if (greenTo >= greenFrom) {
      this.greenTo = greenTo;
    }
  }

  public int getBlueFrom() {
    return blueFrom;
  }

  public void setBlueFrom(int blueFrom) {
    if (blueFrom <= blueTo) {
      this.blueFrom = blueFrom;
    }
  }

  public int getBlueTo() {
    return blueTo;
  }

  public void setBlueTo(int blueTo) {
    if (blueTo >= blueFrom) {
      this.blueTo = blueTo;
    }
  }

  public void setColorAlpha(int colorAlpha) {
    this.colorAlpha = colorAlpha;
  }

  public File flameImageFolder() {
    return isCustomFlameImages ? customFlameImageFolder : new File("fire/animated/256");
  }

  public File bamImageFolder() {
    return isCustomBamImages ? customBamImageFolder : new File("bam");
  }

  public void increaseHeatup(KeyStroke keyStroke) {
    if (keyStroke == null) {
      return;
    }

    long ct = System.currentTimeMillis();
    lastKeys.put(
      keyStroke,
      ct
    );
  }

  public void reduceHeatup() {
    long ct = System.currentTimeMillis();
    lastKeys = filterLastKeys(ct);
  }

  private Map<KeyStroke, Long> filterLastKeys(Long ct) {
    return lastKeys
      .entrySet()
      .stream()
      .filter(e -> e.getValue() >= ct - heatupTime)
      .collect(Collectors.toMap(
        Map.Entry::getKey,
        Map.Entry::getValue
      ));
  }

  public double valueFactor() {
    double base = heatupFactor + ((1 - heatupFactor) * timeFactor());
    double elems = (base - heatupThreshold) / (1 - heatupThreshold);
    double max = Math.max(
      elems,
      0.0
    );
    assert (max <= 1);
    assert (max >= 0);
    return max;
  }

  private double timeFactor() {
    if (heatupTime < 1000) {
      return 1;
    }

    if (lastKeys.isEmpty()) {
      return 0;
    }

    double d = heatupTime / (60000.0 / keyStrokesPerMinute);
    double keysWorth = lastKeys
      .keySet()
      .stream()
      .filter(keystroke -> HOT_INPUTS.contains(keystroke.getModifiers()))
      .count() * hotkeyWeight;

    return Math.min(
      keysWorth,
      d
    ) / d;
  }

  @Override
  public void initComponent() {
    PowerMode
      .logger()
      .debug("initComponent...");
    EditorFactory editorFactory = EditorFactory.getInstance();
    elementContainerManager = new ElementContainerManager();
    editorFactory.addEditorFactoryListener(
      elementContainerManager,
      () -> {
      }
    );
    EditorActionManager editorActionManager = EditorActionManager.getInstance();
    editorActionManager
      .getTypedAction()
      .setupRawHandler(new MyTypedActionHandler(editorActionManager
        .getTypedAction()
        .getRawHandler()));

    PowerMode
      .logger()
      .debug("initComponent done");
  }

  @Override
  public void disposeComponent() {
    if (elementContainerManager == null) {
      return;
    }

    elementContainerManager.dispose();
  }

  @Override
  public @NotNull String getComponentName() {
    return "RidiculousCoding";
  }

  @Override
  public PowerMode getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull PowerMode state) {
    XmlSerializerUtil.copyBean(
      state,
      this
    );
  }

  public boolean isHotkeyHeatup() {
    return hotkeyHeatup;
  }

  public void setHotkeyHeatup(boolean hotkeyHeatup) {
    this.hotkeyHeatup = hotkeyHeatup;
  }

  public long getBamLife() {
    return bamLife;
  }

  public void setBamLife(long bamLife) {
    this.bamLife = bamLife;
  }

  public double getGravityFactor() {
    return gravityFactor;
  }

  public void setGravityFactor(double gravityFactor) {
    this.gravityFactor = gravityFactor;
  }

  public double getSparkVelocityFactor() {
    return sparkVelocityFactor;
  }

  public void setSparkVelocityFactor(double sparkVelocityFactor) {
    this.sparkVelocityFactor = sparkVelocityFactor;
  }

  public int getSparkSize() {
    return sparkSize;
  }

  public void setSparkSize(int sparkSize) {
    this.sparkSize = sparkSize;
  }

  public int getFrameRate() {
    return frameRate;
  }

  public void setFrameRate(int frameRate) {
    this.frameRate = frameRate;
  }

  public int getMaxFlameSize() {
    return maxFlameSize;
  }

  public void setMaxFlameSize(int maxFlameSize) {
    this.maxFlameSize = maxFlameSize;
  }

  public int getMaxFlameLife() {
    return maxFlameLife;
  }

  public void setMaxFlameLife(int maxFlameLife) {
    this.maxFlameLife = maxFlameLife;
  }

  public int getHeatupTime() {
    return heatupTime;
  }

  public void setHeatupTime(int heatupTime) {
    this.heatupTime = Math.max(
      0,
      heatupTime
    );
  }

  // TODO - Seems like unnecessary overhead for a value only set in settings.
  public int getKeyStrokesPerMinute() {
    return keyStrokesPerMinute;
  }

  public void setKeyStrokesPerMinute(int keyStrokesPerMinute) {
    this.keyStrokesPerMinute = keyStrokesPerMinute;
  }

  public int getHeatup() {
    return (int) (heatupFactor * 100);
  }

  public void setHeatup(int heatup) {
    setHeatupFactor((double) heatup / 100);
  }

  public void setHeatupFactor(double heatupFactor) {
    this.heatupFactor = heatupFactor;
  }

  public int getSparkLife() {
    return sparkLife;
  }

  public void setSparkLife(int sparkLife) {
    this.sparkLife = sparkLife;
  }

  public int getSparkCount() {
    return sparkCount;
  }

  public void setSparkCount(int sparkCount) {
    this.sparkCount = sparkCount;
  }

  public int getShakeRange() {
    return shakeRange;
  }

  public void setShakeRange(int shakeRange) {
    this.shakeRange = shakeRange;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isShakeEnabled() {
    return shakeEnabled;
  }

  public void setShakeEnabled(boolean shakeEnabled) {
    this.shakeEnabled = shakeEnabled;
  }

  public boolean isBamEnabled() {
    return bamEnabled;
  }

  public void setBamEnabled(boolean bamEnabled) {
    this.bamEnabled = bamEnabled;
  }

  public boolean isFlamesEnabled() {
    return flamesEnabled;
  }

  public void setFlamesEnabled(boolean flamesEnabled) {
    this.flamesEnabled = flamesEnabled;
  }

  public boolean isSparksEnabled() {
    return sparksEnabled;
  }

  public void setSparksEnabled(boolean sparksEnabled) {
    this.sparksEnabled = sparksEnabled;
  }

  public boolean isCustomFlameImages() {
    return isCustomFlameImages;
  }

  public void setCustomFlameImages(boolean customFlameImages) {
    isCustomFlameImages = customFlameImages;
  }

  public boolean isCustomBamImages() {
    return isCustomBamImages;
  }

  public void setCustomBamImages(boolean customBamImages) {
    isCustomBamImages = customBamImages;
  }

  public int getHeatupThreshold() {
    return (int) (heatupThreshold * 100);
  }

  public void setHeatupThreshold(int heatupThreshold) {
    this.heatupThreshold = (double) heatupThreshold / 100;
  }

  public ElementContainerManager getElementContainerManager() {
    return elementContainerManager;
  }

  public String getCustomFlameImageFolder() {
    return customFlameImageFolder != null
      ? customFlameImageFolder.getAbsolutePath()
      : "";
  }

  public void setCustomFlameImageFolder(String customFlameImageFolder) {
    this.customFlameImageFolder = new File(customFlameImageFolder);
  }

  public String getCustomBamImageFolder() {
    return customBamImageFolder != null ? customBamImageFolder.getAbsolutePath() : "";
  }

  public void setCustomBamImageFolder(String customBamImageFolder) {
    this.customBamImageFolder = new File(customBamImageFolder);
  }

  public boolean isCaretActionEnabled() {
    return caretActionEnabled;
  }

  public void setCaretActionEnabled(boolean caretActionEnabled) {
    this.caretActionEnabled = caretActionEnabled;
  }
}
