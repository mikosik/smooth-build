package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.commandLineLocation;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.Types.array;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Strings.unlines;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.type.ConcreteType;

import com.google.common.testing.EqualsTester;

public class ItemTest {
  private final String name = "name";
  private Item item;

  @Test
  public void null_type_is_forbidden() {
    assertCall(() -> new Item(0, null, name, true, internal()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    assertCall(() -> new Item(0, STRING, null, true, internal()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_getter() {
    item = new Item(0, STRING, name, true, internal());
    assertThat(item.type())
        .isEqualTo(STRING);
  }

  @Test
  public void name_getter() {
    item = new Item(0, STRING, name, true, internal());
    assertThat(item.name())
        .isEqualTo(name);
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        new Item(0, STRING, "equal", true, internal()),
        new Item(0, STRING, "equal", true, commandLineLocation()),
        new Item(1, STRING, "equal", true, internal()));
    for (ConcreteType type : List.of(BOOL, STRING, array(STRING), BLOB, NOTHING, PERSON)) {
      tester.addEqualityGroup(
          new Item(0, type, name, true, internal()),
          new Item(1, type, name, true, internal()));
      tester.addEqualityGroup(
          new Item(0, type, "name2", true, internal()),
          new Item(1, type, "name2", true, internal()));
    }
    tester.testEquals();
  }

  @Test
  public void to_padded_string() {
    item = new Item(0, STRING, "myName", true, internal());
    assertThat(item.toPaddedString(10, 13))
        .isEqualTo("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() {
    item = new Item(0, STRING, "myName", true, internal());
    assertThat(item.toPaddedString(1, 1))
        .isEqualTo("String: myName");
  }

  @Test
  public void to_string() {
    item = new Item(0, STRING, "myName", true, internal());
    assertThat(item.toString())
        .isEqualTo("String myName");
  }

  @Test
  public void params_to_string() {
    List<Item> names = new ArrayList<>();
    names.add(new Item(0, STRING, "param1", true, internal()));
    names.add(new Item(0, STRING, "param2-with-very-long", true, internal()));
    names.add(new Item(0, array(BLOB), "param3", true, internal()));

    assertThat(Item.iterableToString(names))
        .isEqualTo(unlines(
            "  String: param1               ",
            "  String: param2-with-very-long",
            "  [Blob]: param3               ",
            ""));
  }
}
