package com.bryantjames.ridiculouscoding.element;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

public class HighlightElement extends BaseElement {
  private final int width;
  private final int height;
  private final Color color;

  public HighlightElement(
    int x,
    int y,
    int width,
    int height,
    long initLife,
    Color color
  ) {
    this.origin = new Point2D.Float(x, y);
    this.width = width;
    this.height = height;
    this.initLife = initLife;
    this.life = System.currentTimeMillis() + initLife;
    this.color = color;
  }

  @Override
  public void render(
    Graphics g,
    int dxx,
    int dyy
  ) {
    Graphics2D g2d = (Graphics2D) g.create();
    try {
      float p = progress();
      float alpha = 1f - p;

      g2d.setComposite(AlphaComposite.getInstance(
        AlphaComposite.SRC_OVER,
        alpha * 0.35f
      ));

      g2d.setColor(color);

      g2d.fillRect(
        (int) this.origin.x + dxx,
        (int) this.origin.y + dyy + height,
        width,
        height
      );
    } finally {
      g2d.dispose();
    }
  }
}