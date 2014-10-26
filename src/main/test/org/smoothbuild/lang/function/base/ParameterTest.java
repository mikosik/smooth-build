package org.smoothbuild.lang.function.base;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.Types.BLOB;
import static org.smoothbuild.lang.base.Types.FILE_ARRAY;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.base.Types.parameterTypes;
import static org.smoothbuild.lang.function.base.Parameter.parameter;
import static org.smoothbuild.lang.function.base.Parameter.parametersToString;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.util.LineBuilder;

import com.google.common.hash.HashCode;
import com.google.common.testing.EqualsTester;

public class ParameterTest {

  @Test(expected = NullPointerException.class)
  public void null_type_is_forbidden() throws Exception {
    parameter(null, "name", true);
  }

  @Test(expected = NullPointerException.class)
  public void null_name_is_forbidden() throws Exception {
    parameter(STRING, null, true);
  }

  @Test
  public void type() throws Exception {
    assertThat(parameter(STRING, "name", true).type()).isEqualTo(STRING);
  }

  @Test
  public void name() throws Exception {
    assertThat(parameter(STRING, "name", true).name()).isEqualTo("name");
  }

  @Test
  public void is_required() throws Exception {
    assertThat(parameter(STRING, "name", true).isRequired()).isTrue();
  }

  @Test
  public void params_with_different_names_have_different_name_hashes() throws Exception {
    HashCode hash1 = parameter(STRING, "name1", true).nameHash();
    HashCode hash2 = parameter(STRING, "name2", true).nameHash();
    assertThat(hash1).isNotEqualTo(hash2);
  }

  @Test
  public void params_with_same_names_but_different_types_have_the_same_name_hashes()
      throws Exception {
    HashCode hash1 = parameter(STRING, "name1", true).nameHash();
    HashCode hash2 = parameter(BLOB, "name1", true).nameHash();
    assertThat(hash1).isEqualTo(hash2);
  }

  @Test
  public void equals_and_hash_code() throws Exception {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(parameter(STRING, "equal", false), parameter(STRING, "equal", false));

    for (Type<?> type : parameterTypes()) {
      tester.addEqualityGroup(parameter(type, "name", false));
      tester.addEqualityGroup(parameter(type, "name", true));
      tester.addEqualityGroup(parameter(type, "name2", false));
      tester.addEqualityGroup(parameter(type, "name2", true));
    }

    tester.testEquals();
  }

  @Test
  public void to_padded_string() throws Exception {
    Expression<?> expression = mock(Expression.class);
    given(willReturn(STRING), expression).type();

    Parameter parameter = parameter(STRING, "myName", false);
    String actual = parameter.toPaddedString(10, 13);

    assertThat(actual).isEqualTo("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() throws Exception {
    Expression<?> expression = mock(Expression.class);
    given(willReturn(STRING), expression).type();

    Parameter parameter = parameter(STRING, "myName", false);
    String actual = parameter.toPaddedString(1, 1);

    assertThat(actual).isEqualTo("String: myName");
  }

  @Test
  public void to_string() throws Exception {
    assertThat(parameter(STRING, "name", false).toString()).isEqualTo("Param(String: name)");
  }

  @Test
  public void params_to_string() throws Exception {
    Set<Parameter> parameters = newHashSet();
    parameters.add(parameter(STRING, "param1", false));
    parameters.add(parameter(STRING, "param2-with-very-long", false));
    parameters.add(parameter(FILE_ARRAY, "param3", true));

    String actual = parametersToString(parameters);

    LineBuilder builder = new LineBuilder();
    builder.addLine("  String: param1               ");
    builder.addLine("  File[]: param3               ");
    builder.addLine("  String: param2-with-very-long");
    assertThat(actual).isEqualTo(builder.build());
  }
}
