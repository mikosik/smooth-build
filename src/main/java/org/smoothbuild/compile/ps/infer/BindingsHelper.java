package org.smoothbuild.compile.ps.infer;

import java.util.Optional;

import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.RefableS;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.bindings.ScopedBindings;
import org.smoothbuild.util.collect.NList;

public class BindingsHelper {
  public static ScopedBindings<Optional<? extends RefableS>> funcBodyScopeBindings(
      Bindings<? extends Optional<? extends RefableS>> bindings, NList<ItemS> params) {
    var bindingsInBody = new ScopedBindings<Optional<? extends RefableS>>(bindings);
    for (var param : params) {
      bindingsInBody.add(param.name(), Optional.of(param));
    }
    return bindingsInBody;
  }
}
