package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.idSFunc;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sCall;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sInt;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestingSExpression;

public class SCallTest {
  @Test
  public void to_string() {
    var funcS = TestingSExpression.sInstantiate(4, list(sIntType()), idSFunc());
    var callS = sCall(3, funcS, sInt(3, 7));
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
