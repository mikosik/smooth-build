package org.smoothbuild.compile.ps.infer;

import java.util.Optional;

import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.RefableS;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.bindings.ScopedBindings;
import org.smoothbuild.util.collect.NList;

public class BindingsHelper {
  public static ScopedBindings<Optional<? extends RefableS>> funcBodyScopeBindings(
      Bindings<? extends Optional<? extends RefableS>> bindings, NList<ItemP> params) {
    return funcBodyScopeBindings2(bindings, params.map(BindingsHelper::itemS));
  }

  private static ItemS itemS(ItemP p) {
    return new ItemS(p.typeS(), p.name(), Optional.empty(), p.loc());
  }

  public static ScopedBindings<Optional<? extends RefableS>> funcBodyScopeBindings2(
      Bindings<? extends Optional<? extends RefableS>> bindings, NList<ItemS> params) {
    var bindingsInBody = new ScopedBindings<Optional<? extends RefableS>>(bindings);
    for (var param : params) {
      bindingsInBody.add(param.name(), Optional.of(param));
    }
    return bindingsInBody;
  }
}
