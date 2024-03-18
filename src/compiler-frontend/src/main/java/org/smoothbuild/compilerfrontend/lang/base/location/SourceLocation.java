package org.smoothbuild.compilerfrontend.lang.base.location;

import org.smoothbuild.common.bucket.base.BucketId;

public sealed interface SourceLocation extends Location permits CommandLineLocation, FileLocation {
  public BucketId bucketId();

  public int line();
}
