package org.smoothbuild.compile.frontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestExpressionS;

public class CallSTest extends TestExpressionS {
  @Test
  public void to_string() {
    var funcS = instantiateS(4, list(intTS()), idFuncS());
    var callS = callS(3, funcS, intS(3, 7));
    assertThat(callS.toString())
        .isEqualTo(
            """
            CallS(
              callee = InstantiateS(
                typeArgs = <Int>
                polymorphicS = ReferenceS(
                  schema = <A>(A)->A
                  name = myId
                  location = {prj}/build.smooth:4
                )
                evaluationT = (Int)->Int
                location = {prj}/build.smooth:4
              )
              args = CombineS(
                evaluationT = (Int)
                elems = [
                  IntS(Int, 7, {prj}/build.smooth:3)
                ]
                location = {prj}/build.smooth:3
              )
              location = {prj}/build.smooth:3
            )""");
  }
}
