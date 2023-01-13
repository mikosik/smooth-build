package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class CallSTest extends TestContext {
  @Test
  public void to_string() {
    var funcS = monoizeS(4, list(intTS()), idFuncS());
    var callS = callS(3, funcS, intS(3, 7));
    assertThat(callS.toString())
        .isEqualTo("""
            CallS(
              callee = MonoizeS(
                typeArgs = <Int>
                monoizableS = ReferenceS(
                  schema = <A>(A)->A
                  name = myId
                  location = myBuild.smooth:4
                )
                evalT = (Int)->Int
                location = myBuild.smooth:4
              )
              args = CombineS(
                evalT = (Int)
                elems = [
                  IntS(Int, 7, myBuild.smooth:3)
                ]
                location = myBuild.smooth:3
              )
              location = myBuild.smooth:3
            )""");
  }
}
