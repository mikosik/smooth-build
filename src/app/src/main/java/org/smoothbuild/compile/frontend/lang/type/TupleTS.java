package org.smoothbuild.compile.frontend.lang.type;

import static org.smoothbuild.compile.frontend.lang.base.TypeNamesS.tupleTypeName;
import static org.smoothbuild.compile.frontend.lang.type.VarSetS.varSetS;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public final class TupleTS extends TypeS {
  private final ImmutableList<TypeS> elements;

  public TupleTS(List<? extends TypeS> elements) {
    super(tupleTypeName(elements), calculateVars(elements));
    this.elements = ImmutableList.copyOf(elements);
  }

  private static VarSetS calculateVars(List<? extends TypeS> elements) {
    return varSetS(elements);
  }

  public ImmutableList<TypeS> elements() {
    return elements;
  }

  public int size() {
    return elements.size();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof TupleTS that
        && elements.equals(that.elements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(elements);
  }
}
