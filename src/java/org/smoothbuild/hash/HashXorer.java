package org.smoothbuild.hash;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.hash.HashCode;

public class HashXorer {
  private final byte[] bytes;

  public HashXorer() {
    this(Hash.size());
  }

  @VisibleForTesting
  HashXorer(int size) {
    this.bytes = new byte[size];
  }

  public void xorWith(HashCode hash) {
    byte[] hashBytes = hash.asBytes();
    checkSize(hashBytes);
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = (byte) (bytes[i] ^ hashBytes[i]);
    }
  }

  private void checkSize(byte[] hashBytes) {
    if (bytes.length != hashBytes.length) {
      throw new IllegalArgumentException("Wrong hash size, expected " + bytes.length + " got "
          + hashBytes.length);
    }
  }

  public HashCode hash() {
    return HashCode.fromBytes(bytes);
  }
}
