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
    var bodyScopeBindings = new ScopedBindings<Optional<? extends RefableS>>(bindings);
    for (var param : params) {
      bodyScopeBindings.add(param.name(), Optional.of(itemS(param)));
    }
    return bodyScopeBindings;
  }

  private static ItemS itemS(ItemP p) {
    return new ItemS(p.typeS(), p.name(), Optional.empty(), p.loc());
  }
}
