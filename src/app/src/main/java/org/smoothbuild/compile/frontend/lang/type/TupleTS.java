package org.smoothbuild.compile.frontend.lang.type;

import static org.smoothbuild.compile.frontend.lang.base.TypeNamesS.tupleTypeName;
import static org.smoothbuild.compile.frontend.lang.type.VarSetS.varSetS;

import java.util.Objects;
import org.smoothbuild.common.collect.List;

/**
 * This class is immutable.
 */
public final class TupleTS extends TypeS {
  private final List<TypeS> elements;

  public TupleTS(List<? extends TypeS> elements) {
    super(tupleTypeName(elements), calculateVars(elements));
    @SuppressWarnings("unchecked")
    var cast = (List<TypeS>) elements;
    this.elements = cast;
  }

  private static VarSetS calculateVars(List<? extends TypeS> elements) {
    return varSetS(elements);
  }

  public List<TypeS> elements() {
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
    return object instanceof TupleTS that && elements.equals(that.elements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(elements);
  }
}
