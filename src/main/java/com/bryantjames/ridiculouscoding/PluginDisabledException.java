package com.bryantjames.ridiculouscoding;

public class PluginDisabledException extends RuntimeException {
  private static final String DEFAULT_MESSAGE = "Plugin not enabled!";

  public PluginDisabledException() {
    super(DEFAULT_MESSAGE);
  }

  public PluginDisabledException(String message) {
    super(message);
  }

  public static void requirePluginEnabled() {
    if (!PowerMode
      .getInstance()
      .isEnabled()) {
      throw new PluginDisabledException();
    }
  }

  public static void requireNotNull(Object o) {
    if (o == null) {
      throw new PluginDisabledException("Object is null!");
    }
  }
}
