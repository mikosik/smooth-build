package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;

import java.util.HashMap;

import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.bindings.ImmutableBindings;
import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableMap;

public record DefsS(
    ImmutableBindings<TDefS> tDefs,
    ImmutableBindings<PolyRefableS> refables) {

  public static DefsS empty() {
    return new DefsS(immutableBindings(), immutableBindings());
  }

  public DefsS withModule(ModuleS module) {
    return new DefsS(
        merge(tDefs, module.tDefs()),
        merge(refables, module.refables())
    );
  }

  public <E extends Named> ImmutableBindings<E> merge(
      Bindings<E> outer, Bindings<? extends E> inner) {
    var map = new HashMap<String, E>();
    map.putAll(outer.asMap());
    map.putAll(inner.asMap());
    return immutableBindings(ImmutableMap.copyOf(map));
  }
}
