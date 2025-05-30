package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestContext;

public class SLambdaTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    var params = nlist(sItem(varA(), "p1"), sItem(sIntType(), "p2"));
    var resultType = sStringType();
    var sLambda = new SLambda(resultType, fqn("module:myFunc"), params, sInt(17), location(1));
    assertThat(sLambda.toSourceCode()).isEqualTo("(A p1, Int p2) -> 17");
  }
}
