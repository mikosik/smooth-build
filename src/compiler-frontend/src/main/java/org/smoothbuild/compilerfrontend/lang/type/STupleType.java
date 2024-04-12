package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.base.STypeNames.tupleTypeName;

import java.util.Objects;
import org.smoothbuild.common.collect.List;

/**
 * This class is immutable.
 */
public final class STupleType extends SType {
  private final List<SType> elements;

  public STupleType(List<? extends SType> elements) {
    super(tupleTypeName(elements), calculateVars(elements));
    @SuppressWarnings("unchecked")
    var cast = (List<SType>) elements;
    this.elements = cast;
  }

  private static SVarSet calculateVars(List<? extends SType> elements) {
    return SVarSet.varSetS(elements);
  }

  public List<SType> elements() {
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
    return object instanceof STupleType that && elements.equals(that.elements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(elements);
  }
}
