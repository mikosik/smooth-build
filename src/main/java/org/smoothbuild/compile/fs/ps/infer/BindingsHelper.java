package org.smoothbuild.compile.fs.ps.infer;

import java.util.Optional;

import org.smoothbuild.compile.fs.lang.define.ItemS;
import org.smoothbuild.compile.fs.lang.define.RefableS;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.bindings.OptionalBindings;
import org.smoothbuild.util.collect.NList;

public class BindingsHelper {
  public static OptionalBindings<? extends RefableS> funcBodyScopeBindings(
      Bindings<? extends Optional<? extends RefableS>> bindings, NList<ItemS> params) {
    OptionalBindings<RefableS> bindingsInBody = new OptionalBindings<>(bindings);
    for (var param : params) {
      bindingsInBody.add(param.name(), Optional.of(param));
    }
    return bindingsInBody;
  }
}
