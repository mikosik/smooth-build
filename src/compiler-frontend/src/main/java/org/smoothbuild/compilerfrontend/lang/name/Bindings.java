package org.smoothbuild.compilerfrontend.lang.name;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.collect.Result.err;

import java.util.Objects;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.Result;
import org.smoothbuild.compilerfrontend.lang.base.HasName;

public final class Bindings<E> {
  private final Maybe<Bindings<? extends E>> outer;
  private final Map<Name, ? extends E> inner;

  public static <E> Bindings<E> bindings() {
    return new Bindings<>(none(), Map.map());
  }

  @SuppressWarnings("unchecked")
  public static <T extends HasName> Bindings<T> bindings(T... nameds) {
    return bindings(list(nameds).toMap(HasName::name, e -> e));
  }

  public static <E extends HasName> Bindings<E> bindings(Map<Name, E> elements) {
    return new Bindings<>(none(), elements);
  }

  public static <E> Bindings<E> bindings(
      Bindings<? extends E> outer, Map<Name, ? extends E> inner) {
    return new Bindings<>(some(outer), inner);
  }

  private Bindings(Maybe<Bindings<? extends E>> outer, Map<Name, ? extends E> inner) {
    this.outer = requireNonNull(outer);
    this.inner = requireNonNull(inner);
  }

  public Result<? extends E> find(Id id) {
    var parts = id.parts();
    if (parts.size() == 1) {
      return get(parts.get(0)).toResult(() -> cannotResolveErrorMessage(parts, 1));
    }
    return err(cannotResolveErrorMessage(parts, 2));
  }

  private static String cannotResolveErrorMessage(List<Name> parts, int toIndex) {
    return "Cannot resolve " + q(parts.subList(0, toIndex).toString(":")) + ".";
  }

  private Maybe<? extends E> get(Name name) {
    var element = maybe(inner.get(name));
    if (element.isSome()) {
      return element;
    } else {
      return switch (outer) {
        case Maybe.Some<Bindings<? extends E>> some -> some.get().get(name);
        case Maybe.None<?> none -> none();
      };
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
    return toString("");
  }

  private String toString(String innersAppendix) {
    var string = mapToString(inner) + innersAppendix;
    return outer.map(o -> o.toString("\n" + indent(string))).getOr(string);
  }

  private static String mapToString(Map<Name, ?> map) {
    var string =
        listOfAll(map.entrySet()).map(e -> e.getKey() + " -> " + e.getValue()).toString("\n");
    return string.isEmpty() ? "<empty bindings>" : string;
  }
}
