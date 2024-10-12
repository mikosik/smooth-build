package org.smoothbuild.compilerfrontend.compile;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.bindings;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.location;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pCall;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pItem;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pModule;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pNamedFunc;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pNamedValue;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pReference;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sFunc;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sInt;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sItem;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sParamRef;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.lang.define.SScope;

public class InjectDefaultArgumentsTest {
  @Test
  void missing_call_argument_is_filled_with_reference_to_default_argument() {
    var myFuncS = sFunc("myFunc", nlist(sItem("param", sInt(7))), sParamRef(sIntType(), "param"));
    var importedS = new SScope(immutableBindings(), bindings(myFuncS));
    var callLocation = location(9);
    var callP = pCall(pReference("myFunc"), callLocation);
    var namedValueP = pNamedValue("value", callP);
    var moduleP = pModule(list(), list(namedValueP));

    callInjectDefaultArguments(importedS, moduleP);

    assertThat(callP.positionedArgs()).isEqualTo(list(pReference("myFunc:param", callLocation)));
  }

  @Test
  void
      missing_call_argument_in_call_within_default_body_is_filled_with_reference_to_default_argument() {
    var myFuncS = sFunc("myFunc", nlist(sItem("param", sInt(7))), sParamRef(sIntType(), "param"));
    var importedS = new SScope(immutableBindings(), bindings(myFuncS));
    var callLocation = location(9);
    var callP = pCall(pReference("myFunc"), callLocation);
    var namedValueP = pNamedFunc("value", nlist(pItem("p", callP)));
    var moduleP = pModule(list(), list(namedValueP));

    callInjectDefaultArguments(importedS, moduleP);

    assertThat(callP.positionedArgs()).isEqualTo(list(pReference("myFunc:param", callLocation)));
  }

  private static void callInjectDefaultArguments(SScope importedS, PModule pModule) {
    new InitializeScopes().execute(pModule);
    new InjectDefaultArguments().apply(pModule, importedS);
  }
}
