package org.smoothbuild.compile.ps.ast.expr;

import static org.smoothbuild.testing.TestContext.location;
import static org.smoothbuild.util.collect.NList.nlist;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class NamedFuncPTest {
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
    return new NamedFuncP(null, name, nlist(), null, null, location(line));
  }
}
