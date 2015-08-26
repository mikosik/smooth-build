package org.smoothbuild.lang.function.base;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.function.base.Parameter.optionalParameter;
import static org.smoothbuild.lang.function.base.Parameter.parameter;
import static org.smoothbuild.lang.function.base.Parameter.parametersToString;
import static org.smoothbuild.lang.function.base.Parameter.requiredParameter;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;

import com.google.common.testing.EqualsTester;

public class ParameterTest {
  private Parameter parameter;

  @Test
  public void optional_parameter_creates_optional_parameter() throws Exception {
    given(parameter = optionalParameter(STRING, "name"));
    when(parameter).isRequired();
    thenReturned(false);
  }

  @Test
  public void required_parameter_creates_required_parameter() throws Exception {
    given(parameter = requiredParameter(STRING, "name"));
    when(parameter).isRequired();
    thenReturned(true);
  }

  @Test(expected = NullPointerException.class)
  public void null_type_is_forbidden() {
    parameter(null, "name", true);
  }

  @Test(expected = NullPointerException.class)
  public void null_name_is_forbidden() {
    parameter(STRING, null, true);
  }

  @Test
  public void getters() {
    when(parameter = parameter(STRING, "name", true));
    thenEqual(parameter.type(), STRING);
    thenEqual(parameter.name(), "name");
    thenEqual(parameter.isRequired(), true);
  }

  @Test
  public void params_with_different_names_have_different_name_hashes() {
    when(parameter(STRING, "name1", true).nameHash());
    thenReturned(not(parameter(STRING, "name2", true).nameHash()));
  }

  @Test
  public void params_with_same_names_but_different_types_have_the_same_name_hashes() {
    when(parameter(STRING, "name1", true).nameHash());
    thenReturned(parameter(BLOB, "name1", true).nameHash());
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(parameter(STRING, "equal", false), parameter(STRING, "equal", false));

    for (Type type : Types.allTypes()) {
      if (type.isAllowedAsParameter()) {
        tester.addEqualityGroup(parameter(type, "name", false));
        tester.addEqualityGroup(parameter(type, "name", true));
        tester.addEqualityGroup(parameter(type, "name2", false));
        tester.addEqualityGroup(parameter(type, "name2", true));
      }
    }

    tester.testEquals();
  }

  @Test
  public void to_padded_string() {
    given(parameter = parameter(STRING, "myName", false));
    when(parameter.toPaddedString(10, 13));
    thenReturned("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() {
    given(parameter = parameter(STRING, "myName", false));
    when(parameter.toPaddedString(1, 1));
    thenReturned("String: myName");
  }

  @Test
  public void to_string() {
    given(parameter = parameter(STRING, "name", false));
    when(parameter.toString());
    thenReturned("Param(String: name)");
  }

  @Test
  public void params_to_string() {
    List<Parameter> parameters = new ArrayList<>();
    parameters.add(parameter(STRING, "param1", false));
    parameters.add(parameter(STRING, "param2-with-very-long", false));
    parameters.add(parameter(FILE_ARRAY, "param3", true));

    when(parametersToString(parameters));
    thenReturned("" //
        + "  String: param1               \n" //
        + "  String: param2-with-very-long\n" //
        + "  File[]: param3               \n" //
    );
  }
}
