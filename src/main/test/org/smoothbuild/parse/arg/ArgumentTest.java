package org.smoothbuild.parse.arg;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.lang.message.CodeLocation.codeLocation;
import static org.smoothbuild.lang.type.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.ExprNode;

import com.google.common.collect.ImmutableMultimap;

public class ArgumentTest {
  private final String name = "name";
  private final ExprNode expr = mock(ExprNode.class);
  private final CodeLocation codeLocation = codeLocation(1);
  private Argument argument;
  private Argument argument2;
  private Argument argument3;
  private Argument argument4;

  @Test
  public void argument_type_is_equal_to_argument_expression_type() throws Exception {
    given(argument = new Argument(new ArgNode(0, name, expr(FILE), codeLocation)));
    when(() -> argument.type());
    thenReturned(FILE);
  }

  public void nameless_argument_throws_exception_when_asked_for_name() throws Exception {
    given(argument = new Argument(new ArgNode(0, null, expr(FILE), codeLocation)));
    when(() -> argument.name());
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void named_argument_has_name() throws Exception {
    given(argument = new Argument(new ArgNode(1, name, expr, codeLocation)));
    when(argument).hasName();
    thenReturned(true);
  }

  @Test
  public void nameless_argument_does_not_have_name() throws Exception {
    given(argument = new Argument(new ArgNode(1, null, expr, codeLocation)));
    when(argument).hasName();
    thenReturned(false);
  }

  @Test
  public void sanitized_name_of_named_argument_is_equal_its_name() throws Exception {
    given(argument = new Argument(new ArgNode(1, name, expr, codeLocation)));
    when(argument).nameSanitized();
    thenReturned(name);
  }

  @Test
  public void sanitized_name_of_nameless_argument_is_equal_to_nameless() throws Exception {
    given(argument = new Argument(new ArgNode(1, null, expr, codeLocation)));
    when(argument).nameSanitized();
    thenReturned("<nameless>");
  }

  @Test
  public void named_argument_to_string() throws Exception {
    given(argument = new Argument(new ArgNode(1, name, expr(STRING), codeLocation)));
    when(argument).toString();
    thenReturned("String:" + name);
  }

  @Test
  public void nameless_argument_to_string() throws Exception {
    given(argument = new Argument(new ArgNode(1, null, expr(STRING), codeLocation)));
    when(argument).toString();
    thenReturned("String:<nameless>");
  }

  @Test
  public void to_padded_string() throws Exception {
    given(argument = new Argument(new ArgNode(1, "myName", expr(STRING), codeLocation)));
    when(argument).toPaddedString(10, 13, 7);
    thenReturned("String    : myName        #1       " + codeLocation.toString());
  }

  @Test
  public void to_padded_string_with_short_limits() throws Exception {
    given(argument = new Argument(new ArgNode(1, "myName", expr(STRING), codeLocation)));
    when(argument).toPaddedString(1, 1, 1);
    thenReturned("String: myName #1 " + codeLocation.toString());
  }

  @Test
  public void filter_named_returns_only_named_arguments() throws Exception {
    given(argument = named("name1"));
    given(argument2 = named("name2"));
    given(argument3 = nameless(STRING));
    given(argument4 = nameless(STRING));
    when(Argument.filterNamed(asList(argument, argument2, argument3, argument4)));
    thenReturned(contains(argument, argument2));
  }

  @Test
  public void filter_nameless_returns_only_nameless_arguments() {
    given(argument = nameless(STRING));
    given(argument2 = nameless(BLOB_ARRAY));
    given(argument3 = nameless(FILE));
    given(argument4 = named("named"));
    when(Argument.filterNameless(asList(argument, argument2, argument3, argument4)));
    thenReturned(ImmutableMultimap.of(STRING, argument, BLOB_ARRAY, argument2, FILE, argument3));
  }

  private static Argument named(String name) {
    return argument(1, name, mock(ExprNode.class), codeLocation(1));
  }

  private static Argument nameless(Type type) {
    return typedArgument(null, type);
  }

  private static Argument typedArgument(String name, Type type) {
    return argument(1, name, expr(type), codeLocation(1));
  }

  private static ExprNode expr(Type type) {
    ExprNode expr = mock(ExprNode.class);
    given(willReturn(type), expr).get(Type.class);
    return expr;
  }

  private static Argument argument(int position, String name, ExprNode expr,
      CodeLocation codeLocation) {
    return new Argument(new ArgNode(position, name, expr, codeLocation));
  }
}
