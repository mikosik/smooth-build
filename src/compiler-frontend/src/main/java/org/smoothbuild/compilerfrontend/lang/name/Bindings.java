package org.smoothbuild.compilerfrontend.lang.name;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.collect.Result.err;

import java.util.Objects;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Result;
import org.smoothbuild.compilerfrontend.lang.base.HasName;

public final class Bindings<E> {
  private final Bindings<? extends E> outer;
  private final Map<Name, ? extends E> inner;

  public static <E> Bindings<E> bindings() {
    return new Bindings<>(null, Map.map());
  }

  @SuppressWarnings("unchecked")
  public static <T extends HasName> Bindings<T> bindings(T... nameds) {
    return bindings(list(nameds));
  }

  public static <E extends HasName> Bindings<E> bindings(List<E> elements) {
    return bindings(elements.toMap(HasName::name, e -> e));
  }

  public static <E extends HasName> Bindings<E> bindings(Map<Name, E> elements) {
    return bindings(null, elements);
  }

  public static <E> Bindings<E> bindings(
      Bindings<? extends E> outer, Map<Name, ? extends E> inner) {
    return new Bindings<>(outer, inner);
  }

  Bindings(Bindings<? extends E> outer, Map<Name, ? extends E> inner) {
    this.outer = outer;
    this.inner = requireNonNull(inner);
  }

  public Result<E> find(Id id) {
    var parts = id.parts();
    if (parts.size() == 1) {
      return maybe(get(parts.get(0))).toResult(() -> cannotResolveErrorMessage(parts, 1));
    }
    return err(cannotResolveErrorMessage(parts, 2));
  }

  private static String cannotResolveErrorMessage(List<Name> parts, int toIndex) {
    return "Cannot resolve " + q(parts.subList(0, toIndex).toString(":")) + ".";
  }

  private E get(Name name) {
    E element = inner.get(name);
    if (element != null) {
      return element;
    } else if (outer == null) {
      return null;
    } else {
      return outer.get(name);
    }
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof Bindings<?> that
        && Objects.equals(this.outer, that.outer)
        && Objects.equals(this.inner, that.inner);
  }

  @Override
  public int hashCode() {
    return Objects.hash(outer, inner);
  }

  @Override
  public String toString() {
    var innerToString = mapToString(inner);
    return outer == null ? innerToString : outer + "\n" + indent(innerToString);
  }

  private static String mapToString(Map<Name, ?> map) {
    return listOfAll(map.entrySet())
        .map(e -> e.getKey() + " -> " + e.getValue())
        .toString("\n");
  }
}
