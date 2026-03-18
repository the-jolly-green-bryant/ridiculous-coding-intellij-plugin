package com.bryantjames.ridiculouscoding.listeners;

import com.bryantjames.ridiculouscoding.PowerMode;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class ToggleCaretPowerMode extends AnAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    PowerMode
      .getInstance()
      .setCaretActionEnabled(!PowerMode
        .getInstance()
        .isCaretActionEnabled());
  }
}
