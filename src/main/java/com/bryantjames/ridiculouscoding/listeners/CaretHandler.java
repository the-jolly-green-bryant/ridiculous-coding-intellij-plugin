package com.bryantjames.ridiculouscoding.listeners;

import com.bryantjames.ridiculouscoding.PluginDisabledGuard;
import com.bryantjames.ridiculouscoding.PowerMode;
import com.bryantjames.ridiculouscoding.util.Util;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class CaretHandler implements CaretListener {
  private boolean modified = true;

  @Override
  public void caretPositionChanged(@NotNull CaretEvent event) {
    PluginDisabledGuard.run(() -> {
      if (!modified && PowerMode
        .getInstance()
        .isCaretActionEnabled() && event.getCaret() != null) {
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
      || PowerMode
      .getInstance()
      .getElementContainerManager() == null) {
      return;
    }

    Point position = Util.getCaretPosition(caret);
    PowerMode
      .getInstance()
      .getElementContainerManager()
      .initializeAnimation(
        caret.getEditor(),
        "highlight",
        position
      )
    ;
  }
}
