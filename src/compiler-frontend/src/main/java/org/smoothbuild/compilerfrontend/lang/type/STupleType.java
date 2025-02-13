package org.smoothbuild.compilerfrontend.lang.type;

import static org.smoothbuild.compilerfrontend.lang.type.STypeVarSet.sTypeVarSet;

import java.util.Objects;
import org.smoothbuild.common.collect.List;

/**
 * This class is immutable.
 */
public final class STupleType extends SType {
  private final List<SType> elements;

  public STupleType(List<? extends SType> elements) {
    super(calculateTypeVars(elements));
    @SuppressWarnings("unchecked")
    var cast = (List<SType>) elements;
    this.elements = cast;
  }

  private static STypeVarSet calculateTypeVars(List<? extends SType> elements) {
    return sTypeVarSet(elements);
  }

  public List<SType> elements() {
    return elements;
  }

  public int size() {
    return elements.size();
  }

  @Override
  public String specifier(STypeVarSet localTypeVars) {
    return "{" + elements.map(type -> type.specifier(localTypeVars)).toString(",") + "}";
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
