package com.bryantjames.ridiculouscoding.element;

import com.bryantjames.ridiculouscoding.Direction;
import com.bryantjames.ridiculouscoding.PowerMode;
import com.bryantjames.ridiculouscoding.util.ImageUtil;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimatedImageBaseElement extends BaseElement {

  private static final Map<String, List<BufferedImage>> IMAGE_CACHE = new HashMap<>();
  private Point2D.Float renderAt;
  private int renderWidth;
  private final int width;
  private int renderHeight;
  private final int height;
  private final String cacheKey;
  private final File folder;
  private final Point2D.Float origin;

  public AnimatedImageBaseElement(
    String folderPath,
    float x,
    float y,
    int width,
    int height,
    long initLife,
    Direction direction
  ) {
    this.origin = new Point2D.Float(
      x,
      y
    );
    this.renderAt = new Point2D.Float(
      this.origin.x,
      this.origin.y
    );
    this.renderWidth = 0;
    this.renderHeight = 0;
    this.width = width;
    this.height = height;
    this.initLife = initLife;
    this.life = System.currentTimeMillis() + initLife;

    this.folder = new File(folderPath);
    this.cacheKey = this.folder.getAbsolutePath();
    cacheImages();
  }

  private void cacheImages() {
    List<BufferedImage> image = IMAGE_CACHE.get(cacheKey);
    if (image != null && !image.isEmpty()) {
      return;
    }

    IMAGE_CACHE.clear();

    List<BufferedImage> images = ImageUtil.imagesForPath(this.folder);
    if (images == null || images.isEmpty()) {
      PowerMode
        .logger()
        .warn("No images loaded for: " + this.folder);
      throw new RuntimeException("No images loaded for: " + this.folder);
    }

    IMAGE_CACHE.put(
      cacheKey,
      images
    );
  }

  @Override
  public boolean update(double delta) {
    if (!isAlive()) {
      return true;
    }

    float p = progress();

    float scale = 0.9f + (0.25f * p); // 0.90 -> 1.15
    renderWidth = Math.round(width * scale);
    renderHeight = Math.round(height * scale);

    this.renderAt = new Point2D.Float(
      this.origin.x - ((renderWidth - width) / 2f),
      this.origin.y - ((renderHeight - height) / 2f)
    );

    return false;
  }

  @Override
  public void render(
    Graphics g,
    int dxx,
    int dyy
  ) {
    if (!isAlive() || IMAGE_CACHE.get(cacheKey) == null) {
      return;
    }

    List<BufferedImage> images = IMAGE_CACHE.get(this.cacheKey);
    int imageCount = images.size();
    float p = progress();
    int frame = Math.min(
      (int) (p * (imageCount - 1)),
      imageCount - 1
    );

    Graphics2D g2d = (Graphics2D) g.create();

    float alpha = 0.95f - (0.3f * p); // 0.95 -> 0.20
    g2d.setComposite(AlphaComposite.getInstance(
      AlphaComposite.SRC_OVER,
      Math.max(
        0f,
        Math.min(
          1f,
          alpha
        )
      )
    ));

    g2d.drawImage(
      images.get(frame),
      (int) this.renderAt.x + dxx,
      (int) this.renderAt.y + dyy,
      renderWidth,
      renderHeight,
      null
    );
    g2d.dispose();
  }
}
