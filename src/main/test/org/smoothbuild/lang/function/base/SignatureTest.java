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
  private static final TypeSystem TYPE_SYSTEM = new TypeSystem();
  private static final Type STRING = TYPE_SYSTEM.string();
  private static final Type BLOB = TYPE_SYSTEM.blob();
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
    new Signature(STRING, null, parameters);
  }

  @Test(expected = NullPointerException.class)
  public void null_param_is_forbidden() {
    new Signature(STRING, name, null);
  }

  @Test
  public void test_to_string() throws Exception {
    given(parameter = new Parameter(BLOB, new Name("blob"), mock(Dag.class)));
    given(parameter2 = new Parameter(STRING, new Name("string"), mock(Dag.class)));
    when(new Signature(STRING, name, asList(parameter, parameter2))).toString();
    thenReturned(STRING.name() + " " + name + "(" + parameter.type().name() + " "
        + parameter.name() + ", " + parameter2.type().name() + " " + parameter2.name() + ")");
  }
}
