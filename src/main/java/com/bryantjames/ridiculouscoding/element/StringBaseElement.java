package com.bryantjames.ridiculouscoding.element;

import com.bryantjames.ridiculouscoding.util.FontUtil;

import java.awt.*;
import java.awt.geom.Point2D;

public class StringBaseElement extends BaseElement {

  private final float driftX;
  private final float riseHeight;
  private final int size;
  private final float[] colors;
  private final String text;

  public StringBaseElement(
    float x,
    float y,
    int size,
    long initLife,
    float[] colors,
    String text
  ) {
    this.origin = new Point2D.Float(
      x,
      y
    );
    this.driftX = 10f + (float) (Math.random() * 10f);
    this.riseHeight = 24f + (float) (Math.random() * 12f);
    this.size = size;
    this.initLife = initLife;
    this.colors = colors;
    this.life = System.currentTimeMillis() + initLife;
    this.text = text;
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
      float sizeProgress = (float) Math.pow(
        p,
        0.8f
      );
      float scale = 0.35f + (1.35f * sizeProgress);
      float rawSize = size * scale;
      int quantizedSize = Math.round(rawSize / 2f) * 2; // 16, 18, 20, etc
      Font font = FontUtil.getPixelFont(quantizedSize);
      g2d.setFont(font);

      String text = String
        .valueOf(this.text)
        .toUpperCase();

      float riseProgress = (float) Math.pow(
        p,
        0.9f
      );

      float drawX = this.origin.x + driftX * riseProgress;
      float drawY = this.origin.y - riseHeight * riseProgress;


      float fade = Math.min(
        1.0f,
        p * 6.0f
      );
      float alpha = colors[3] * fade;
      alpha = Math.max(
        0f,
        Math.min(
          1f,
          alpha
        )
      );

      Color mainColor = new Color(
        colors[0],
        colors[1],
        colors[2],
        alpha
      );
      float shadowAlpha = Math.max(
        0.15f,
        alpha * 0.35f
      );

      Color shadowColor = new Color(
        colors[0] * 0.25f,
        colors[1] * 0.25f,
        colors[2] * 0.25f,
        shadowAlpha
      );

      int shadowSize = 2;

      g2d.setColor(shadowColor);
      g2d.drawString(
        text,
        drawX - shadowSize,
        drawY - shadowSize
      );
      g2d.drawString(
        text,
        drawX,
        drawY - shadowSize
      );
      g2d.drawString(
        text,
        drawX + shadowSize,
        drawY - shadowSize
      );
      g2d.drawString(
        text,
        drawX - shadowSize,
        drawY
      );
      g2d.drawString(
        text,
        drawX + shadowSize,
        drawY
      );
      g2d.drawString(
        text,
        drawX - shadowSize,
        drawY + shadowSize
      );
      g2d.drawString(
        text,
        drawX,
        drawY + shadowSize
      );
      g2d.drawString(
        text,
        drawX + shadowSize,
        drawY + shadowSize
      );

      g2d.setColor(mainColor);
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
