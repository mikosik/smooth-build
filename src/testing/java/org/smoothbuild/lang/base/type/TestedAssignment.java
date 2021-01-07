package org.smoothbuild.lang.base.type;

import static com.google.common.collect.Sets.union;
import static java.lang.String.join;

public record TestedAssignment(TestedType target, TestedType source) {
  public String declarations() {
    return join("\n", union(target.declarations(), source.declarations()));
  }

  @Override
  public String toString() {
    return target.name() + " <- " + source.name();
  }
}
