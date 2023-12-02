package org.smoothbuild.common.bindings;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Maps.override;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.function.Function;
import org.smoothbuild.common.collect.Maybe;

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
  public <T> ScopedBindings<T> map(Function<? super E, T> mapper) {
    return new ScopedBindings<>(outerScopeBindings.map(mapper), innerScopeBindings.map(mapper));
  }

  @Override
  public ImmutableMap<String, E> toMap() {
    return override(innerScopeBindings.toMap(), outerScopeBindings.toMap());
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
