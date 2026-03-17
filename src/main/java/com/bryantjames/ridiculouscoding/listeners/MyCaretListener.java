package com.bryantjames.ridiculouscoding.listeners;

import com.bryantjames.ridiculouscoding.PluginDisabledGuard;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.bryantjames.ridiculouscoding.Power;
import com.bryantjames.ridiculouscoding.util.Util;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class MyCaretListener implements CaretListener, Power {
  private boolean modified = true;

  @Override
  public void caretPositionChanged(@NotNull CaretEvent event) {
    PluginDisabledGuard.run(() -> {
      if (!modified && powerMode().isCaretActionEnabled() && event.getCaret() != null) {
        initializeAnimationByCaretEvent(event.getCaret());
      }

      modified = false;
    });
  }

  @Override
  public void caretAdded(@NotNull CaretEvent event) {
    modified = true;
  }

  @Override
  public void caretRemoved(@NotNull CaretEvent event) {
    modified = true;
  }

  private void initializeAnimationByCaretEvent(Caret caret) {
    if (!Util.isActualEditor(caret.getEditor())
      || powerMode().getElementContainerManager() == null) {
      return;
    }

    Point position = Util.getCaretPosition(caret);
    powerMode()
      .getElementContainerManager()
      .initializeAnimation(
        caret.getEditor(),
        "",
        position
      )
    ;
  }
}
