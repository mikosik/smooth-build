package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class CallSTest extends TestContext {
  @Test
  public void to_string() {
    var defFuncS = monoizeS(4, varMap(varA(), intTS()), idFuncS());
    var callS = callS(3, defFuncS, intS(3, 7));
    assertThat(callS.toString())
        .isEqualTo("""
            CallS(
              callee = MonoizeS(
                varMap = {A=Int}
                polyExprS = PolyRefS(
                  namedEvaluable = DefFuncS(
                    schema = <A>(A)->A
                    params = [
                      A a
                    ]
                    loc = myBuild.smooth:1
                    body = ParamRefS(A, a, myBuild.smooth:1)
                  )
                  loc = myBuild.smooth:4
                )
                evalT = (Int)->Int
                loc = myBuild.smooth:4
              )
              args = [
                IntS(Int, 7, myBuild.smooth:3)
              ]
              loc = myBuild.smooth:3
            )""");
  }
}
