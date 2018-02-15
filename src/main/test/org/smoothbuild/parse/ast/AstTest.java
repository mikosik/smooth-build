package org.smoothbuild.parse.ast;

import static java.util.Arrays.asList;
import static org.smoothbuild.lang.message.Location.location;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Name;

public class AstTest {
  private Ast ast;
  private FuncNode func;

  @Test
  public void contains_function_passed_to_constructor() throws Exception {
    given(func = function("name"));
    given(ast = new Ast(list(func)));
    when(() -> ast.containsFunc(new Name("name")));
    thenReturned(true);
  }

  @Test
  public void does_not_contain_function_not_passed_to_constructor() throws Exception {
    given(ast = new Ast(list()));
    when(() -> ast.containsFunc(new Name("name")));
    thenReturned(false);
  }

  @Test
  public void function_passed_to_constructor_can_be_retrieved() throws Exception {
    given(func = function("name"));
    given(ast = new Ast(list(func)));
    when(() -> ast.func(new Name("name")));
    thenReturned(func);
  }

  @Test
  public void retrieving_function_not_contained_causes_exception() throws Exception {
    given(ast = new Ast(list()));
    when(() -> ast.func(new Name("name")));
    thenThrown(IllegalStateException.class);
  }

  private static FuncNode function(String name) {
    return new FuncNode(mock(TypeNode.class), new Name(name), asList(),
        mock(ExprNode.class), location(null, 1));
  }
}
