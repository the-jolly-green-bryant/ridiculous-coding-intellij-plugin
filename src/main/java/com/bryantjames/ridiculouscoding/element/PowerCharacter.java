package com.bryantjames.ridiculouscoding.element;

import com.bryantjames.ridiculouscoding.util.Util;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PowerCharacter extends Element {

  private float x;
  private float y;
  private float dx;
  private float dy;
  private int size;
  private float[] colors;
  private float gravityFactor;
  private char character;

  private static final Map<Integer, Font> FONT_CACHE = new HashMap<>();

  public PowerCharacter(
    float x,
    float y,
    float dx,
    float dy,
    int size,
    long initLife,
    float[] colors,
    float gravityFactor,
    char c
  ) {
    this.x = x;
    this.y = y;
    this.dx = dx;
    this.dy = dy;
    this.size = size;
    this.initLife = initLife;
    this.colors = colors;
    this.gravityFactor = gravityFactor;
    this.life = System.currentTimeMillis() + initLife;
    this.character = c;
  }

  @Override
  public boolean update(double delta) {
    dy += (0.07f * gravityFactor) * delta;
    x += dx * delta;
    y += dy * delta;
    return !isAlive();
  }

  @Override
  public void render(
    Graphics g,
    int dxx,
    int dyy
  ) {
    if (!isAlive()) {
      return;
    }

    Graphics2D g2d = (Graphics2D) g.create();
    try {
      g2d.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON
      );
      g2d.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      );

      g2d.setColor(new Color(
        colors[0],
        colors[1],
        colors[2],
        Util.alpha(colors[3])
      ));

      float p = progress();
      float rawSize = Math.max(size * (1.4f + (p * 0.8f)), 16f);
      int quantizedSize = Math.round(rawSize / 2f) * 2; // 16, 18, 20, etc
      Font font = FONT_CACHE.computeIfAbsent(
        quantizedSize,
        s -> g2d.getFont().deriveFont(Font.BOLD, (float) s)
      );
      g2d.setFont(font);

      String text = String.valueOf(character).toUpperCase();
      FontMetrics fm = g2d.getFontMetrics(font);

      int drawX = (int) (dxx + x - (fm.stringWidth(text) / 2f));
      int drawY = Math.round(dyy + y + ((fm.getAscent() - fm.getDescent()) / 2f));

      g2d.drawString(
        text,
        drawX,
        drawY
      );
    } finally {
      g2d.dispose();
    }
  }
}
