package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.idSFunc;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sCall;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sInstantiate;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sInt;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;

import org.junit.jupiter.api.Test;

public class SCallTest {
  @Test
  public void to_string() {
    var funcS = sInstantiate(4, list(sIntType()), idSFunc());
    var callS = sCall(3, funcS, sInt(3, 7));
    assertThat(callS.toString())
        .isEqualTo(
            """
            SCall(
              callee = SInstantiate(
                typeArgs = <Int>
                polymorphicS = SReference(
                  schema = <A>(A)->A
                  referencedName = myId
                  location = {prj}/build.smooth:4
                )
                evaluationType = (Int)->Int
                location = {prj}/build.smooth:4
              )
              args = SCombine(
                evaluationType = (Int)
                elems = [
                  SInt(Int, 7, {prj}/build.smooth:3)
                ]
                location = {prj}/build.smooth:3
              )
              location = {prj}/build.smooth:3
            )""");
  }
}
