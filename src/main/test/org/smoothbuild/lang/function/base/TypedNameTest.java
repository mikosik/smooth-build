package org.smoothbuild.lang.function.base;

import static org.smoothbuild.lang.type.Types.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

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
  public void to_string() throws Exception {
    given(typedName = new TypedName(STRING, new Name("myName")));
    when(() -> typedName.toString());
    thenReturned("String myName");
  }
}
