package com.bryantjames.ridiculouscoding.element;

import com.bryantjames.ridiculouscoding.util.FontUtil;
import com.bryantjames.ridiculouscoding.util.Util;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PowerCharacter extends Element {

  private float x;
  private float y;
  private final float startX;
  private final float startY;
  private float dx;
  private float dy;
  private int size;
  private float[] colors;
  private float gravityFactor;
  private char character;

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
    this.startX = x;
    this.startY = y;
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
    dy += (0.05f * gravityFactor) * delta;
    dx *= 0.98f;
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

      float p = progress();
      float pop = 1.0f + (0.25f * (1.0f - p));
      float rawSize = size * pop;
      int quantizedSize = Math.round(rawSize / 2f) * 2; // 16, 18, 20, etc
      Font font = FontUtil.getPixelFont(quantizedSize);
      g2d.setFont(font);

      String text = String.valueOf(character).toUpperCase();
      FontMetrics fm = g2d.getFontMetrics(font);

      float eased = 1.0f - (float) Math.pow(1.0f - p, 2.0f); // ease out

      float offsetY = -28f * eased;
      float offsetX = dx * 10f * eased;

      float drawX = x + offsetX;
      float drawY = y + offsetY;

      float fade = 1.0f - (p * 0.85f);
      float alpha = colors[3] * fade;

      Color mainColor = new Color(
        colors[0],
        colors[1],
        colors[2],
        alpha
      );
      float shadowAlpha = Math.max(0.15f, alpha * 0.35f);

      Color shadowColor = new Color(
        colors[0] * 0.25f,
        colors[1] * 0.25f,
        colors[2] * 0.25f,
        shadowAlpha
      );

      int shadowSize = 2;

      g2d.setColor(shadowColor);
      g2d.drawString(text, drawX - shadowSize, drawY - shadowSize);
      g2d.drawString(text, drawX, drawY - shadowSize);
      g2d.drawString(text, drawX + shadowSize, drawY - shadowSize);
      g2d.drawString(text, drawX - shadowSize, drawY);
      g2d.drawString(text, drawX + shadowSize, drawY);
      g2d.drawString(text, drawX - shadowSize, drawY + shadowSize);
      g2d.drawString(text, drawX,     drawY + shadowSize);
      g2d.drawString(text, drawX + shadowSize, drawY + shadowSize);

      g2d.setColor(mainColor);
      g2d.drawString(text, drawX, drawY);
    } finally {
      g2d.dispose();
    }
  }
}
