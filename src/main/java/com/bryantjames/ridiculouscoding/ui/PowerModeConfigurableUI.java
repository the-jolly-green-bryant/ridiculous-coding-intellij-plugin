package com.bryantjames.ridiculouscoding.ui;

import com.intellij.openapi.options.ConfigurableUi;
import com.bryantjames.ridiculouscoding.PowerMode;
import com.bryantjames.ridiculouscoding.color.MultiGradientPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Arrays;

/**
 * @author Baptiste Mesta
 */
public class PowerModeConfigurableUI implements ConfigurableUi<PowerMode> {


  private JPanel mainPanel;
  private JCheckBox powerModeEnabled;
  private JCheckBox shakeEnabled;
  private JSlider sparkCount;
  private JSlider sparkLife;
  private JSlider shakeRange;
  private JSlider heatup;
  private JSlider heatupTime;
  private JLabel sparkCountValue;
  private JLabel sparkLifeValue;
  private JLabel shakeRangeValue;



  private JSlider keyStrokesPerMinute;
  private JLabel keyStrokesPerMinuteValue;
  private JCheckBox FLAMESCheckBox;
  private JCheckBox PARTICLESCheckBox;
  private JSlider sparkSize;
  private JLabel sparkSizeValue;
  private JLabel velocityFactorValue;
  private JLabel gravityFactorValue;
  private JSlider velocityFactor;
  private JSlider gravityFactor;
  private JLabel frameRateValue;
  private JSlider frameRate;
  private JPanel colorView = new MultiGradientPanel(
    200,
    null
  );
  private JCheckBox visualizeEveryCaretMovementCheckBox;
  private JLabel heatupThresholdValue;
  private JSlider heatupThreshold;
  private JCheckBox HOTKEYHEATUPCheckBox;


  public PowerModeConfigurableUI(PowerMode powerMode) {
    ((MultiGradientPanel) colorView).setColorEdges(powerMode.obtainColorEdges());
    new ColorViewController(
      (MultiGradientPanel) colorView,
      powerMode
    );
    powerModeEnabled.setSelected(powerMode.isEnabled());
    shakeEnabled.setSelected(powerMode.isShakeEnabled());
    shakeEnabled.addChangeListener(e -> powerMode.setShakeEnabled(shakeEnabled.isSelected()));
    FLAMESCheckBox.setSelected(powerMode.isFlamesEnabled());
    FLAMESCheckBox.addChangeListener(e -> powerMode.setFlamesEnabled(FLAMESCheckBox.isSelected()));
    PARTICLESCheckBox.setSelected(powerMode.isSparksEnabled());
    PARTICLESCheckBox.addChangeListener(e -> powerMode.setSparksEnabled(PARTICLESCheckBox.isSelected()));
    visualizeEveryCaretMovementCheckBox.setSelected(powerMode.isCaretActionEnabled());
    visualizeEveryCaretMovementCheckBox.addChangeListener(e -> powerMode.setCaretActionEnabled(visualizeEveryCaretMovementCheckBox.isSelected()));
    HOTKEYHEATUPCheckBox.setSelected(powerMode.isHotkeyHeatup());
    HOTKEYHEATUPCheckBox.addChangeListener(e -> powerMode.setHotkeyHeatup(HOTKEYHEATUPCheckBox.isSelected()));

    initValues(
      powerMode.getSparkCount(),
      sparkCount,
      sparkCountValue,
      slider -> powerMode.setSparkCount(slider.getValue())
    );
    initValues(
      powerMode.getSparkSize(),
      sparkSize,
      sparkSizeValue,
      slider -> powerMode.setSparkSize(slider.getValue())
    );
    initValues(
      powerMode.getSparkLife(),
      sparkLife,
      sparkLifeValue,
      slider -> powerMode.setSparkLife(slider.getValue())
    );
    initValues(
      Double
        .valueOf((powerMode.getSparkVelocityFactor() * 100.0))
        .intValue(),
      velocityFactor,
      velocityFactorValue,
      slider -> powerMode.setSparkVelocityFactor(slider.getValue() / 100.0)
    );
    initValues(
      Double
        .valueOf(powerMode.getGravityFactor() * 100.0)
        .intValue(),
      gravityFactor,
      gravityFactorValue,
      slider -> powerMode.setGravityFactor(slider.getValue() / 100.0)
    );
    initValues(
      powerMode.getShakeRange(),
      shakeRange,
      shakeRangeValue,
      slider -> powerMode.setShakeRange(slider.getValue())
    );
    initValues(
      powerMode.getHeatupThreshold(),
      heatupThreshold,
      heatupThresholdValue,
      slider -> powerMode.setHeatupThreshold(slider.getValue())
    );
    initValues(
      powerMode.getKeyStrokesPerMinute(),
      keyStrokesPerMinute,
      keyStrokesPerMinuteValue,
      slider -> powerMode.setKeyStrokesPerMinute(slider.getValue())
    );
    initValues(
      powerMode.getFrameRate(),
      frameRate,
      frameRateValue,
      slider -> powerMode.setFrameRate(slider.getValue())
    );


    Arrays
      .stream(mainPanel.getComponents())
      .filter(c -> c instanceof JScrollPane)
      .findFirst()
      .ifPresent(scrollPane -> ((JScrollPane) scrollPane)
        .getVerticalScrollBar()
        .setUnitIncrement(16))
    ;
  }

  private void initValues(
    int initValue,
    JSlider slider,
    JLabel sliderValueLabel,
    ValueSettable valueSettable
  ) {
    slider.setValue(initValue);
    sliderValueLabel.setText(String.valueOf(initValue));
    slider.addChangeListener(new MyChangeListener(
      slider,
      sliderValueLabel
    ) {
      @Override
      public void setValue(JSlider slider) {
        valueSettable.setValue(slider);
      }
    });
  }

  private void initValuesColor(
    int initValue,
    JSlider slider,
    JLabel sliderValueLabel,
    PowerMode powerMode,
    ValueColorSettable valueSettable
  ) {
    initValues(
      initValue,
      slider,
      sliderValueLabel,
      slider1 -> {
        valueSettable.setValue(slider1);
        ((MultiGradientPanel) colorView).setColorEdges(powerMode.obtainColorEdges());
      }
    );

  }

  private void bindSliders(
    JSlider from,
    JSlider to
  ) {
    from.addChangeListener(e -> {
      if (from.getValue() > to.getValue()) {
        to.setValue(from.getValue());
      }
    });
    to.addChangeListener(e -> {
      if (to.getValue() < from.getValue()) {
        from.setValue(to.getValue());
      }
    });
  }

  @Override
  public void reset(PowerMode powerMode) {
    powerModeEnabled.setSelected(powerMode.isEnabled());
  }

  @Override
  public boolean isModified(PowerMode powerMode) {
    return powerModeEnabled.isSelected() != powerMode.isEnabled();
  }

  @Override
  public void apply(PowerMode powerMode) {
    powerMode.setEnabled(powerModeEnabled.isSelected());
  }


  @Override
  public @NotNull JComponent getComponent() {
    return mainPanel;
  }

  private void createUIComponents() {
    this.colorView = new MultiGradientPanel(
      200,
      null
    );


  }

  private interface ValueColorSettable {
    void setValue(JSlider slider);
  }

  private abstract class MyChangeListener implements ChangeListener, ValueSettable {
    private JSlider slider;
    private JLabel jLabel;

    public MyChangeListener(
      JSlider slider,
      JLabel jLabel
    ) {
      this.slider = slider;
      this.jLabel = jLabel;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
      setValue(slider);
      jLabel.setText(String.valueOf(slider.getValue()));
    }

  }
}
