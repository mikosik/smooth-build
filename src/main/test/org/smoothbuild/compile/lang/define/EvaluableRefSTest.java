package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class EvaluableRefSTest extends TestContext {
  @Test
  public void to_string() {
    var evaluableRef = new EvaluableRefS(idFuncS(), loc(7));
    assertThat(evaluableRef.toString())
        .isEqualTo("""
            EvaluableRefS(
              namedEvaluable = DefFuncS(
                name = myId
                schema = <A>(A)->A
                params = [
                  ItemS(
                    type = A
                    name = a
                    defaultValue = Optional.empty
                    loc = myBuild.smooth:1
                  )
                ]
                loc = myBuild.smooth:1
                body = ParamRefS(A, a, myBuild.smooth:1)
              )
              loc = myBuild.smooth:7
            )""");
  }
}
