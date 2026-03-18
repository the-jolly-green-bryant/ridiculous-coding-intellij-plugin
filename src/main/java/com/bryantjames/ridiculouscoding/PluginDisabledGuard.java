package com.bryantjames.ridiculouscoding;

public final class PluginDisabledGuard {

  public static void run(Runnable r) {
    try {
      r.run();
    } catch (PluginDisabledException ignored) {
    }
  }
}