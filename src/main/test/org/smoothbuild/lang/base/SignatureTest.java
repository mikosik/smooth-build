package org.smoothbuild.lang.base;

import static org.smoothbuild.lang.type.TestingTypes.blob;
import static org.smoothbuild.lang.type.TestingTypes.string;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.expr.Expression;

public class SignatureTest {
  private Parameter parameter;
  private Parameter parameter2;

  @Test
  public void null_type_is_forbidden() {
    when(() -> new Signature(null, "name", list()));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    when(() -> new Signature(string, null, list()));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_param_is_forbidden() {
    when(() -> new Signature(string, "name", null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void parameter_types() throws Exception {
    given(parameter = new Parameter(blob, "blob", mock(Expression.class)));
    given(parameter2 = new Parameter(string, "string", mock(Expression.class)));
    when(() -> new Signature(string, "name", list(parameter, parameter2)).parameterTypes());
    thenReturned(list(blob, string));
  }

  @Test
  public void to_string() throws Exception {
    given(parameter = new Parameter(blob, "blob", mock(Expression.class)));
    given(parameter2 = new Parameter(string, "string", mock(Expression.class)));
    when(() -> new Signature(string, "name", list(parameter, parameter2)).toString());
    thenReturned(string.name() + " " + "name" + "(" + parameter.type().name() + " "
        + parameter.name() + ", " + parameter2.type().name() + " " + parameter2.name() + ")");
  }
}
