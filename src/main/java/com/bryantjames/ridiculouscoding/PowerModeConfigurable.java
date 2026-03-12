package com.bryantjames.ridiculouscoding;


import com.intellij.openapi.options.ConfigurableBase;
import com.intellij.openapi.options.ConfigurableUi;
import com.bryantjames.ridiculouscoding.ui.PowerModeConfigurableUI;
import org.jetbrains.annotations.NotNull;

public class PowerModeConfigurable extends ConfigurableBase {

  private final PowerMode settings;

  public PowerModeConfigurable() {
    super(
      "Ridiculous Coding",
      "Ridiculous Coding",
      "Ridiculous Coding"
    );
    settings = PowerMode.getInstance();
  }

  @NotNull
  @Override
  protected PowerMode getSettings() {
    if (settings == null) {
      throw new IllegalStateException("power mode is null");
    }
    return settings;
  }

  @Override
  protected ConfigurableUi createUi() {
    return new PowerModeConfigurableUI(settings);
  }
}
