package org.smoothbuild.lang.function.base;

import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.smoothbuild.lang.type.Types.FILE;
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

public class TypedNameTest {
  private final Name name = new Name("name");
  private final Type type = STRING;
  private TypedName typedName;

  @Test
  public void null_type_is_forbidden() {
    when(() -> new TypedName(null, name));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    when(() -> new TypedName(type, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type_getter() throws Exception {
    given(typedName = new TypedName(type, name));
    when(() -> typedName.type());
    thenReturned(type);
  }

  @Test
  public void name_getter() throws Exception {
    given(typedName = new TypedName(type, name));
    when(() -> typedName.name());
    thenReturned(name);
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        new TypedName(STRING, new Name("equal")),
        new TypedName(STRING, new Name("equal")));
    for (Type type : Types.allTypes()) {
      tester.addEqualityGroup(new TypedName(type, name));
      tester.addEqualityGroup(new TypedName(type, new Name("name2")));
    }
    tester.testEquals();
  }

  @Test
  public void to_padded_string() {
    given(typedName = new TypedName(STRING, new Name("myName")));
    when(typedName.toPaddedString(10, 13));
    thenReturned("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() {
    given(typedName = new TypedName(STRING, new Name("myName")));
    when(typedName.toPaddedString(1, 1));
    thenReturned("String: myName");
  }

  @Test
  public void to_string() throws Exception {
    given(typedName = new TypedName(STRING, new Name("myName")));
    when(() -> typedName.toString());
    thenReturned("String myName");
  }

  @Test
  public void params_to_string() {
    List<TypedName> names = new ArrayList<>();
    names.add(new TypedName(STRING, new Name("param1")));
    names.add(new TypedName(STRING, new Name("param2-with-very-long")));
    names.add(new TypedName(arrayOf(FILE), new Name("param3")));

    when(TypedName.iterableToString(names));
    thenReturned(""
        + "  String: param1               \n"
        + "  String: param2-with-very-long\n"
        + "  [File]: param3               \n");
  }
}
