package org.smoothbuild.parse.ast;

import static org.smoothbuild.lang.message.Location.location;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.nio.file.Paths;

import org.junit.Test;
import org.smoothbuild.lang.message.Location;

import com.google.common.testing.EqualsTester;

public class FuncNodeTest {
  private final Location location = location(Paths.get("script.smooth"), 1);
  private FuncNode func;

  @Test
  public void func_with_expression_is_not_native() throws Exception {
    given(func = new FuncNode(mock(TypeNode.class), "name", list(), mock(ExprNode.class),
        location));
    when(() -> func.isNative());
    thenReturned(false);
  }

  @Test
  public void func_without_expression_is_native() throws Exception {
    given(func = new FuncNode(new TypeNode("type", location), "name", list(), null, location));
    when(() -> func.isNative());
    thenReturned(true);
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
    return new FuncNode(null, name, list(), null, location(Paths.get("script.smooth"), line));
  }
}
