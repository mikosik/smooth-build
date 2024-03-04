package org.smoothbuild.common.bindings;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.base.Strings.indent;

import java.util.Objects;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.function.Function1;

public sealed class ScopedBindings<E> extends AbstractBindings<E>
    permits ScopedImmutableBindings, ScopedMutableBindings {
  private final Bindings<? extends E> outerScopeBindings;
  private final Bindings<? extends E> innerScopeBindings;

  protected ScopedBindings(
      Bindings<? extends E> outerScopeBindings, Bindings<? extends E> innerScopeBindings) {
    this.outerScopeBindings = requireNonNull(outerScopeBindings);
    this.innerScopeBindings = requireNonNull(innerScopeBindings);
  }

  @Override
  public Maybe<E> getMaybe(String name) {
    Maybe<E> element = innerScopeBindings.getMaybe(name).map(e -> e);
    if (element.isSome()) {
      return element;
    } else {
      return outerScopeBindings.getMaybe(name).map(e -> e);
    }
  }

  @Override
  public <F, T extends Throwable> Bindings<F> map(Function1<? super E, F, T> mapper) throws T {
    return new ScopedBindings<>(outerScopeBindings.map(mapper), innerScopeBindings.map(mapper));
  }

  @Override
  public Map<String, E> toMap() {
    @SuppressWarnings("unchecked")
    Map<String, E> map = (Map<String, E>) outerScopeBindings.toMap();
    return map.overrideWith(innerScopeBindings.toMap());
  }

  public Bindings<? extends E> outerScopeBindings() {
    return outerScopeBindings;
  }

  public Bindings<? extends E> innerScopeBindings() {
    return innerScopeBindings;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof ScopedBindings<?> that
        && Objects.equals(this.outerScopeBindings, that.outerScopeBindings)
        && Objects.equals(this.innerScopeBindings, that.innerScopeBindings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(outerScopeBindings, innerScopeBindings);
  }

  @Override
  public String toString() {
    return outerScopeBindings + "\n" + indent(innerScopeBindings.toString());
  }
}
