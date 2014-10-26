package org.smoothbuild.lang.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.Types.BLOB;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Parameter.parameter;
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
  public void paramsAreSortedAccordingToName() throws Exception {
    String name1 = "aaa";
    String name2 = "bbb";
    String name3 = "ccc";
    String name4 = "ddd";
    String name5 = "eee";
    String name6 = "fff";
    Parameter parameter1 = parameter(STRING, name1, false);
    Parameter parameter2 = parameter(STRING, name2, false);
    Parameter parameter3 = parameter(STRING, name3, false);
    Parameter parameter4 = parameter(STRING, name4, false);
    Parameter parameter5 = parameter(STRING, name5, false);
    Parameter parameter6 = parameter(STRING, name6, false);

    ImmutableList<Parameter> parameters = ImmutableList.of(parameter4, parameter6, parameter1,
        parameter3, parameter5, parameter2);
    Signature<?> signature = new Signature<>(type, name, parameters);
    assertThat(signature.parameters()).containsExactly(parameter1, parameter2, parameter3, parameter4,
        parameter5, parameter6);
  }

  @Test
  public void test_to_string() throws Exception {
    given(parameter = parameter(BLOB, "blob", false));
    given(parameter2 = parameter(FILE, "file", false));
    when(new Signature<>(STRING, name, ImmutableList.of(parameter, parameter2))).toString();
    thenReturned(
        STRING.name() + " " + name.value() + "(" + parameter.type().name() + " " + parameter.name() + ", " + parameter2.type().name() + " " + parameter2.name() + ")");
  }
}
