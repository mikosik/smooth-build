package org.smoothbuild.record.base;

/**
 * This class is immutable.
 */
public class Nothing extends RecordImpl {
  private Nothing() {
    super(null, null);
  }
}
