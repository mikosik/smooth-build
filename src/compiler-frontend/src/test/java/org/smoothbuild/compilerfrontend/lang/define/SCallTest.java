package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SCallTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    var funcReference = sReference(sFuncSchema(varA(), varA()), fqn("my:company:myFunc"));
    var callable = sInstantiate(list(sIntType()), funcReference);
    var callS = sCall(callable, sInt(7));
    assertThat(callS.toSourceCode()).isEqualTo("my:company:myFunc<Int>(7)");
  }

  @Test
  void to_string() {
    var funcS = sInstantiate(4, list(sIntType()), idSFunc());
    var callS = sCall(3, funcS, sInt(3, 7));
    assertThat(callS.toString())
        .isEqualTo(
            """
            SCall(
              callee = SInstantiate(
                typeArgs = <Int>
                polymorphic = SReference(
                  schema = <A>(A)->A
                  referencedName = myId
                  location = {t-project}/module.smooth:4
                )
                evaluationType = (Int)->Int
                location = {t-project}/module.smooth:4
              )
              args = SCombine(
                evaluationType = {Int}
                elements = [
                  SInt(
                    type = Int
                    bigInteger = 7
                    location = {t-project}/module.smooth:3
                  )
                ]
                location = {t-project}/module.smooth:3
              )
              location = {t-project}/module.smooth:3
            )""");
  }
}
