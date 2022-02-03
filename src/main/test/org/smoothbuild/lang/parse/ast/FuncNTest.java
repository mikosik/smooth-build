package org.smoothbuild.lang.parse.ast;

import static org.smoothbuild.testing.TestingContext.loc;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class FuncNTest {
  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(node("equal", 11), node("equal", 11));

    tester.addEqualityGroup(node("a", 1), node("a", 2), node("a", 3));
    tester.addEqualityGroup(node("b", 1), node("b", 2), node("b", 3));
    tester.addEqualityGroup(node("ab", 1), node("ab", 2), node("ab", 3));
    tester.addEqualityGroup(node("ba", 1), node("ba", 2), node("ba", 3));

    tester.testEquals();
  }

  private static FuncN node(String name, int line) {
    return new FuncN(null, name, list(), null, null, loc(line));
  }
}
