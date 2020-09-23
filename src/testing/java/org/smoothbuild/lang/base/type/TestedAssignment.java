package org.smoothbuild.lang.base.type;

import static java.lang.String.join;
import static org.smoothbuild.util.Sets.set;

public class TestedAssignment {
  public final TestedType target;
  public final TestedType source;

  public TestedAssignment(TestedType target, TestedType source) {
    this.target = target;
    this.source = source;
  }

  public String declarations() {
    return join("\n", set(target.declarations(), source.declarations()));
  }

  @Override
  public String toString() {
    return target.name() + " <- " + source.name();
  }
}
