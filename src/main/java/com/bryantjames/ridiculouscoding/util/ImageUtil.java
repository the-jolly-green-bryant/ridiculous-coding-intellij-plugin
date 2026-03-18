package com.bryantjames.ridiculouscoding.util;


import com.bryantjames.ridiculouscoding.PowerMode;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class ImageUtil {
  private static final Logger LOG = Logger.getInstance(ImageUtil.class);

  public static List<BufferedImage> imagesForPath(@Nullable File folderOrFile) {
    if (folderOrFile == null) {
      return List.of();
    }

    try {
      if (folderOrFile.exists()) {
        return loadFromFilesystem(folderOrFile);
      }

      String path = folderOrFile
        .getPath()
        .toLowerCase(Locale.ROOT);

      if (path.contains("bam")) {
        return loadBundledSingle("bam/bam.png");
      }

      if (path.contains("fire")) {
        // Adjust frame count / extension to match your actual assets
        return loadBundledSeries(
          "fire/animated/256",
          "fire1_ %02d.png",
          17
        );
      }

      if (path.contains("reticule")) {
        // Adjust frame count / extension to match your actual assets
        return loadBundledSeries(
          "reticule",
          "tile%03d.png",
          8
        );
      }

      if (path.contains("explosion")) {
        // Adjust frame count / extension to match your actual assets
        return loadBundledSeries(
          "explosion",
          "tile%03d.png",
          6
        );
      }

      LOG.warn("Unknown image path fallback: " + folderOrFile.getPath());
      return List.of();
    } catch (Exception e) {
      LOG.warn(
        "Failed loading images for path: " + folderOrFile.getPath(),
        e
      );
      return List.of();
    }
  }

  private static List<BufferedImage> loadFromFilesystem(File fileOrDir) throws IOException {
    if (fileOrDir.isDirectory()) {
      try (var stream = Files.walk(fileOrDir.toPath())) {
        return stream
          .filter(Files::isRegularFile)
          .sorted(Comparator.comparing(path -> path
            .getFileName()
            .toString()))
          .map(path -> {
            try {
              return ImageIO.read(path.toFile());
            } catch (IOException e) {
              LOG.warn(
                "Failed reading image file: " + path,
                e
              );
              return null;
            }
          })
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
      }
    }

    BufferedImage image = ImageIO.read(fileOrDir);
    return image == null ? List.of() : List.of(image);
  }

  private static List<BufferedImage> loadBundledSingle(String resourcePath) {
    BufferedImage image = readBundledImage(resourcePath);
    return image == null ? List.of() : List.of(image);
  }

  private static List<BufferedImage> loadBundledSeries(
    String basePath,
    String filePattern,
    int endInclusive
  ) {
    int startInclusive = 1;
    List<BufferedImage> images = new ArrayList<>();
    String normalizedBase = stripLeadingSlash(basePath);

    for (int i = startInclusive; i <= endInclusive; i++) {
      String resourcePath = normalizedBase + "/" + String.format(
        filePattern,
        i
      );
      BufferedImage image = readBundledImage(resourcePath);
      if (image != null) {
        images.add(image);
      }
    }

    if (images.isEmpty()) {
      PowerMode
        .logger()
        .warn("No bundled images found for base path: " + normalizedBase);
    }

    return images;
  }

  private static @Nullable BufferedImage readBundledImage(String resourcePath) {
    String normalized = stripLeadingSlash(resourcePath);

    try (InputStream in = ImageUtil.class
      .getClassLoader()
      .getResourceAsStream(normalized)) {
      if (in == null) {
        LOG.warn("Missing bundled image: " + normalized);
        return null;
      }

      BufferedImage image = ImageIO.read(in);
      if (image == null) {
        LOG.warn("Unreadable bundled image: " + normalized);
      }
      return image;
    } catch (IOException e) {
      LOG.warn(
        "Failed loading bundled image: " + normalized,
        e
      );
      return null;
    }
  }

  private static String stripLeadingSlash(String path) {
    return path.startsWith("/") ? path.substring(1) : path;
  }
}
