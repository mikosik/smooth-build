package org.smoothbuild.lang.function.def;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.lang.base.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.def.Argument.namedArgument;
import static org.smoothbuild.lang.function.def.Argument.namelessArgument;
import static org.smoothbuild.lang.function.def.Argument.pipedArgument;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.message.base.CodeLocation;

import com.google.common.collect.ImmutableMultimap;

public class ArgumentTest {
  private final String name = "name";
  private final Expression expression = mock(Expression.class);
  private final CodeLocation codeLocation = codeLocation(1);
  private Argument argument;
  private Argument argument2;
  private Argument argument3;
  private Argument argument4;

  @Test(expected = IllegalArgumentException.class)
  public void negative_index_is_forbidden_in_named_argument() {
    namedArgument(-1, name, expression, codeLocation);
  }

  @Test(expected = IllegalArgumentException.class)
  public void zero_index_is_forbidden_in_named_argument() {
    namedArgument(0, name, expression, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void nullNameIsForbiddenInNamedArg() {
    namedArgument(1, null, expression, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void null_expression_is_forbidden_in_null_argument() {
    namedArgument(1, name, null, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void null_source_location_is_forbidden_in_named_argument() {
    namedArgument(1, name, expression, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void negative_index_is_forbidden_in_nameless_argument() {
    namelessArgument(-1, expression, codeLocation);
  }

  @Test(expected = IllegalArgumentException.class)
  public void zero_index_is_forbidden_in_nameless_argument() {
    namelessArgument(0, expression, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void null_expression_is_forbidden_in_nameless_argument() {
    namelessArgument(1, null, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void null_source_location_is_forbidden_in_nameless_argument() {
    namelessArgument(1, expression, null);
  }

  @Test(expected = NullPointerException.class)
  public void null_expression_is_forbidden_in_piped_argument() {
    pipedArgument(null, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void null_source_location_is_forbidden_in_piped_argument() {
    pipedArgument(expression, null);
  }

  @Test
  public void argument_type_is_equal_to_argument_expression_type() throws Exception {
    given(willReturn(FILE), expression).type();
    when(namedArgument(1, name, expression, codeLocation)).type();
    thenReturned(FILE);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void nameless_argument_throws_exception_when_asked_for_name() throws Exception {
    nameless(STRING).name();
  }

  @Test
  public void named_argument_has_name() throws Exception {
    given(argument = namedArgument(1, name, expression, codeLocation));
    when(argument).hasName();
    thenReturned(true);
  }

  @Test
  public void nameless_argument_does_not_have_name() throws Exception {
    given(argument = namelessArgument(1, expression, codeLocation));
    when(argument).hasName();
    thenReturned(false);
  }

  @Test
  public void piped_argument_does_not_have_name() throws Exception {
    given(argument = pipedArgument(expression, codeLocation));
    when(argument).hasName();
    thenReturned(false);
  }

  @Test
  public void sanitized_name_of_named_argument_is_equal_its_name() throws Exception {
    given(argument = named(name));
    when(argument).nameSanitized();
    thenReturned(name);
  }

  @Test
  public void sanitized_name_of_nameless_argument_is_equal_to_nameless() throws Exception {
    given(argument = nameless(STRING));
    when(argument).nameSanitized();
    thenReturned("<nameless>");
  }

  @Test
  public void named_argument_to_string() throws Exception {
    given(willReturn(STRING), expression).type();
    when(namedArgument(1, name, expression, codeLocation)).toString();
    thenReturned("String:" + name);
  }

  @Test
  public void nameless_argument_to_strig() throws Exception {
    given(willReturn(STRING), expression).type();
    when(namelessArgument(1, expression, codeLocation)).toString();
    thenReturned("String:<nameless>");
  }

  @Test
  public void to_padded_string() throws Exception {
    given(willReturn(STRING), expression).type();
    when(namedArgument(1, "myName", expression, codeLocation)).toPaddedString(10, 13, 7);
    thenReturned("String    : myName        #1       " + codeLocation.toString());
  }

  @Test
  public void to_padded_string_with_short_limits() throws Exception {
    given(willReturn(STRING), expression).type();
    when(namedArgument(1, "myName", expression, codeLocation(1))).toPaddedString(1, 1, 1);
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
    return Argument.namedArgument(1, name, mock(Expression.class), codeLocation(1));
  }

  private static Argument nameless(Type type) {
    Expression expression = mock(Expression.class);
    given(willReturn(type), expression).type();
    return Argument.namelessArgument(1, expression, codeLocation(1));
  }
}
