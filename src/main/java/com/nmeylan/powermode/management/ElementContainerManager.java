package com.nmeylan.powermode.management;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.nmeylan.powermode.Power;
import com.nmeylan.powermode.PowerMode;
import com.nmeylan.powermode.util.Util;
import org.jetbrains.annotations.NotNull;

import javax.swing.SwingUtilities;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class ElementContainerManager implements EditorFactoryListener, Power {

  private static final Map<Editor, ElementContainer> elementContainers = new HashMap<>();
  private final Thread elementsUpdateThread;

  public ElementContainerManager() {
    elementsUpdateThread = new Thread(() -> {
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
          PowerMode.logger().error(e.getMessage(), e);
        }
      }
    });
    elementsUpdateThread.start();
  }


  void updateContainers() {
    elementContainers.values().forEach(ElementContainer::updateElementsOfPower);
  }


  @Override
  public void editorCreated(@NotNull EditorFactoryEvent event) {
    Editor editor = event.getEditor();
    if (Util.isActualEditor(editor)) {
      elementContainers.put(editor, new ElementContainer((EditorImpl) editor));
    }
  }

  @Override
  public void editorReleased(@NotNull EditorFactoryEvent event) {
    elementContainers.remove(event.getEditor());
  }

  public void initializeAnimation(Editor editor, char c, Point position) {
    if (powerMode().isEnabled()) {
      SwingUtilities.invokeLater(() -> initializeInUI(editor, c, position));
    }
  }

  public void initializeInUI(Editor editor, char c, Point position) {
    elementContainers.get(editor).initializeAnimation(c, position);
  }

  public void dispose() {
    elementsUpdateThread.interrupt();
    elementContainers.clear();
  }
}
