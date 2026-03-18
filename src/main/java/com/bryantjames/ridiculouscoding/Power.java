package com.bryantjames.ridiculouscoding;

// TODO - Remove this.
public interface Power {
  default PowerMode powerMode() {
    return PowerMode.getInstance();
  }
}
