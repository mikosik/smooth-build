package org.smoothbuild.lang.function.base;

import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;

import com.google.common.testing.EqualsTester;

public class ParameterInfoTest {
  private final Name name = new Name("name");
  private final Type type = STRING;
  private ParameterInfo parameterInfo;

  @Test
  public void null_type_is_forbidden() {
    when(() -> new ParameterInfo(null, name, true));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    when(() -> new ParameterInfo(type, null, true));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type_getter() throws Exception {
    given(parameterInfo = new ParameterInfo(type, name, true));
    when(() -> parameterInfo.type());
    thenReturned(type);
  }

  @Test
  public void name_getter() throws Exception {
    given(parameterInfo = new ParameterInfo(type, name, true));
    when(() -> parameterInfo.name());
    thenReturned(name);
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        new ParameterInfo(STRING, new Name("equal"), true),
        new ParameterInfo(STRING, new Name("equal"), true));
    for (Type type : Types.allTypes()) {
      tester.addEqualityGroup(new ParameterInfo(type, name, true));
      tester.addEqualityGroup(new ParameterInfo(type, new Name("name2"), true));
    }
    tester.testEquals();
  }

  @Test
  public void to_padded_string() {
    given(parameterInfo = new ParameterInfo(STRING, new Name("myName"), true));
    when(parameterInfo.toPaddedString(10, 13));
    thenReturned("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() {
    given(parameterInfo = new ParameterInfo(STRING, new Name("myName"), true));
    when(parameterInfo.toPaddedString(1, 1));
    thenReturned("String: myName");
  }

  @Test
  public void to_string() throws Exception {
    given(parameterInfo = new ParameterInfo(STRING, new Name("myName"), true));
    when(() -> parameterInfo.toString());
    thenReturned("String myName");
  }

  @Test
  public void params_to_string() {
    List<ParameterInfo> names = new ArrayList<>();
    names.add(new ParameterInfo(STRING, new Name("param1"), true));
    names.add(new ParameterInfo(STRING, new Name("param2-with-very-long"), true));
    names.add(new ParameterInfo(arrayOf(BLOB), new Name("param3"), true));

    when(ParameterInfo.iterableToString(names));
    thenReturned(""
        + "  String: param1               \n"
        + "  String: param2-with-very-long\n"
        + "  [Blob]: param3               \n");
  }
}
