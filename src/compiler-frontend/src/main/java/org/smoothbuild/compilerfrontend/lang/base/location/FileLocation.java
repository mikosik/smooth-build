package org.smoothbuild.compilerfrontend.lang.base.location;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.bucket.base.BucketId;
import org.smoothbuild.common.bucket.base.FullPath;

public record FileLocation(FullPath file, int line) implements SourceLocation {

  public FileLocation(FullPath file, int line) {
    this.file = requireNonNull(file);
    this.line = line;
  }

  public BucketId bucketId() {
    return file().bucketId();
  }

  @Override
  public String toString() {
    return file + ":" + line;
  }
}
