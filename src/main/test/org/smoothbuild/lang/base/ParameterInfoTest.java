package org.smoothbuild.lang.base;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.TestingTypes;
import org.smoothbuild.lang.type.TestingTypesDb;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypesDb;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;

public class ParameterInfoTest {
  private final String name = "name";
  private ParameterInfo parameterInfo;
  private final TypesDb typesDb = new TestingTypesDb();
  private final Type string = typesDb.string();
  private final Type blob = typesDb.blob();
  private final Type type = string;
  private final Type generic = typesDb.generic("b");

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
        new ParameterInfo(string, "equal", true),
        new ParameterInfo(string, "equal", true));
    for (Type type : ImmutableList.of(string, typesDb.array(string), blob, generic,
        personType())) {
      tester.addEqualityGroup(new ParameterInfo(type, name, true));
      tester.addEqualityGroup(new ParameterInfo(type, "name2", true));
    }
    tester.testEquals();
  }

  @Test
  public void to_padded_string() {
    given(parameterInfo = new ParameterInfo(string, "myName", true));
    when(parameterInfo.toPaddedString(10, 13));
    thenReturned("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() {
    given(parameterInfo = new ParameterInfo(string, "myName", true));
    when(parameterInfo.toPaddedString(1, 1));
    thenReturned("String: myName");
  }

  @Test
  public void to_string() throws Exception {
    given(parameterInfo = new ParameterInfo(string, "myName", true));
    when(() -> parameterInfo.toString());
    thenReturned("String myName");
  }

  @Test
  public void params_to_string() {
    List<ParameterInfo> names = new ArrayList<>();
    names.add(new ParameterInfo(string, "param1", true));
    names.add(new ParameterInfo(string, "param2-with-very-long", true));
    names.add(new ParameterInfo(typesDb.array(blob), "param3", true));

    when(ParameterInfo.iterableToString(names));
    thenReturned(""
        + "  String: param1               \n"
        + "  String: param2-with-very-long\n"
        + "  [Blob]: param3               \n");
  }

  private StructType personType() {
    return TestingTypes.personType(typesDb);
  }
}
