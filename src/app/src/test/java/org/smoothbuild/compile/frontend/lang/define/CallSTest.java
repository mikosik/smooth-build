package org.smoothbuild.compile.frontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class CallSTest extends TestContext {
  @Test
  public void to_string() {
    var funcS = instantiateS(4, list(intTS()), idFuncS());
    var callS = callS(3, funcS, intS(3, 7));
    assertThat(callS.toString())
        .isEqualTo("""
            CallS(
              callee = InstantiateS(
                typeArgs = <Int>
                polymorphicS = ReferenceS(
                  schema = <A>(A)->A
                  name = myId
                  location = build.smooth:4
                )
                evaluationT = (Int)->Int
                location = build.smooth:4
              )
              args = CombineS(
                evaluationT = (Int)
                elems = [
                  IntS(Int, 7, build.smooth:3)
                ]
                location = build.smooth:3
              )
              location = build.smooth:3
            )""");
  }
}
