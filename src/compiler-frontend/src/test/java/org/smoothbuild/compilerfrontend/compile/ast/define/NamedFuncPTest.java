package org.smoothbuild.compilerfrontend.compile.ast.define;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestingExpressionS;

public class NamedFuncPTest extends TestingExpressionS {
  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(func("equal", 11), func("equal", 11));

    tester.addEqualityGroup(func("a", 1), func("a", 1));
    tester.addEqualityGroup(func("a", 2), func("a", 2));
    tester.addEqualityGroup(func("b", 1), func("b", 1));

    tester.testEquals();
  }

  private static NamedFuncP func(String name, int line) {
    return namedFuncP(name, line);
  }
}
