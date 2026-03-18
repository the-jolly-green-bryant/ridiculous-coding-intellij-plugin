package com.bryantjames.ridiculouscoding.listeners;

import com.bryantjames.ridiculouscoding.Power;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class ToggleCaretPowerMode extends AnAction implements Power {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    powerMode().setCaretActionEnabled(!powerMode().isCaretActionEnabled());
  }
}
