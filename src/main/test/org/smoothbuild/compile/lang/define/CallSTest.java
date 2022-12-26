package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class CallSTest extends TestContext {
  @Test
  public void to_string() {
    var funcS = monoizeS(4, varMap(varA(), intTS()), idFuncS());
    var callS = callS(3, funcS, intS(3, 7));
    assertThat(callS.toString())
        .isEqualTo("""
            CallS(
              callee = MonoizeS(
                varMap = {A=Int}
                monoizableS = EvaluableRefS(
                  namedEvaluable = NamedExprFuncS(
                    name = myId
                    schema = <A>(A)->A
                    params = [
                      ItemS(
                        type = A
                        name = a
                        defaultValue = Optional.empty
                        location = myBuild.smooth:1
                      )
                    ]
                    location = myBuild.smooth:1
                    body = ParamRefS(
                      evalT = A
                      paramName = a
                      location = myBuild.smooth:1
                    )
                  )
                  location = myBuild.smooth:4
                )
                evalT = (Int)->Int
                location = myBuild.smooth:4
              )
              args = [
                IntS(Int, 7, myBuild.smooth:3)
              ]
              location = myBuild.smooth:3
            )""");
  }
}
