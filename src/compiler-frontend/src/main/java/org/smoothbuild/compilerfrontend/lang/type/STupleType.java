package org.smoothbuild.compilerfrontend.lang.type;

import java.util.Objects;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Set;

/**
 * This class is immutable.
 */
public final class STupleType extends SType {
  private final List<SType> elements;

  public STupleType(List<? extends SType> elements) {
    @SuppressWarnings("unchecked")
    var cast = (List<SType>) elements;
    this.elements = cast;
  }

  @Override
  protected Set<STypeVar> calculateTypeVars() {
    return elements.flatMap(SType::typeVars).toSet();
  }

  public List<SType> elements() {
    return elements;
  }

  public int size() {
    return elements.size();
  }

  @Override
  public String specifier() {
    return "{" + elements.map(SType::specifier).toString(",") + "}";
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
