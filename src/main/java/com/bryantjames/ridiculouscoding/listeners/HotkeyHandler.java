package com.bryantjames.ridiculouscoding.listeners;

import com.bryantjames.ridiculouscoding.PluginDisabledGuard;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;

import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class HotkeyHandler implements KeyEventDispatcher {

  private static HotkeyHandler instance;
  private static boolean initialized = false;

  public static HotkeyHandler getInstance() {
    if (instance == null) {
      instance = new HotkeyHandler();
    }

    return instance;
  }

  public static void initialize() {
    if (initialized) {
      return;
    }

    initialized = true;
    KeyboardFocusManager
      .getCurrentKeyboardFocusManager()
      .addKeyEventDispatcher(getInstance());
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    PluginDisabledGuard.run(() -> {
      if (event.getID() != KeyEvent.KEY_PRESSED) {
        return;
      }

      int mods = event.getModifiersEx();
      if ((mods & (
        InputEvent.CTRL_DOWN_MASK
          | InputEvent.ALT_DOWN_MASK
          | InputEvent.SHIFT_DOWN_MASK
          | InputEvent.META_DOWN_MASK
      )) == 0) {
        return;
      }

      int key = event.getKeyCode();

      if (key == KeyEvent.VK_SHIFT
        || key == KeyEvent.VK_CONTROL
        || key == KeyEvent.VK_ALT
        || key == KeyEvent.VK_META) {
        return;
      }

      Component focusOwner = KeyboardFocusManager
        .getCurrentKeyboardFocusManager()
        .getFocusOwner();

      if (focusOwner == null) {
        return;
      }

      DataContext dataContext = DataManager
        .getInstance()
        .getDataContext(focusOwner);

      Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
      if (editor == null) {
        return;
      }

      String keyText = switch (key) {
        case KeyEvent.VK_UP -> "up";
        case KeyEvent.VK_DOWN -> "down";
        case KeyEvent.VK_LEFT -> "left";
        case KeyEvent.VK_RIGHT -> "right";
        default -> KeyEvent.getKeyText(key).toLowerCase();
      };

      if (keyText.isBlank() || keyText.contains("unknown")) {
        return;
      }

      StringBuilder text = new StringBuilder();

      if ((mods & InputEvent.META_DOWN_MASK) != 0) text.append("cmd+");
      if ((mods & InputEvent.CTRL_DOWN_MASK) != 0) text.append("ctrl+");
      if ((mods & InputEvent.ALT_DOWN_MASK) != 0) text.append("alt+");
      if ((mods & InputEvent.SHIFT_DOWN_MASK) != 0) text.append("shift+");

      text.append(keyText);

      TypingHandler.powerType(editor, text.toString());
    });

    return false;
  }
}