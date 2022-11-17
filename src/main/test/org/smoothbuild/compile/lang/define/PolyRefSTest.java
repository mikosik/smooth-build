package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class PolyRefSTest extends TestContext {
  @Test
  public void to_string() {
    var polyRefS = new PolyRefS(idFuncS(), loc(7));
    assertThat(polyRefS.toString())
        .isEqualTo("""
            PolyRefS(
              namedEvaluable = DefFuncS(
                schema = <A>(A)->A
                params = [
                  A a
                ]
                loc = myBuild.smooth:1
                body = ParamRefS(A, a, myBuild.smooth:1)
              )
              loc = myBuild.smooth:7
            )""");
  }
}
