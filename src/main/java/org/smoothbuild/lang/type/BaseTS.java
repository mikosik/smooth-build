package org.smoothbuild.lang.type;

/**
 * This class is immutable.
 */
public sealed abstract class BaseTS extends MonoTS
    permits BlobTS, BoolTS, EdgeTS, IntTS, StringTS {
  public BaseTS(String name) {
    super(name);
  }
}

