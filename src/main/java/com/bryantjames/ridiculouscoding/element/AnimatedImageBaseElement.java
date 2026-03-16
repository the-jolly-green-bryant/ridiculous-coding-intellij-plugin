package com.bryantjames.ridiculouscoding.element;

import com.bryantjames.ridiculouscoding.Direction;
import com.bryantjames.ridiculouscoding.PowerMode;
import com.bryantjames.ridiculouscoding.util.ImageUtil;
import com.bryantjames.ridiculouscoding.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimatedImageBaseElement extends BaseElement {

  private static final Map<String, List<BufferedImage>> IMAGE_CACHE = new HashMap<>();
  private float x;
  private int i;
  private float _x;
  private float y;
  private float _y;
  private int width;
  private int _width;
  private int height;
  private int _height;
  private long initLife;
  private long life;
  private Direction direction;
  private final String cacheKey;
  private final File folder;

  public AnimatedImageBaseElement(
    String folderPath,
    float _x,
    float _y,
    int _width,
    int _height,
    long initLife,
    Direction direction
  ) {
    this.x = _x;
    this.y = _y;
    this.width = 0;
    this.height = 0;
    this._x = _x;
    this._y = _y;
    this._width = _width;
    this._height = _height;
    this.initLife = initLife;
    this.life = System.currentTimeMillis() + initLife;
    this.direction = direction;

    this.folder = new File(folderPath);
    this.cacheKey = this.folder.getAbsolutePath();
    findImages();
  }

  private Image findImages() {
    List<BufferedImage> image = IMAGE_CACHE.get(cacheKey);
    if (image != null && !image.isEmpty()) {
      return image.get(0);
    }

    IMAGE_CACHE.clear();

    List<BufferedImage> images = ImageUtil.imagesForPath(this.folder);
    if (images == null || images.isEmpty()) {
      PowerMode
        .logger()
        .warn("No images loaded for: " + this.folder);
      return null;
    }

    IMAGE_CACHE.put(
      cacheKey,
      images
    );

    return images.get(0);
  }

  @Override
  public boolean update(double delta) {
    if (!isAlive()) {
      return true;
    }

    i += 1;
    x = _x - (int) (0.5 * _width * lifeFactor());
    // TODO - This can be simplified.
    if (direction == Direction.UP) {
      y = _y - (int) (1.1 * _height * lifeFactor());
    } else if (direction == Direction.DOWN) {
      y = _y + (int) (0.25 * _height * lifeFactor());
    } else if (direction == Direction.LEFT) {
      y = _y - (int) (0.5 * _height * lifeFactor());
    } else if (direction == Direction.RIGHT) {
      y = _y - (int) (0.5 * _height * lifeFactor());
    }

    width = (int) (_width * lifeFactor());
    height = (int) (_height * lifeFactor());
    return false;
  }

  @Override
  public void render(
    Graphics g,
    int dxx,
    int dyy
  ) {
    if (isAlive() && IMAGE_CACHE.get(cacheKey) != null) {
      int imageCount = IMAGE_CACHE
        .get(cacheKey)
        .size();
      Graphics2D g2d = (Graphics2D) g.create();
      g2d.setComposite(AlphaComposite.getInstance(
        AlphaComposite.SRC_OVER,
        Util.alpha(0.9f * (1 - lifeFactor()))
      ));
      if (direction == Direction.DOWN) {
        g2d.drawImage(
          IMAGE_CACHE
            .get(cacheKey)
            .get(i % imageCount),
          (int) x + dxx,
          (int) (y + dyy + height),
          width,
          -height,
          null
        );
        g2d.dispose();
        return;
      }

      g2d.drawImage(
        IMAGE_CACHE
          .get(cacheKey)
          .get(i % imageCount),
        (int) x + dxx,
        (int) y + dyy,
        width,
        height,
        null
      );
      g2d.dispose();
    }
  }

  @Override
  public long life() {
    return this.life;
  }

  @Override
  public long initLife() {
    return this.initLife;
  }

}
