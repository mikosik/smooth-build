package org.smoothbuild.parse.component;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;

public class InferMonoizeTest extends TestContext {
  @Test
  public void poly_func_ref_assigned_to_mono_value_is_monoized() {
    var code = """
          @Native("impl")
          A producer();
          Int() myValue = producer;
          """;
    module(code)
        .loadsWithSuccess()
        .containsTopRefableWithType("myValue", funcTS(intTS()));
  }
}
