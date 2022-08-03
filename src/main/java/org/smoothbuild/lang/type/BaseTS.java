package org.smoothbuild.lang.type;

/**
 * This class is immutable.
 */
public sealed abstract class BaseTS extends TypeS
    permits BlobTS, BoolTS, IntTS, StringTS {
  public BaseTS(String name) {
    super(name);
  }
}

