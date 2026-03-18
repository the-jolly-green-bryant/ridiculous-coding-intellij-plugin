package com.bryantjames.ridiculouscoding.gamification;

import com.bryantjames.ridiculouscoding.PowerMode;
import com.bryantjames.ridiculouscoding.element.BaseElement;
import com.bryantjames.ridiculouscoding.listeners.TypingHandler;
import com.intellij.openapi.editor.Editor;

public class Experience extends BaseElement {
  private static final int base = 100;

  public static void modExperience(
    Editor editor,
    int mod
  ) {
    PowerMode.getInstance().xp += mod;
    int level = deriveLevel();
    if (level == PowerMode.getInstance().level) {
      return;
    }

    PowerMode.getInstance().level = level;
    renderCongratulations(editor);
  }

  public static int deriveLevel() {
    long xp = PowerMode.getInstance().xp;
    return (int) Math.floor((Math.sqrt(1 + 8.0 * xp / base) - 1) / 2);
  }

  private static void renderCongratulations(Editor editor) {
    // TODO - Convert congratulations to an overlay.
    TypingHandler.powerType(
      editor,
      "Level Up!"
    );
    TypingHandler.powerType(
      editor,
      "Level " + PowerMode.getInstance().level
    );
  }

  private static String statusText() {
    int level = PowerMode.getInstance().level;
    long needed = deriveExperience(level + 1);
    long previouslyNeeded = deriveExperience(level);
    long into = PowerMode.getInstance().xp - previouslyNeeded;
    long diff = needed - previouslyNeeded;

    float levelProgress = Math.max(
      0.0f,
      Math.min(
        1.0f,
        (float) into / diff
      )
    );

    int blocks = 6;
    int filled = (int) Math.floor(levelProgress * blocks);
    if (filled > blocks) {
      filled = blocks;
    }

    String bar = "▓".repeat(filled) + "░".repeat(blocks - filled);
    return "⚡ Lv" + level + " " + bar + " " + into + "/" + needed;
  }

  public static long deriveExperience(int level) {
    return (long) base * level * (level + 1) / 2;
  }
}
