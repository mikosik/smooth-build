package org.smoothbuild.compilerfrontend.lang.base.location;

import org.smoothbuild.common.bucket.base.BucketId;

public final class CommandLineLocation implements SourceLocation {
  public static final CommandLineLocation INSTANCE = new CommandLineLocation();

  private CommandLineLocation() {}

  @Override
  public int line() {
    return 0;
  }

  @Override
  public BucketId bucketId() {
    return null;
  }

  @Override
  public String toString() {
    return "command line";
  }
}
