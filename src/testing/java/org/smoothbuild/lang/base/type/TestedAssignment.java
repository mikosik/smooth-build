package org.smoothbuild.lang.base.type;

import static com.google.common.collect.Sets.union;
import static java.lang.String.join;

public class TestedAssignment {
  public final TestedType target;
  public final TestedType source;

  public TestedAssignment(TestedType target, TestedType source) {
    this.target = target;
    this.source = source;
  }

  public String declarations() {
    return join("\n", union(target.declarations(), source.declarations()));
  }

  @Override
  public String toString() {
    return target.name() + " <- " + source.name();
  }
}
