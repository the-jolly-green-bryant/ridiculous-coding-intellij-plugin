package com.bryantjames.ridiculouscoding.listeners;

import com.bryantjames.ridiculouscoding.PowerMode;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.components.ApplicationComponent;
import com.bryantjames.ridiculouscoding.Power;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Optional;

class HotkeyHeatupListener implements AWTEventListener, Power, ApplicationComponent {

  @Override
  public void eventDispatched(AWTEvent e) {
    if (!powerMode().isEnabled()) {
      return;
    }

    if (e instanceof KeyEvent event && (
      event.getModifiersEx() & (
        InputEvent.CTRL_DOWN_MASK
          | InputEvent.ALT_DOWN_MASK
          | InputEvent.SHIFT_DOWN_MASK
          | InputEvent.META_DOWN_MASK
      )
    ) > 0) {

      Component focusOwner = KeyboardFocusManager
        .getCurrentKeyboardFocusManager()
        .getFocusOwner();

      DataContext dataContext = DataManager
        .getInstance()
        .getDataContext(focusOwner);
      Editor editor = CommonDataKeys.EDITOR.getData(dataContext);

      Object rawHandler = EditorActionManager
        .getInstance()
        .getTypedAction()
        .getRawHandler()
        ;

      if (rawHandler instanceof MyTypedActionHandler handler) {

        int mods = event.getModifiersEx();
        int key = event.getKeyCode();

        if (mods == 0)
          return;

        // ignore pure modifier keys
        if (key == KeyEvent.VK_SHIFT
          || key == KeyEvent.VK_CONTROL
          || key == KeyEvent.VK_ALT
          || key == KeyEvent.VK_META) {
          return;
        }

        String keyText;

        switch (key) {
          case KeyEvent.VK_UP -> keyText = "up";
          case KeyEvent.VK_DOWN -> keyText = "down";
          case KeyEvent.VK_LEFT -> keyText = "left";
          case KeyEvent.VK_RIGHT -> keyText = "right";
          default -> keyText = KeyEvent.getKeyText(key).toLowerCase();
        }

        if (keyText == null || keyText.isBlank() || keyText.toLowerCase().contains("unknown")) {
          return;
        }

        StringBuilder text = new StringBuilder();

        if ((mods & InputEvent.META_DOWN_MASK) != 0)
          text.append("cmd+");
        if ((mods & InputEvent.CTRL_DOWN_MASK) != 0)
          text.append("ctrl+");
        if ((mods & InputEvent.ALT_DOWN_MASK) != 0)
          text.append("alt+");
        if ((mods & InputEvent.SHIFT_DOWN_MASK) != 0)
          text.append("shift+");

        if (!keyText.isBlank()) {
          text.append(keyText);
          handler.powerType(
            editor,
            text.toString(),
            dataContext
          );
        }
      }
    }
  }

  @Override
  public void initComponent() {
    Toolkit
      .getDefaultToolkit()
      .addAWTEventListener(
        this,
        AWTEvent.KEY_EVENT_MASK
      );
  }

  @Override
  public void disposeComponent() {}

  @NotNull
  @Override
  public String getComponentName() {
    return "HotkeyHeatupListener";
  }
}
