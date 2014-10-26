package org.smoothbuild.lang.function.def.args;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.lang.base.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.def.args.Arg.namedArg;
import static org.smoothbuild.lang.function.def.args.Arg.namelessArg;
import static org.smoothbuild.lang.function.def.args.Arg.pipedArg;
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

public class ArgTest {
  private final String name = "name";
  private final Expression<?> expression = mock(Expression.class);
  private final CodeLocation codeLocation = codeLocation(1);
  private Arg arg;
  private Arg arg2;
  private Arg arg3;
  private Arg arg4;

  @Test(expected = IllegalArgumentException.class)
  public void negative_index_is_forbidden_in_named_argument() {
    namedArg(-1, name, expression, codeLocation);
  }

  @Test(expected = IllegalArgumentException.class)
  public void zero_index_is_forbidden_in_named_argument() {
    namedArg(0, name, expression, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void nullNameIsForbiddenInNamedArg() {
    namedArg(1, null, expression, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void null_expression_is_forbidden_in_null_argument() {
    namedArg(1, name, null, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void null_source_location_is_forbidden_in_named_argument() {
    namedArg(1, name, expression, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void negative_index_is_forbidden_in_nameless_argument() {
    namelessArg(-1, expression, codeLocation);
  }

  @Test(expected = IllegalArgumentException.class)
  public void zero_index_is_forbidden_in_nameless_argument() {
    namelessArg(0, expression, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void null_expression_is_forbidden_in_nameless_argument() {
    namelessArg(1, null, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void null_source_location_is_forbidden_in_nameless_argument() {
    namelessArg(1, expression, null);
  }

  @Test(expected = NullPointerException.class)
  public void null_expression_is_forbidden_in_piped_argument() {
    pipedArg(null, codeLocation);
  }

  @Test(expected = NullPointerException.class)
  public void null_source_location_is_forbidden_in_piped_argument() {
    pipedArg(expression, null);
  }

  @Test
  public void argument_type_is_equal_to_argument_expression_type() throws Exception {
    given(willReturn(FILE), expression).type();
    when(namedArg(1, name, expression, codeLocation)).type();
    thenReturned(FILE);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void nameless_argument_throws_exception_when_asked_for_name() throws Exception {
    nameless(STRING).name();
  }

  @Test
  public void named_argument_has_name() throws Exception {
    given(arg = namedArg(1, name, expression, codeLocation));
    when(arg).hasName();
    thenReturned(true);
  }

  @Test
  public void nameless_argument_does_not_have_name() throws Exception {
    given(arg = namelessArg(1, expression, codeLocation));
    when(arg).hasName();
    thenReturned(false);
  }

  @Test
  public void piped_argument_does_not_have_name() throws Exception {
    given(arg = pipedArg(expression, codeLocation));
    when(arg).hasName();
    thenReturned(false);
  }

  @Test
  public void sanitized_name_of_named_argument_is_equal_its_name() throws Exception {
    given(arg = named(name));
    when(arg).nameSanitized();
    thenReturned(name);
  }

  @Test
  public void sanitized_name_of_nameless_argument_is_equal_to_nameless() throws Exception {
    given(arg = nameless(STRING));
    when(arg).nameSanitized();
    thenReturned("<nameless>");
  }

  @Test
  public void named_argument_to_string() throws Exception {
    given(willReturn(STRING), expression).type();
    when(namedArg(1, name, expression, codeLocation)).toString();
    thenReturned("String:" + name);
  }

  @Test
  public void nameless_argument_to_strig() throws Exception {
    given(willReturn(STRING), expression).type();
    when(namelessArg(1, expression, codeLocation)).toString();
    thenReturned("String:<nameless>");
  }

  @Test
  public void to_padded_string() throws Exception {
    given(willReturn(STRING), expression).type();
    when(namedArg(1, "myName", expression, codeLocation)).toPaddedString(10, 13, 7);
    thenReturned("String    : myName        #1       " + codeLocation.toString());
  }

  @Test
  public void to_padded_string_with_short_limits() throws Exception {
    given(willReturn(STRING), expression).type();
    when(namedArg(1, "myName", expression, codeLocation(1))).toPaddedString(1, 1, 1);
    thenReturned("String: myName #1 " + codeLocation.toString());
  }

  @Test
  public void filter_named_returns_only_named_arguments() throws Exception {
    given(arg = named("name1"));
    given(arg2 = named("name2"));
    given(arg3 = nameless(STRING));
    given(arg4 = nameless(STRING));
    when(Arg.filterNamed(ImmutableList.of(arg, arg2, arg3, arg4)));
    thenReturned(contains(arg, arg2));
  }

  @Test
  public void filter_nameless_returns_only_nameless_arguments() {
    given(arg = nameless(STRING));
    given(arg2 = nameless(BLOB_ARRAY));
    given(arg3 = nameless(FILE));
    given(arg4 = named("named"));
    when(Arg.filterNameless(ImmutableList.of(arg, arg2, arg3, arg4)));
    thenReturned(ImmutableMultimap.of(STRING, arg, BLOB_ARRAY, arg2, FILE, arg3));
  }

  private static Arg named(String name) {
    return Arg.namedArg(1, name, mock(Expression.class), codeLocation(1));
  }

  private static Arg nameless(Type<?> type) {
    Expression<?> expression = mock(Expression.class);
    given(willReturn(type), expression).type();
    return Arg.namelessArg(1, expression, codeLocation(1));
  }
}
