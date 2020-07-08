package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.testing.common.TestingLocation.loc;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class FuncNodeTest {

  @Test
  public void func_with_expression_is_not_native() {
    FuncNode func = new FuncNode(typeNode(), "name", list(), exprNode(), internal());
    assertThat(func.isNative())
        .isFalse();
  }

  @Test
  public void func_without_expression_is_native() {
    FuncNode func = new FuncNode(new TypeNode("type", internal()), "name", list(), null,
        internal());
    assertThat(func.isNative())
        .isTrue();
  }

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
    return new FuncNode(null, name, list(), null, loc(line));
  }

  private static TypeNode typeNode() {
    return mock(TypeNode.class);
  }

  private static ExprNode exprNode() {
    return mock(ExprNode.class);
  }
}
