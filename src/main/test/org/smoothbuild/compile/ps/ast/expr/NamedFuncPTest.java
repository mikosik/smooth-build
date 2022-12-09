package org.smoothbuild.compile.ps.ast.expr;

import static org.smoothbuild.testing.TestContext.loc;
import static org.smoothbuild.util.collect.NList.nlist;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class NamedFuncPTest {
  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(func("equal", 11), func("equal", 11));

    tester.addEqualityGroup(func("a", 1), func("a", 2), func("a", 3));
    tester.addEqualityGroup(func("b", 1), func("b", 2), func("b", 3));
    tester.addEqualityGroup(func("ab", 1), func("ab", 2), func("ab", 3));
    tester.addEqualityGroup(func("ba", 1), func("ba", 2), func("ba", 3));

    tester.testEquals();
  }

  private static NamedFuncP func(String name, int line) {
    return new NamedFuncP(null, name, nlist(), null, null, loc(line));
  }
}
