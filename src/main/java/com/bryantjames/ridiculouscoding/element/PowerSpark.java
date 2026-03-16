package com.bryantjames.ridiculouscoding.element;

import com.bryantjames.ridiculouscoding.util.Util;

import java.awt.*;

public class PowerSpark extends Element {
  private float x;
  private float y;
  private float dx;
  private float dy;
  private int size;
  private float[] colors;

  public PowerSpark(
    float x,
    float y,
    float dx,
    float dy,
    int size,
    long initLife,
    float[] colors
  ) {
    this.x = x;
    this.y = y;
    this.dx = dx;
    this.dy = dy;
    this.size = size;
    this.initLife = initLife;
    this.colors = colors;
    this.life = System.currentTimeMillis() + initLife;
  }

  @Override
  public boolean update(double delta) {
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

    float p = progress();
    float alpha = colors[3] * (1.0f - p);
    alpha = Math.max(0f, Math.min(1f, alpha));
    float scale = 1.0f - (p * 0.6f);
    int scaledSize = Math.max(1, Math.round(size * scale));

    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setColor(new Color(
      colors[0],
      colors[1],
      colors[2],
      alpha
    ));
    g2d.fillOval(
      (int) (dxx + x - (scaledSize / 2)),
      (int) (dyy + y - (scaledSize / 2)),
      size,
      size
    );
    g2d.dispose();
  }

  public static float[] getColor() {
    float base = 0.75f + (float) Math.random() * 0.25f;

    float r = base + ((float) Math.random() - 0.5f) * 0.05f;
    float g = base + ((float) Math.random() - 0.5f) * 0.05f;
    float b = base + ((float) Math.random() - 0.5f) * 0.05f;

    r = Math.min(1f, Math.max(0f, r));
    g = Math.min(1f, Math.max(0f, g));
    b = Math.min(1f, Math.max(0f, b));

    return new float[] { r, g, b, 1.0f };
  }
}
