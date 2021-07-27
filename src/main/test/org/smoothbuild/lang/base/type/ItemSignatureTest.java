package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.a;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;
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
    assertCall(() -> new ItemSignature(null, name, Optional.of(STRING)))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    assertCall(() -> new ItemSignature(STRING, (String) null, Optional.of(STRING)))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_getter() {
    item = new ItemSignature(STRING, name, Optional.of(STRING));
    assertThat(item.type())
        .isEqualTo(STRING);
  }

  @Test
  public void name_getter() {
    item = new ItemSignature(STRING, name, Optional.of(STRING));
    assertThat(item.name())
        .isEqualTo(Optional.of(name));
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(new ItemSignature(STRING, "equal", Optional.of(STRING)));
    tester.addEqualityGroup(new ItemSignature(STRING, "equal", Optional.empty()));
    for (Type type : list(BOOL, STRING, a(STRING), BLOB, NOTHING, PERSON)) {
      tester.addEqualityGroup(new ItemSignature(type, name, Optional.of(STRING)));
      tester.addEqualityGroup(new ItemSignature(type, "name2", Optional.of(STRING)));
    }
    tester.testEquals();
  }

  @Test
  public void to_padded_string() {
    item = new ItemSignature(STRING, "myName", Optional.of(STRING));
    assertThat(item.toPaddedString(10, 13))
        .isEqualTo("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() {
    item = new ItemSignature(STRING, "myName", Optional.of(STRING));
    assertThat(item.toPaddedString(1, 1))
        .isEqualTo("String: myName");
  }

  @Test
  public void to_string() {
    item = new ItemSignature(STRING, "myName", Optional.of(STRING));
    assertThat(item.toString())
        .isEqualTo("String myName");
  }

  @Test
  public void to_string_without_name() {
    item = new ItemSignature(STRING, Optional.empty(), Optional.of(STRING));
    assertThat(item.toString())
        .isEqualTo("String");
  }

  @Test
  public void params_to_string() {
    List<ItemSignature> names = new ArrayList<>();
    names.add(new ItemSignature(STRING, "param1", Optional.of(STRING)));
    names.add(new ItemSignature(STRING, "param2-with-very-long", Optional.of(STRING)));
    names.add(new ItemSignature(a(BLOB), "param3", Optional.of(STRING)));

    assertThat(ItemSignature.iterableToString(names))
        .isEqualTo(unlines(
            "  String: param1               ",
            "  String: param2-with-very-long",
            "  [Blob]: param3               ",
            ""));
  }
}
