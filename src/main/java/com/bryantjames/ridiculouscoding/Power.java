package com.bryantjames.ridiculouscoding;

public interface Power {
  default PowerMode powerMode() {
    return PowerMode.getInstance();
  }
}
