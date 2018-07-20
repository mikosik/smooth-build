package org.smoothbuild.lang.base;

import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.TestingTypesDb;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.util.Dag;

public class SignatureTest {
  private final TypesDb typesDb = new TestingTypesDb();
  private final ConcreteType string = typesDb.string();
  private final ConcreteType blob = typesDb.blob();
  private final String name = "name";
  private final List<Parameter> parameters = list();
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
  public void to_string() throws Exception {
    given(parameter = new Parameter(blob, "blob", mock(Dag.class)));
    given(parameter2 = new Parameter(string, "string", mock(Dag.class)));
    when(new Signature(string, name, list(parameter, parameter2))).toString();
    thenReturned(string.name() + " " + name + "(" + parameter.type().name() + " "
        + parameter.name() + ", " + parameter2.type().name() + " " + parameter2.name() + ")");
  }
}
