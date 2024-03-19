package org.smoothbuild.compilerfrontend.compile;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.bindings;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.callP;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.funcS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.itemP;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.itemS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.location;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.moduleP;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.namedFuncP;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.namedValueP;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.paramRefS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.referenceP;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.compile.ast.define.ModuleP;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;

public class InjectDefaultArgumentsTest {
  @Test
  public void missing_call_argument_is_filled_with_reference_to_default_argument() {
    var myFuncS = funcS("myFunc", nlist(itemS("param", intS(7))), paramRefS(intTS(), "param"));
    var importedS = new ScopeS(immutableBindings(), bindings(myFuncS));
    var callLocation = location(9);
    var callP = callP(referenceP("myFunc"), callLocation);
    var namedValueP = namedValueP("value", callP);
    var moduleP = moduleP(list(), list(namedValueP));

    callInjectDefaultArguments(importedS, moduleP);

    assertThat(callP.positionedArgs()).isEqualTo(list(referenceP("myFunc:param", callLocation)));
  }

  @Test
  public void
      missing_call_argument_in_call_within_default_body_is_filled_with_reference_to_default_argument() {
    var myFuncS = funcS("myFunc", nlist(itemS("param", intS(7))), paramRefS(intTS(), "param"));
    var importedS = new ScopeS(immutableBindings(), bindings(myFuncS));
    var callLocation = location(9);
    var callP = callP(referenceP("myFunc"), callLocation);
    var namedValueP = namedFuncP("value", nlist(itemP("p", callP)));
    var moduleP = moduleP(list(), list(namedValueP));

    callInjectDefaultArguments(importedS, moduleP);

    assertThat(callP.positionedArgs()).isEqualTo(list(referenceP("myFunc:param", callLocation)));
  }

  private static void callInjectDefaultArguments(ScopeS importedS, ModuleP moduleP) {
    new InitializeScopes().apply(moduleP);
    new InjectDefaultArguments().apply(moduleP, importedS);
  }
}
