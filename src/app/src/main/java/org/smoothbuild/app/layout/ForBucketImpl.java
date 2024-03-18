package org.smoothbuild.app.layout;

import java.lang.annotation.Annotation;
import java.util.Objects;

public class ForBucketImpl implements ForBucket {
  private final SmoothBucketId smoothBucketId;

  public ForBucketImpl(SmoothBucketId smoothBucketId) {
    this.smoothBucketId = smoothBucketId;
  }

  @Override
  public SmoothBucketId value() {
    return smoothBucketId;
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return ForBucket.class;
  }

  @Override
  public int hashCode() {
    // As specified in java.lang.Annotation.
    return (127 * "value".hashCode()) ^ smoothBucketId.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ForBucket that && Objects.equals(this.value(), that.value());
  }

  @Override
  public String toString() {
    return '@' + ForBucket.class.getName() + "(value=" + smoothBucketId + ')';
  }
}
