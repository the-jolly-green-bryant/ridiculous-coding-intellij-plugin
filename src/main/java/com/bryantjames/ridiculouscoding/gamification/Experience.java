package com.bryantjames.ridiculouscoding.gamification;

import com.bryantjames.ridiculouscoding.PowerMode;
import com.bryantjames.ridiculouscoding.element.BaseElement;
import com.bryantjames.ridiculouscoding.listeners.TypingHandler;
import com.intellij.openapi.editor.Editor;

public class Experience extends BaseElement {
  private static final int base = 100;

  public static int deriveLevel() {
    long xp = PowerMode.getInstance().xp;
    return (int) Math.floor((Math.sqrt(1 + 8.0 * xp / base) - 1) / 2);
  }

  public static void modExperience(Editor editor, int mod) {
    PowerMode.getInstance().xp += mod;
    int level = deriveLevel();
    if (level == PowerMode.getInstance().level) {
      return;
    }

    PowerMode.getInstance().level = level;
    renderCongratulations(editor);
  }

  private static void renderCongratulations(Editor editor) {
    // TODO - Convert congratulations to an overlay.
    TypingHandler.powerType(editor, "Level Up!");
    TypingHandler.powerType(editor, "Level " + PowerMode.getInstance().level);
  }
}
