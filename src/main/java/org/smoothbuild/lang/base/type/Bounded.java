package org.smoothbuild.lang.base.type;

import static com.google.common.base.Preconditions.checkArgument;

public record Bounded(Variable variable, Bounds bounds) {
  public static Bounded mergeWith(Bounded bounded1, Bounded bounded2) {
    checkArgument(bounded1.variable().equals(bounded2.variable()));
    return new Bounded(bounded1.variable(), bounded1.bounds().mergeWith(bounded2.bounds()));
  }
}
