package org.smoothbuild.lang.function.base;

import static java.util.Arrays.asList;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Parameter.parameter;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.type.Type;

public class SignatureTest {
  private final Type type = STRING;
  private final Name name = name("name");
  private final List<Parameter> parameters = asList();
  private Parameter parameter;
  private Parameter parameter2;

  @Test(expected = NullPointerException.class)
  public void null_type_is_forbidden() {
    new Signature(null, name, parameters);
  }

  @Test(expected = NullPointerException.class)
  public void null_name_is_forbidden() {
    new Signature(type, null, parameters);
  }

  @Test(expected = NullPointerException.class)
  public void null_param_is_forbidden() {
    new Signature(type, name, null);
  }

  @Test
  public void test_to_string() throws Exception {
    given(parameter = parameter(BLOB, "blob", mock(Expression.class)));
    given(parameter2 = parameter(FILE, "file", mock(Expression.class)));
    when(new Signature(STRING, name, asList(parameter, parameter2))).toString();
    thenReturned(STRING.name() + " " + name.value() + "(" + parameter.type().name() + " "
        + parameter.name() + ", " + parameter2.type().name() + " " + parameter2.name() + ")");
  }
}
