package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;

public class AstTest {
  @Test
  public void contains_function_passed_to_constructor() {
    FuncNode func = function("name");
    Ast ast = new Ast(list(), list(func));
    assertThat(ast.containsFunc("name"))
        .isTrue();
  }

  @Test
  public void does_not_contain_function_not_passed_to_constructor() {
    Ast ast = new Ast(list(), list());
    assertThat(ast.containsFunc("name"))
        .isFalse();
  }

  @Test
  public void function_passed_to_constructor_can_be_retrieved() {
    FuncNode func = function("name");
    Ast ast = new Ast(list(), list(func));
    assertThat(ast.func("name"))
        .isEqualTo(func);
  }

  @Test
  public void retrieving_function_not_contained_causes_exception() {
    Ast ast = new Ast(list(), list());
    assertCall(() -> ast.func("name"))
        .throwsException(IllegalStateException.class);
  }

  private static FuncNode function(String name) {
    return new FuncNode(mock(TypeNode.class), name, list(),
        mock(ExprNode.class), location(null, 1));
  }
}
