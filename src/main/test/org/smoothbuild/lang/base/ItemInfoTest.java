package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.commandLineLocation;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.type.TestingTypes.blob;
import static org.smoothbuild.lang.base.type.TestingTypes.bool;
import static org.smoothbuild.lang.base.type.TestingTypes.nothing;
import static org.smoothbuild.lang.base.type.TestingTypes.person;
import static org.smoothbuild.lang.base.type.TestingTypes.string;
import static org.smoothbuild.lang.base.type.Types.array;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Strings.unlines;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.type.ConcreteType;

import com.google.common.testing.EqualsTester;

public class ItemInfoTest {
  private final String name = "name";
  private ItemInfo itemInfo;

  @Test
  public void null_type_is_forbidden() {
    assertCall(() -> new ItemInfo(0, null, name, true, internal()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    assertCall(() -> new ItemInfo(0, string, null, true, internal()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_getter() {
    itemInfo = new ItemInfo(0, string, name, true, internal());
    assertThat(itemInfo.type())
        .isEqualTo(string);
  }

  @Test
  public void name_getter() {
    itemInfo = new ItemInfo(0, string, name, true, internal());
    assertThat(itemInfo.name())
        .isEqualTo(name);
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        new ItemInfo(0, string, "equal", true, internal()),
        new ItemInfo(0, string, "equal", true, commandLineLocation()),
        new ItemInfo(1, string, "equal", true, internal()));
    for (ConcreteType type : List.of(bool, string, array(string), blob, nothing, person)) {
      tester.addEqualityGroup(
          new ItemInfo(0, type, name, true, internal()),
          new ItemInfo(1, type, name, true, internal()));
      tester.addEqualityGroup(
          new ItemInfo(0, type, "name2", true, internal()),
          new ItemInfo(1, type, "name2", true, internal()));
    }
    tester.testEquals();
  }

  @Test
  public void to_padded_string() {
    itemInfo = new ItemInfo(0, string, "myName", true, internal());
    assertThat(itemInfo.toPaddedString(10, 13))
        .isEqualTo("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() {
    itemInfo = new ItemInfo(0, string, "myName", true, internal());
    assertThat(itemInfo.toPaddedString(1, 1))
        .isEqualTo("String: myName");
  }

  @Test
  public void to_string() {
    itemInfo = new ItemInfo(0, string, "myName", true, internal());
    assertThat(itemInfo.toString())
        .isEqualTo("String myName");
  }

  @Test
  public void params_to_string() {
    List<ItemInfo> names = new ArrayList<>();
    names.add(new ItemInfo(0, string, "param1", true, internal()));
    names.add(new ItemInfo(0, string, "param2-with-very-long", true, internal()));
    names.add(new ItemInfo(0, array(blob), "param3", true, internal()));

    assertThat(ItemInfo.iterableToString(names))
        .isEqualTo(unlines(
            "  String: param1               ",
            "  String: param2-with-very-long",
            "  [Blob]: param3               ",
            ""));
  }
}
