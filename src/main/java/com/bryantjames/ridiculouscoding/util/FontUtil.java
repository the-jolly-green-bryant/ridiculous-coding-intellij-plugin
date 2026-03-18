package com.bryantjames.ridiculouscoding.util;

import com.intellij.openapi.diagnostic.Logger;

import java.awt.*;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class FontUtil {
  private static final Logger LOG = Logger.getInstance(FontUtil.class);

  private static final String FONT_PATH = "fonts/Gravity Bold 8.ttf";
  private static final Map<Integer, Font> FONT_CACHE = new ConcurrentHashMap<>();

  private static volatile Font basePopupFont;

  public static Font getPixelFont(int size) {
    int safeSize = Math.max(
      8,
      size
    );

    return FONT_CACHE.computeIfAbsent(
      safeSize,
      s -> {
        Font base = getBasePopupFont();
        return base.deriveFont(
          Font.PLAIN,
          (float) s
        );
      }
    );
  }

  private static Font getBasePopupFont() {
    if (basePopupFont != null) {
      return basePopupFont;
    }

    synchronized (FontUtil.class) {
      if (basePopupFont != null) {
        return basePopupFont;
      }

      try (InputStream in = FontUtil.class
        .getClassLoader()
        .getResourceAsStream(FONT_PATH)) {
        if (in == null) {
          LOG.warn("Popup font not found at " + FONT_PATH + ", falling back to Dialog");
          basePopupFont = new Font(
            "Dialog",
            Font.BOLD,
            16
          );
          return basePopupFont;
        }

        Font loaded = Font.createFont(
          Font.TRUETYPE_FONT,
          in
        );
        GraphicsEnvironment
          .getLocalGraphicsEnvironment()
          .registerFont(loaded);
        basePopupFont = loaded;
        return basePopupFont;
      } catch (Exception e) {
        LOG.warn(
          "Failed to load popup font, falling back to Dialog",
          e
        );
        basePopupFont = new Font(
          "Dialog",
          Font.BOLD,
          16
        );
        return basePopupFont;
      }
    }
  }
}
