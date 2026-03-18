package com.bryantjames.ridiculouscoding.management;

import com.bryantjames.ridiculouscoding.PluginDisabledException;
import com.bryantjames.ridiculouscoding.PowerMode;
import com.bryantjames.ridiculouscoding.util.Util;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.util.concurrency.AppExecutorUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ElementContainerManager implements EditorFactoryListener {

  private static final Map<Editor, ElementContainer> elementContainers
    = new HashMap<>();

  public ElementContainerManager() {
    initializeExistingEditors();
    Timer updateTimer = new Timer(
      1000 / Math.max(
        1,
        PowerMode
          .getInstance()
          .getFrameRate()
      ),
      e -> {
        try {
          PowerMode pm = PowerMode.getInstance();
          if (pm == null || !pm.isEnabled()) {
            return;
          }

          if (elementContainers.isEmpty()) {
            return;
          }

          pm.reduceHeatup();
          updateContainers();

        } catch (PluginDisabledException ignored) {
        }
      }
    );
    updateTimer.start();
  }

  public void initializeExistingEditors() {
    for (Editor editor : com.intellij.openapi.editor.EditorFactory
      .getInstance()
      .getAllEditors()) {
      if (Util.isActualEditor(editor) && !elementContainers.containsKey(editor)) {
        elementContainers.put(
          editor,
          new ElementContainer((EditorImpl) editor)
        );
      }
    }
  }

  void updateContainers() {
    elementContainers
      .values()
      .forEach(ElementContainer::updateElementsOfPower);
  }


  @Override
  public void editorCreated(@NotNull EditorFactoryEvent event) {
    Editor editor = event.getEditor();
    if (Util.isActualEditor(editor)) {
      elementContainers.put(
        editor,
        new ElementContainer((EditorImpl) editor)
      );
    }
  }

  @Override
  public void editorReleased(@NotNull EditorFactoryEvent event) {
    elementContainers.remove(event.getEditor());
  }

  public void initializeAnimation(
    Editor editor,
    String text,
    Point position
  ) {
    PluginDisabledException.requirePluginEnabled();
    SwingUtilities.invokeLater(() -> initializeInUI(
      editor,
      text,
      position
    ));
  }

  public void initializeInUI(
    Editor editor,
    String text,
    Point position
  ) {
    elementContainers
      .get(editor)
      .initializeAnimation(
        text,
        position
      );
  }

  // TODO - We should probably wire this up somewhere...
  public void dispose() {
      elementContainers.clear();
  }
}
