package com.bryantjames.ridiculouscoding.gamification;

import com.bryantjames.ridiculouscoding.PowerMode;
import com.bryantjames.ridiculouscoding.element.BaseElement;

public class Experience extends BaseElement {
  public static int deriveLevel() {
    long xp = PowerMode.getInstance().xp;
    return (int) Math.floor((Math.sqrt(1 + 8.0 * xp) - 1) / 2);
  }

  public static void modExperience(int mod) {
    PowerMode.getInstance().xp += mod;
    int level = deriveLevel();
    if (level == PowerMode.getInstance().level) {
      return;
    }

    PowerMode.getInstance().level = level;
    renderCongratulations();
  }

  private static void renderCongratulations() {
    throw new UnsupportedOperationException("Congratulations render not supported yet.");
  }
}
