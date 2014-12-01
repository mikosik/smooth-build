package org.smoothbuild.lang.function.base;

import static org.smoothbuild.lang.base.Types.BLOB;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Parameter.optionalParameter;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.base.Type;

import com.google.common.collect.ImmutableList;

public class SignatureTest {
  private final Type<?> type = STRING;
  private final Name name = name("name");
  private final ImmutableList<Parameter> parameters = ImmutableList.of();
  private Parameter parameter;
  private Parameter parameter2;

  @Test(expected = NullPointerException.class)
  public void nullTypeIsForbidden() {
    new Signature<>(null, name, parameters);
  }

  @Test(expected = NullPointerException.class)
  public void nullNameIsForbidden() {
    new Signature<>(type, null, parameters);
  }

  @Test(expected = NullPointerException.class)
  public void nullParamsIsForbidden() {
    new Signature<>(type, name, null);
  }

  @Test
  public void test_to_string() throws Exception {
    given(parameter = optionalParameter(BLOB, "blob"));
    given(parameter2 = optionalParameter(FILE, "file"));
    when(new Signature<>(STRING, name, ImmutableList.of(parameter, parameter2))).toString();
    thenReturned(STRING.name() + " " + name.value() + "(" + parameter.type().name() + " "
        + parameter.name() + ", " + parameter2.type().name() + " " + parameter2.name() + ")");
  }
}
