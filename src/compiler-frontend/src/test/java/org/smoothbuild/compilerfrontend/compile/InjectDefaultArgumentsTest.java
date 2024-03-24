package org.smoothbuild.compilerfrontend.compile;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.bindings;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pCall;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pModule;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pNamedValue;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pReference;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;
import org.smoothbuild.compilerfrontend.testing.TestingSExpression;

public class InjectDefaultArgumentsTest {
  @Test
  public void missing_call_argument_is_filled_with_reference_to_default_argument() {
    var myFuncS = TestingSExpression.sFunc(
        "myFunc",
        nlist(TestingSExpression.sItem("param", TestingSExpression.sInt(7))),
        TestingSExpression.sParamRef(sIntType(), "param"));
    var importedS = new ScopeS(immutableBindings(), bindings(myFuncS));
    var callLocation = location(9);
    var callP = pCall(TestingSExpression.pReference("myFunc"), callLocation);
    var namedValueP = pNamedValue("value", callP);
    var moduleP = pModule(list(), list(namedValueP));

    callInjectDefaultArguments(importedS, moduleP);

    assertThat(callP.positionedArgs()).isEqualTo(list(pReference("myFunc:param", callLocation)));
  }

  @Test
  public void
      missing_call_argument_in_call_within_default_body_is_filled_with_reference_to_default_argument() {
    var myFuncS = TestingSExpression.sFunc(
        "myFunc",
        nlist(TestingSExpression.sItem("param", TestingSExpression.sInt(7))),
        TestingSExpression.sParamRef(sIntType(), "param"));
    var importedS = new ScopeS(immutableBindings(), bindings(myFuncS));
    var callLocation = location(9);
    var callP = pCall(TestingSExpression.pReference("myFunc"), callLocation);
    var namedValueP =
        TestingSExpression.pNamedFunc("value", nlist(TestingSExpression.pItem("p", callP)));
    var moduleP = pModule(list(), list(namedValueP));

    callInjectDefaultArguments(importedS, moduleP);

    assertThat(callP.positionedArgs()).isEqualTo(list(pReference("myFunc:param", callLocation)));
  }

  private static void callInjectDefaultArguments(ScopeS importedS, PModule pModule) {
    new InitializeScopes().apply(pModule);
    new InjectDefaultArguments().apply(pModule, importedS);
  }
}
