package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class EvaluableRefSTest extends TestContext {
  @Test
  public void to_string() {
    var polyRefS = new EvaluableRefS(idFuncS(), loc(7));
    assertThat(polyRefS.toString())
        .isEqualTo("""
            EvaluableRefS(
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
