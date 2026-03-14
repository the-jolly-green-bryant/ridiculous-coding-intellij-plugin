package com.bryantjames.ridiculouscoding.listeners;

import com.intellij.ide.DataManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.bryantjames.ridiculouscoding.Power;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Optional;

class HotkeyHeatupListener implements AWTEventListener, Power, ApplicationComponent {

  @Override
  public void eventDispatched(AWTEvent e) {
    if (!powerMode().isEnabled() || !powerMode().isHotkeyHeatup()) {
      return;
    }

    if (e instanceof KeyEvent event && (
      event.getModifiersEx() & (
        InputEvent.CTRL_DOWN_MASK
          | InputEvent.ALT_DOWN_MASK
          | InputEvent.SHIFT_DOWN_MASK
      )
    ) > 0) {

        KeyStroke eventKeyStroke = KeyStroke.getKeyStroke(
          event.getKeyCode(),
          event.getModifiersEx()
        );

        powerMode().increaseHeatup(
          eventKeyStroke
        );
    }
  }

  @Override
  public void initComponent() {
    Toolkit
      .getDefaultToolkit()
      .addAWTEventListener(
        this,
        AWTEvent.KEY_EVENT_MASK
      );
  }

  @Override
  public void disposeComponent() {}

  @NotNull
  @Override
  public String getComponentName() {
    return "HotkeyHeatupListener";
  }
}
