package com.bryantjames.ridiculouscoding.listeners;

import com.bryantjames.ridiculouscoding.Power;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class TogglePowerMode extends AnAction implements Power {

  @Override
  public void actionPerformed(AnActionEvent e) {
    powerMode().setEnabled(!powerMode().isEnabled());
  }
}
