package com.bryantjames.ridiculouscoding.listeners;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.bryantjames.ridiculouscoding.Power;

public class ToggleCaretPowerMode extends AnAction implements Power {

  @Override
  public void actionPerformed(AnActionEvent e) {
    powerMode().setCaretActionEnabled(!powerMode().isCaretActionEnabled());
  }
}
