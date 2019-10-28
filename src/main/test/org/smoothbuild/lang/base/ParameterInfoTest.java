package org.smoothbuild.lang.base;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;

public class ParameterInfoTest extends TestingContext {
  private final String name = "name";
  private ParameterInfo parameterInfo;
  private final ConcreteType bool = boolType();
  private final ConcreteType string = stringType();
  private final ConcreteType blob = blobType();
  private final ConcreteType nothing = nothingType();
  private final ConcreteType type = string;

  @Test
  public void null_type_is_forbidden() {
    when(() -> new ParameterInfo(0, null, name, true));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    when(() -> new ParameterInfo(0, type, null, true));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type_getter() throws Exception {
    given(parameterInfo = new ParameterInfo(0, type, name, true));
    when(() -> parameterInfo.type());
    thenReturned(type);
  }

  @Test
  public void name_getter() throws Exception {
    given(parameterInfo = new ParameterInfo(0, type, name, true));
    when(() -> parameterInfo.name());
    thenReturned(name);
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        new ParameterInfo(0, string, "equal", true),
        new ParameterInfo(1, string, "equal", true));
    for (ConcreteType type :
        ImmutableList.of(bool, string, arrayType(string), blob, nothing, personType())) {
      tester.addEqualityGroup(
          new ParameterInfo(0, type, name, true),
          new ParameterInfo(1, type, name, true));
      tester.addEqualityGroup(
          new ParameterInfo(0, type, "name2", true),
          new ParameterInfo(1, type, "name2", true));
    }
    tester.testEquals();
  }

  @Test
  public void to_padded_string() {
    given(parameterInfo = new ParameterInfo(0, string, "myName", true));
    when(parameterInfo.toPaddedString(10, 13));
    thenReturned("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() {
    given(parameterInfo = new ParameterInfo(0, string, "myName", true));
    when(parameterInfo.toPaddedString(1, 1));
    thenReturned("String: myName");
  }

  @Test
  public void to_string() throws Exception {
    given(parameterInfo = new ParameterInfo(0, string, "myName", true));
    when(() -> parameterInfo.toString());
    thenReturned("String myName");
  }

  @Test
  public void params_to_string() {
    List<ParameterInfo> names = new ArrayList<>();
    names.add(new ParameterInfo(0, string, "param1", true));
    names.add(new ParameterInfo(0, string, "param2-with-very-long", true));
    names.add(new ParameterInfo(0, arrayType(blob), "param3", true));

    when(ParameterInfo.iterableToString(names));
    thenReturned(""
        + "  String: param1               \n"
        + "  String: param2-with-very-long\n"
        + "  [Blob]: param3               \n");
  }
}
