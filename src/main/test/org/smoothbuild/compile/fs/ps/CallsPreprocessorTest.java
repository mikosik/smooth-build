package org.smoothbuild.compile.fs.ps;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compile.fs.ps.CallsPreprocessor.preprocessCalls;
import static org.smoothbuild.util.bindings.Bindings.immutableBindings;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.fs.lang.define.DefinitionsS;
import org.smoothbuild.compile.fs.ps.ast.expr.ModuleP;
import org.smoothbuild.testing.TestContext;

public class CallsPreprocessorTest extends TestContext  {
  @Test
  public void missing_call_argument_is_filled_with_reference_to_default_argument() {
    var myFuncS = funcS("myFunc", nlist(itemS("param", intS(7))), paramRefS(intTS(), "param"));
    var importedS = new DefinitionsS(immutableBindings(), bindings(myFuncS));
    var callLocation = location(9);
    var callP = callP(refP("myFunc"), callLocation);
    var namedValueP = namedValueP("value", callP);
    var moduleP = new ModuleP(list(), list(namedValueP));

    ScopesInitializer.initializeScopes(moduleP);
    preprocessCalls(moduleP, importedS);

    assertThat(callP.positionedArgs())
        .isEqualTo(list(refP("myFunc:param", callLocation)));
  }

  @Test
  public void missing_call_argument_in_call_within_default_body_is_filled_with_reference_to_default_argument() {
    var myFuncS = funcS("myFunc", nlist(itemS("param", intS(7))), paramRefS(intTS(), "param"));
    var importedS = new DefinitionsS(immutableBindings(), bindings(myFuncS));
    var callLocation = location(9);
    var callP = callP(refP("myFunc"), callLocation);
    var namedValueP = namedFuncP("value", nlist(itemP("p", callP)));
    var moduleP = new ModuleP(list(), list(namedValueP));

    ScopesInitializer.initializeScopes(moduleP);
    preprocessCalls(moduleP, importedS);

    assertThat(callP.positionedArgs())
        .isEqualTo(list(refP("myFunc:param", callLocation)));
  }
}
