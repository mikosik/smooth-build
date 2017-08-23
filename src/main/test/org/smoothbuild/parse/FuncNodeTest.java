package org.smoothbuild.parse;

import static java.util.Arrays.asList;
import static org.smoothbuild.lang.message.CodeLocation.codeLocation;

import java.util.HashSet;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.parse.ast.FuncNode;

import com.google.common.testing.EqualsTester;

public class FuncNodeTest {

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

  private static FuncNode node(String name, int line) {
    return new FuncNode(new Name(name), asList(), null, new HashSet<>(), codeLocation(line));
  }
}
