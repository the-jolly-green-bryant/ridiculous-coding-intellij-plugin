package com.bryantjames.ridiculouscoding.listeners;

import com.bryantjames.ridiculouscoding.PluginDisabledGuard;
import com.bryantjames.ridiculouscoding.PowerMode;
import com.bryantjames.ridiculouscoding.util.Util;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ClickHandler implements EditorMouseListener {

  @Override
  public void mousePressed(@NotNull EditorMouseEvent event) {
    PluginDisabledGuard.run(() -> {
      Editor editor = event.getEditor();

      if (!Util.isActualEditor(editor)
        || PowerMode
        .getInstance()
        .getElementContainerManager() == null) {
        return;
      }

      Point click = event
        .getMouseEvent()
        .getPoint();
      var visual = editor.xyToVisualPosition(click);
      Point editorPoint = editor.visualPositionToXY(visual);

      Rectangle visibleArea = editor
        .getScrollingModel()
        .getVisibleArea();
      Point point = new Point(
        editorPoint.x - visibleArea.x,
        editorPoint.y - visibleArea.y
      );

      PowerMode
        .getInstance()
        .getElementContainerManager()
        .initializeAnimation(
          editor,
          "",
          point
        )
      ;
    });
  }
}