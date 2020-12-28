package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.commandLineLocation;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.a;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Strings.unlines;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class ItemSignatureTest {
  private final String name = "name";
  private ItemSignature item;

  @Test
  public void null_type_is_forbidden() {
    assertCall(() -> new ItemSignature(null, name, Optional.of(STRING), internal()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    assertCall(() -> new ItemSignature(STRING, null, Optional.of(STRING), internal()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_getter() {
    item = new ItemSignature(STRING, name, Optional.of(STRING), internal());
    assertThat(item.type())
        .isEqualTo(STRING);
  }

  @Test
  public void name_getter() {
    item = new ItemSignature(STRING, name, Optional.of(STRING), internal());
    assertThat(item.name())
        .isEqualTo(name);
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        new ItemSignature(STRING, "equal", Optional.of(STRING), internal()));
    tester.addEqualityGroup(
        new ItemSignature(STRING, "equal", Optional.of(STRING), commandLineLocation()));
    tester.addEqualityGroup(
        new ItemSignature(STRING, "equal", Optional.empty(), internal()));
    for (Type type : List.of(BOOL, STRING, a(STRING), BLOB, NOTHING, PERSON)) {
      tester.addEqualityGroup(new ItemSignature(type, name, Optional.of(STRING), internal()));
      tester.addEqualityGroup(new ItemSignature(type, "name2", Optional.of(STRING), internal()));
    }
    tester.testEquals();
  }

  @Test
  public void to_padded_string() {
    item = new ItemSignature(STRING, "myName", Optional.of(STRING), internal());
    assertThat(item.toPaddedString(10, 13))
        .isEqualTo("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() {
    item = new ItemSignature(STRING, "myName", Optional.of(STRING), internal());
    assertThat(item.toPaddedString(1, 1))
        .isEqualTo("String: myName");
  }

  @Test
  public void to_string() {
    item = new ItemSignature(STRING, "myName", Optional.of(STRING), internal());
    assertThat(item.toString())
        .isEqualTo("String myName");
  }

  @Test
  public void params_to_string() {
    List<ItemSignature> names = new ArrayList<>();
    names.add(new ItemSignature(STRING, "param1", Optional.of(STRING), internal()));
    names.add(new ItemSignature(STRING, "param2-with-very-long", Optional.of(STRING), internal()));
    names.add(new ItemSignature(a(BLOB), "param3", Optional.of(STRING), internal()));

    assertThat(ItemSignature.iterableToString(names))
        .isEqualTo(unlines(
            "  String: param1               ",
            "  String: param2-with-very-long",
            "  [Blob]: param3               ",
            ""));
  }
}
