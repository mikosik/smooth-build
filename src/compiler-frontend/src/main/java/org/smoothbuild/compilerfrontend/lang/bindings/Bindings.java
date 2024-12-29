package org.smoothbuild.compilerfrontend.lang.bindings;

import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.collect.Result.error;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Result;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.name.Name;

public sealed interface Bindings<E>
    permits FlatBindings, ImmutableBindings, MutableBindings, ScopedBindings {
  public static <E> ImmutableFlatBindings<E> immutableBindings() {
    return new ImmutableFlatBindings<>(Map.map());
  }

  public static <E> ImmutableFlatBindings<E> immutableBindings(Map<String, E> innerScopeMap) {
    return new ImmutableFlatBindings<>(innerScopeMap);
  }

  public static <E> ImmutableScopedBindings<E> immutableBindings(
      ImmutableBindings<? extends E> outerScopeBindings, Map<String, ? extends E> innerScopeMap) {
    return immutableBindings(outerScopeBindings, immutableBindings(innerScopeMap));
  }

  public static <E> ImmutableScopedBindings<E> immutableBindings(
      ImmutableBindings<? extends E> outerScopeBindings,
      ImmutableBindings<? extends E> innerScopeBindings) {
    return new ImmutableScopedBindings<>(outerScopeBindings, innerScopeBindings);
  }

  public static <E> MutableFlatBindings<E> mutableBindings() {
    return new MutableFlatBindings<>();
  }

  public static <E> MutableScopedBindings<E> mutableBindings(Bindings<E> outerScopeBindings) {
    return new MutableScopedBindings<>(outerScopeBindings);
  }

  public default Result<E> find(Id id) {
    var parts = id.parts();
    if (parts.size() == 1) {
      return maybe(get(parts.get(0).toString()))
          .toResult(() -> cannotResolveErrorMessage(parts, 1));
    }
    return error(cannotResolveErrorMessage(parts, 2));
  }

  private static String cannotResolveErrorMessage(List<Name> parts, int toIndex) {
    return "Cannot resolve " + q(parts.subList(0, toIndex).toString(":")) + ".";
  }

  public default ImmutableFlatBindings<E> toFlatImmutable() {
    return immutableBindings(toMap());
  }

  public E get(String name);

  public <F, T extends Throwable> Bindings<F> map(Function1<? super E, F, T> mapper) throws T;

  public Map<String, E> toMap();
}
