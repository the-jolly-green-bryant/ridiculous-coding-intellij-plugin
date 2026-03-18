package com.bryantjames.ridiculouscoding;

import com.bryantjames.ridiculouscoding.color.ColorEdges;
import com.bryantjames.ridiculouscoding.management.ElementContainerManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@State(name = "RidiculousCoding", storages = @Storage("ridiculous.coding.xml"))
public class PowerMode implements PersistentStateComponent<PowerMode> {
  // TODO - A lot of these values are no longer used.
  private static final List<Integer> HOT_INPUTS = Arrays.asList(
    InputEvent.CTRL_DOWN_MASK,
    InputEvent.ALT_DOWN_MASK,
    InputEvent.SHIFT_DOWN_MASK
  );
  private static PowerMode instance;
  public long xp = 0;
  public int level = 1;
  private boolean hotkeyHeatup = true;
  private double gravityFactor = 21.21;
  private double sparkVelocityFactor = 4.36;
  private int sparkSize = 3;
  private int frameRate = 30;
  private final int heatupTime = 10000;
  private Map<KeyStroke, Long> lastKeys = new HashMap<>();
  private int keyStrokesPerMinute = 300;
  private final double hotkeyWeight = keyStrokesPerMinute * 0.05;
  private double heatupFactor = 1.0;
  private int sparkLife = 3000;
  private int sparkCount = 10;
  private int shakeRange = 6;
  private boolean enabled = true;
  private boolean caretActionEnabled = true;
  private boolean shakeEnabled = true;
  private boolean flamesEnabled = true;
  private boolean sparksEnabled = true;
  private double heatupThreshold = 0.0;
  private ElementContainerManager elementContainerManager;

  public static PowerMode getInstance() {
    if (instance == null) {
      instance = new PowerMode();
    }

    return instance;
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
    return 164;
  }

  public int getRedFrom() {
    return 200;
  }

  public int getRedTo() {
    return 255;
  }

  // TODO - I don't like how repetitive these colors are, maybe derive class?

  public int getGreenFrom() {
    return 0;
  }

  public int getGreenTo() {
    return 255;
  }

  public int getBlueFrom() {
    return 0;
  }

  public int getBlueTo() {
    return 103;
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

  public void initialize() {
    EditorFactory editorFactory = EditorFactory.getInstance();
    elementContainerManager = new ElementContainerManager();
    editorFactory.addEditorFactoryListener(
      elementContainerManager,
      () -> {
      }
    );
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

  public double getSparkVelocityFactor() {
    return sparkVelocityFactor;
  }

  public int getSparkSize() {
    return sparkSize;
  }

  public int getFrameRate() {
    return frameRate;
  }

  public void setFrameRate(int frameRate) {
    this.frameRate = frameRate;
  }

  // TODO - Seems like unnecessary overhead for a value only set in settings.
  public int getKeyStrokesPerMinute() {
    return keyStrokesPerMinute;
  }

  public void setKeyStrokesPerMinute(int keyStrokesPerMinute) {
    this.keyStrokesPerMinute = keyStrokesPerMinute;
  }

  public int getSparkCount() {
    return sparkCount;
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

  public int getHeatupThreshold() {
    return (int) (heatupThreshold * 100);
  }

  public void setHeatupThreshold(int heatupThreshold) {
    this.heatupThreshold = (double) heatupThreshold / 100;
  }

  public ElementContainerManager getElementContainerManager() {
    return elementContainerManager;
  }

  // I don't think we use this...
  public boolean isCaretActionEnabled() {
    return caretActionEnabled;
  }

  public void setCaretActionEnabled(boolean caretActionEnabled) {
    this.caretActionEnabled = caretActionEnabled;
  }
}
