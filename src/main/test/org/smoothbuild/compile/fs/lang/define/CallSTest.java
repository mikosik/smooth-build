package org.smoothbuild.compile.fs.lang.define;

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
                monoizableS = RefS(
                  schema = <A>(A)->A
                  name = myId
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
