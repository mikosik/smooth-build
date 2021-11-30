package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.TestingTypesS.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypesS.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypesS.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypesS.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypesS.STRING;
import static org.smoothbuild.lang.base.type.TestingTypesS.a;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.type.impl.TypeS;

import com.google.common.testing.EqualsTester;

public class ItemSigSTest {
  private final String name = "name";
  private ItemSigS item;

  @Test
  public void null_type_is_forbidden() {
    assertCall(() -> new ItemSigS(null, name, Optional.of(STRING)))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    assertCall(() -> new ItemSigS(STRING, (String) null, Optional.of(STRING)))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_getter() {
    item = new ItemSigS(STRING, name, Optional.of(STRING));
    assertThat(item.type())
        .isEqualTo(STRING);
  }

  @Test
  public void name_getter() {
    item = new ItemSigS(STRING, name, Optional.of(STRING));
    assertThat(item.nameO())
        .isEqualTo(Optional.of(name));
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(new ItemSigS(STRING, "equal", Optional.of(STRING)));
    tester.addEqualityGroup(new ItemSigS(STRING, "equal", Optional.empty()));
    for (TypeS type : list(BOOL, STRING, a(STRING), BLOB, NOTHING, PERSON)) {
      tester.addEqualityGroup(new ItemSigS(type, name, Optional.of(STRING)));
      tester.addEqualityGroup(new ItemSigS(type, "name2", Optional.of(STRING)));
    }
    tester.testEquals();
  }

  @Test
  public void to_padded_string() {
    item = new ItemSigS(STRING, "myName", Optional.of(STRING));
    assertThat(item.toPaddedString(10, 13))
        .isEqualTo("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() {
    item = new ItemSigS(STRING, "myName", Optional.of(STRING));
    assertThat(item.toPaddedString(1, 1))
        .isEqualTo("String: myName");
  }

  @Test
  public void to_string() {
    item = new ItemSigS(STRING, "myName", Optional.of(STRING));
    assertThat(item.toString())
        .isEqualTo("String myName");
  }

  @Test
  public void to_string_without_name() {
    item = new ItemSigS(STRING, Optional.empty(), Optional.of(STRING));
    assertThat(item.toString())
        .isEqualTo("String");
  }
}