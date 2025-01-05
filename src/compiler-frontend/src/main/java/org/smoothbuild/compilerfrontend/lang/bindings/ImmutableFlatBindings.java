package org.smoothbuild.compilerfrontend.lang.bindings;

import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.name.Name;

/**
 * Immutable bindings with single scope.
 */
public final class ImmutableFlatBindings<E> extends FlatBindings<E>
    implements ImmutableBindings<E> {
  ImmutableFlatBindings(Map<Name, E> map) {
    super(map.asJdkMap());
  }
}
