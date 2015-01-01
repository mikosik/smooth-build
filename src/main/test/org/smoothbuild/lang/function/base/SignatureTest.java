package org.smoothbuild.lang.function.base;

import static java.util.Arrays.asList;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Parameter.optionalParameter;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.util.Empty;

public class SignatureTest {
  private final Type type = STRING;
  private final Name name = name("name");
  private final List<Parameter> parameters = Empty.paramList();
  private Parameter parameter;
  private Parameter parameter2;

  @Test(expected = NullPointerException.class)
  public void nullTypeIsForbidden() {
    new Signature(null, name, parameters);
  }

  @Test(expected = NullPointerException.class)
  public void nullNameIsForbidden() {
    new Signature(type, null, parameters);
  }

  @Test(expected = NullPointerException.class)
  public void nullParamsIsForbidden() {
    new Signature(type, name, null);
  }

  @Test
  public void test_to_string() throws Exception {
    given(parameter = optionalParameter(BLOB, "blob"));
    given(parameter2 = optionalParameter(FILE, "file"));
    when(new Signature(STRING, name, asList(parameter, parameter2))).toString();
    thenReturned(STRING.name() + " " + name.value() + "(" + parameter.type().name() + " "
        + parameter.name() + ", " + parameter2.type().name() + " " + parameter2.name() + ")");
  }
}
