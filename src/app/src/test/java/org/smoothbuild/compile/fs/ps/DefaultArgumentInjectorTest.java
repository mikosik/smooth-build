package org.smoothbuild.compile.fs.ps;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.Lists.list;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compile.fs.ps.DefaultArgumentInjector.injectDefaultArguments;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.testing.TestContext;

public class DefaultArgumentInjectorTest extends TestContext  {
  @Test
  public void missing_call_argument_is_filled_with_reference_to_default_argument() {
    var myFuncS = funcS("myFunc", nlist(itemS("param", intS(7))), paramRefS(intTS(), "param"));
    var importedS = new ScopeS(immutableBindings(), bindings(myFuncS));
    var callLocation = location(9);
    var callP = callP(referenceP("myFunc"), callLocation);
    var namedValueP = namedValueP("value", callP);
    var moduleP = moduleP(list(), list(namedValueP));

    ScopesInitializer.initializeScopes(moduleP);
    injectDefaultArguments(moduleP, importedS);

    assertThat(callP.positionedArgs())
        .isEqualTo(list(referenceP("myFunc:param", callLocation)));
  }

  @Test
  public void missing_call_argument_in_call_within_default_body_is_filled_with_reference_to_default_argument() {
    var myFuncS = funcS("myFunc", nlist(itemS("param", intS(7))), paramRefS(intTS(), "param"));
    var importedS = new ScopeS(immutableBindings(), bindings(myFuncS));
    var callLocation = location(9);
    var callP = callP(referenceP("myFunc"), callLocation);
    var namedValueP = namedFuncP("value", nlist(itemP("p", callP)));
    var moduleP = moduleP(list(), list(namedValueP));

    ScopesInitializer.initializeScopes(moduleP);
    injectDefaultArguments(moduleP, importedS);

    assertThat(callP.positionedArgs())
        .isEqualTo(list(referenceP("myFunc:param", callLocation)));
  }
}
