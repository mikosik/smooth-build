package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.callS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.idFuncS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.instantiateS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intTS;

import org.junit.jupiter.api.Test;

public class CallSTest {
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
                  referencedName = myId
                  location = {prj}/build.smooth:4
                )
                evaluationType = (Int)->Int
                location = {prj}/build.smooth:4
              )
              args = CombineS(
                evaluationType = (Int)
                elems = [
                  IntS(Int, 7, {prj}/build.smooth:3)
                ]
                location = {prj}/build.smooth:3
              )
              location = {prj}/build.smooth:3
            )""");
  }
}
