package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class PolyRefSTest extends TestContext {
  @Test
  public void to_string() {
    var polyRefS = polyRefS(7, varMap(varA(), intTS()), idFuncS());
    assertThat(polyRefS.toString())
        .isEqualTo("""
            PolyRefS(
              varMap = {A=Int}
              polyEvaluable = PolyFuncS(
                schema = <A>A(A)
                mono = DefFuncS(
                  type = A(A)
                  params = [
                    A a
                  ]
                  loc = myBuild.smooth:1
                  body = ParamRefS(A, a, myBuild.smooth:1)
                )
              )
              evalT = Int(Int)
              loc = myBuild.smooth:7
            )""");
  }
}
