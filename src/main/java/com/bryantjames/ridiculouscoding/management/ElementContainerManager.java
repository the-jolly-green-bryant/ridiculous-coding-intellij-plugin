package com.bryantjames.ridiculouscoding.management;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.bryantjames.ridiculouscoding.Power;
import com.bryantjames.ridiculouscoding.PowerMode;
import com.bryantjames.ridiculouscoding.util.Util;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ElementContainerManager implements EditorFactoryListener, Power {

  private static final Map<Editor, ElementContainer> elementContainers
    = new HashMap<>();
  private final Thread elementsOfPowerUpdateThread;

  public ElementContainerManager() {
    elementsOfPowerUpdateThread = new Thread(() -> {
      while (true) {
        try {
          if (powerMode() != null) {
            powerMode().reduceHeatup();
            updateContainers();

            try {
              Thread.sleep(1000 / powerMode().getFrameRate());
            } catch (InterruptedException e) {
              // Do nothing
            }
          }
        } catch (Exception e) {
          PowerMode
            .logger()
            .error(
              e.getMessage(),
              e
            );
        }
      }
    });

    elementsOfPowerUpdateThread.start();
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
    if (!powerMode().isEnabled()) {
      return;
    }

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

  public void dispose() {
    elementsOfPowerUpdateThread.interrupt();
    elementContainers.clear();
  }
}
