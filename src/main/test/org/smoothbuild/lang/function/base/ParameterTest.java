package org.smoothbuild.lang.function.base;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.function.base.Parameter.parameter;
import static org.smoothbuild.lang.function.base.Parameter.parametersToString;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;

import com.google.common.testing.EqualsTester;

public class ParameterTest {
  private Parameter parameter;

  @Test
  public void optional_parameter_creates_optional_parameter() throws Exception {
    given(parameter = Parameter.parameter(STRING, "name", mock(Expression.class)));
    when(parameter).isRequired();
    thenReturned(false);
  }

  @Test
  public void required_parameter_creates_required_parameter() throws Exception {
    given(parameter = parameter(STRING, "name"));
    when(parameter).isRequired();
    thenReturned(true);
  }

  @Test(expected = NullPointerException.class)
  public void null_type_is_forbidden() {
    parameter(null, "name");
  }

  @Test(expected = NullPointerException.class)
  public void null_name_is_forbidden() {
    parameter(STRING, null);
  }

  @Test
  public void getters() {
    when(parameter = parameter(STRING, "name"));
    thenEqual(parameter.type(), STRING);
    thenEqual(parameter.name(), "name");
    thenEqual(parameter.isRequired(), true);
  }

  @Test
  public void params_with_different_names_have_different_name_hashes() {
    when(parameter(STRING, "name1").nameHash());
    thenReturned(not(parameter(STRING, "name2").nameHash()));
  }

  @Test
  public void params_with_same_names_but_different_types_have_the_same_name_hashes() {
    when(parameter(STRING, "name1").nameHash());
    thenReturned(parameter(BLOB, "name1").nameHash());
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        parameter(STRING, "equal", mock(Expression.class)),
        parameter(STRING, "equal", mock(Expression.class)));
    for (Type type : Types.allTypes()) {
      tester.addEqualityGroup(parameter(type, "name", mock(Expression.class)));
      tester.addEqualityGroup(parameter(type, "name"));
      tester.addEqualityGroup(parameter(type, "name2", mock(Expression.class)));
      tester.addEqualityGroup(parameter(type, "name2"));
    }
    tester.testEquals();
  }

  @Test
  public void to_padded_string() {
    given(parameter = parameter(STRING, "myName", mock(Expression.class)));
    when(parameter.toPaddedString(10, 13));
    thenReturned("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() {
    given(parameter = parameter(STRING, "myName", mock(Expression.class)));
    when(parameter.toPaddedString(1, 1));
    thenReturned("String: myName");
  }

  @Test
  public void to_string() {
    given(parameter = parameter(STRING, "name", mock(Expression.class)));
    when(parameter.toString());
    thenReturned("Param(String: name)");
  }

  @Test
  public void params_to_string() {
    List<Parameter> parameters = new ArrayList<>();
    parameters.add(parameter(STRING, "param1", mock(Expression.class)));
    parameters.add(parameter(STRING, "param2-with-very-long", mock(Expression.class)));
    parameters.add(parameter(FILE_ARRAY, "param3"));

    when(parametersToString(parameters));
    thenReturned("" //
        + "  String: param1               \n" //
        + "  String: param2-with-very-long\n" //
        + "  [File]: param3               \n" //
    );
  }
}
