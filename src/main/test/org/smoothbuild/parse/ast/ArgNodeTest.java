package org.smoothbuild.parse.ast;

import static org.smoothbuild.lang.message.Location.location;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.nio.file.Paths;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeSystem;

public class ArgNodeTest {
  private static final TypeSystem TYPE_SYSTEM = new TypeSystem();
  private static final Type STRING = TYPE_SYSTEM.string();

  private ArgNode arg;
  private final Location location = location(Paths.get("path"), 1);
  private final Name name = new Name("arg-name");

  @Test
  public void named_arg_has_name() throws Exception {
    given(arg = new ArgNode(0, name, expr(STRING), location));
    when(() -> arg.hasName());
    thenReturned(true);
  }

  @Test
  public void nameless_arg_does_not_have_name() throws Exception {
    given(arg = new ArgNode(0, null, expr(STRING), location));
    when(() -> arg.hasName());
    thenReturned(false);
  }

  @Test
  public void nameless_arg_throws_exception_when_asked_for_name() throws Exception {
    given(arg = new ArgNode(0, null, expr(STRING), location));
    when(() -> arg.name());
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void sanitized_name_of_named_argument_is_equal_its_name() throws Exception {
    given(arg = new ArgNode(1, name, null, location));
    when(arg).nameSanitized();
    thenReturned(name.toString());
  }

  @Test
  public void sanitized_name_of_nameless_argument_is_equal_to_nameless() throws Exception {
    given(arg = new ArgNode(1, null, null, location));
    when(arg).nameSanitized();
    thenReturned("<nameless>");
  }

  @Test
  public void type_and_name_of_named_argument() throws Exception {
    given(arg = new ArgNode(1, name, expr(STRING), location));
    given(arg).set(Type.class, STRING);
    when(arg).typeAndName();
    thenReturned("String:" + name);
  }

  @Test
  public void nameless_argument_to_string() throws Exception {
    given(arg = new ArgNode(1, null, expr(STRING), location));
    given(arg).set(Type.class, STRING);
    when(arg).typeAndName();
    thenReturned("String:<nameless>");
  }

  @Test
  public void to_padded_string() throws Exception {
    given(arg = new ArgNode(1, new Name("myName"), expr(STRING), location));
    given(arg).set(Type.class, STRING);
    when(arg).toPaddedString(10, 13, 7);
    thenReturned("String    : myName        #1       [" + location.toString() + "]");
  }

  @Test
  public void to_padded_string_with_short_limits() throws Exception {
    given(arg = new ArgNode(1, new Name("myName"), expr(STRING), location));
    given(arg).set(Type.class, STRING);
    when(arg).toPaddedString(1, 1, 1);
    thenReturned("String: myName #1 [" + location.toString() + "]");
  }

  private static ExprNode expr(Type type) {
    ExprNode expr = mock(ExprNode.class);
    given(willReturn(type), expr).get(Type.class);
    return expr;
  }
}
