package org.smoothbuild.compilerfrontend.compile;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.compilerfrontend.lang.bindings.Bindings.immutableBindings;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class InjectDefaultArgumentsTest extends FrontendCompilerTestContext {
  @Test
  void missing_call_argument_is_filled_with_reference_to_default_argument() {
    var myFuncS = sFunc(
        "myFunc",
        nlist(sItem(sIntType(), "param", "myFunc:param")),
        sParamRef(sIntType(), "param"));
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
    var sImported = new SScope(immutableBindings(), bindings());
    var callLocation = location(9);
    var pCall = pCall(pReference("myFunc"), callLocation);
    var pValue = pNamedValue("result", pCall);
    var pDefaultValue = pNamedValue("myFunc:param", pInt());
    var pItem = pItem("p", pInt());
    pItem.setDefaultValueId(some(fqn("myFunc:param")));
    var pNamedFunc = pNamedFunc("myFunc", nlist(pItem));
    var pModule = pModule(list(), list(pDefaultValue, pNamedFunc, pValue));

    callInjectDefaultArguments(sImported, pModule);

    assertThat(pCall.positionedArgs()).isEqualTo(list(pReference("myFunc:param", callLocation)));
  }

  private static void callInjectDefaultArguments(SScope sImported, PModule pModule) {
    new GenerateScopes().execute(pModule);
    new InjectDefaultArguments().execute(pModule, sImported);
  }
}
