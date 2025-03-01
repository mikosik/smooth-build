package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SLambdaTest extends FrontendCompilerTestContext {
  @Test
  void to_source_code() {
    var params = nlist(sItem(varA(), "p1"), sItem(sIntType(), "p2"));
    var schema = sFuncType(sStringType());
    var sLambda = new SLambda(schema, fqn("module:myFunc"), params, sInt(17), location(1));
    assertThat(sLambda.toSourceCode()).isEqualTo("(A p1, Int p2) -> 17");
  }
}
