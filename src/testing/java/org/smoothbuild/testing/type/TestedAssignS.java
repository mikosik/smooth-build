package org.smoothbuild.testing.type;

import static com.google.common.collect.Sets.union;
import static java.lang.String.join;

public record TestedAssignS(TestedTS target, TestedTS source) implements TestedAssign<TestedTS> {

  public String declarations() {
    return join("\n", union(target.allDeclarations(), source.allDeclarations()));
  }

  public String typeDeclarations() {
    return join("\n", union(target.typeDeclarations(), source.typeDeclarations()));
  }

  @Override
  public String toString() {
    return target().name() + " <- " + source().name();
  }
}
