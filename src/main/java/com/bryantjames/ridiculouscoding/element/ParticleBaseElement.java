package com.bryantjames.ridiculouscoding.element;

import java.awt.*;
import java.awt.geom.Point2D;

public class ParticleBaseElement extends BaseElement {
  private final Point2D.Float direction;
  private final int size;
  private final float[] colors;
  private final float speed = 0.55f;

  public ParticleBaseElement(
    float x,
    float y,
    float dx,
    float dy,
    int size,
    long initLife,
    float[] colors
  ) {
    this.origin = new Point2D.Float(
      x,
      y
    );
    this.direction = new Point2D.Float(
      dx,
      dy
    );
    this.size = size;
    this.initLife = initLife;
    this.colors = colors;
    this.life = System.currentTimeMillis() + initLife;
  }

  public static float[] getColor() {
    float base = 0.75f + (float) Math.random() * 0.25f;

    float r = base + ((float) Math.random() - 0.5f) * 0.05f;
    float g = base + ((float) Math.random() - 0.5f) * 0.05f;
    float b = base + ((float) Math.random() - 0.5f) * 0.05f;

    r = Math.min(
      1f,
      Math.max(
        0f,
        r
      )
    );
    g = Math.min(
      1f,
      Math.max(
        0f,
        g
      )
    );
    b = Math.min(
      1f,
      Math.max(
        0f,
        b
      )
    );

    return new float[]{r, g, b, 1.0f};
  }

  @Override
  public void update(double delta) {
    this.origin.x += (float) (this.direction.x * delta * this.speed);
    this.origin.y += (float) (this.direction.y * delta * this.speed);
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
    alpha = Math.max(
      0f,
      Math.min(
        1f,
        alpha
      )
    );
    float scale = 1.0f - (p * 0.6f);
    int scaledSize = Math.max(
      1,
      Math.round(size * scale)
    );

    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setColor(new Color(
      colors[0],
      colors[1],
      colors[2],
      alpha
    ));
    g2d.fillRect(
      (int) (dxx + this.origin.x - ((float) scaledSize / 2)),
      (int) (dyy + this.origin.y - ((float) scaledSize / 2)),
      size,
      size
    );
    g2d.dispose();
  }
}
