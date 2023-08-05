package org.smoothbuild.filesystem.space;

import java.lang.annotation.Annotation;
import java.util.Objects;

public class ForSpaceImpl implements ForSpace {
  private final Space space;

  public ForSpaceImpl(Space space) {
    this.space = space;
  }

  @Override
  public Space value() {
    return space;
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return ForSpace.class;
  }

  @Override
  public int hashCode() {
    // As specified in java.lang.Annotation.
    return (127 * "value".hashCode()) ^ space.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ForSpace that
        && Objects.equals(this.value(), that.value());
  }

  @Override
  public String toString() {
    return '@' + ForSpace.class.getName() + "(value="+ space + ')';
  }
}
