package org.smoothbuild.lang.function.base;

import static java.util.Arrays.asList;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeSystem;
import org.smoothbuild.util.Dag;

public class SignatureTest {
  private final TypeSystem typeSystem = new TypeSystem();
  private final Type string = typeSystem.string();
  private final Type blob = typeSystem.blob();
  private final Name name = new Name("name");
  private final List<Parameter> parameters = asList();
  private Parameter parameter;
  private Parameter parameter2;

  @Test(expected = NullPointerException.class)
  public void null_type_is_forbidden() {
    new Signature(null, name, parameters);
  }

  @Test(expected = NullPointerException.class)
  public void null_name_is_forbidden() {
    new Signature(string, null, parameters);
  }

  @Test(expected = NullPointerException.class)
  public void null_param_is_forbidden() {
    new Signature(string, name, null);
  }

  @Test
  public void test_to_string() throws Exception {
    given(parameter = new Parameter(blob, new Name("blob"), mock(Dag.class)));
    given(parameter2 = new Parameter(string, new Name("string"), mock(Dag.class)));
    when(new Signature(string, name, asList(parameter, parameter2))).toString();
    thenReturned(string.name() + " " + name + "(" + parameter.type().name() + " "
        + parameter.name() + ", " + parameter2.type().name() + " " + parameter2.name() + ")");
  }
}
