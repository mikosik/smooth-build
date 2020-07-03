package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Strings.unlines;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;

public class ItemInfoTest extends TestingContext {
  private final String name = "name";
  private ItemInfo itemInfo;
  private final ConcreteType bool = boolType();
  private final ConcreteType string = stringType();
  private final ConcreteType blob = blobType();
  private final ConcreteType nothing = nothingType();
  private final ConcreteType type = string;

  @Test
  public void null_type_is_forbidden() {
    assertCall(() -> new ItemInfo(0, null, name, true))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    assertCall(() -> new ItemInfo(0, type, null, true))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_getter() {
    itemInfo = new ItemInfo(0, type, name, true);
    assertThat(itemInfo.type())
        .isEqualTo(type);
  }

  @Test
  public void name_getter() {
    itemInfo = new ItemInfo(0, type, name, true);
    assertThat(itemInfo.name())
        .isEqualTo(name);
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        new ItemInfo(0, string, "equal", true),
        new ItemInfo(1, string, "equal", true));
    for (ConcreteType type :
        ImmutableList.of(bool, string, arrayType(string), blob, nothing, personType())) {
      tester.addEqualityGroup(
          new ItemInfo(0, type, name, true),
          new ItemInfo(1, type, name, true));
      tester.addEqualityGroup(
          new ItemInfo(0, type, "name2", true),
          new ItemInfo(1, type, "name2", true));
    }
    tester.testEquals();
  }

  @Test
  public void to_padded_string() {
    itemInfo = new ItemInfo(0, string, "myName", true);
    assertThat(itemInfo.toPaddedString(10, 13))
        .isEqualTo("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() {
    itemInfo = new ItemInfo(0, string, "myName", true);
    assertThat(itemInfo.toPaddedString(1, 1))
        .isEqualTo("String: myName");
  }

  @Test
  public void to_string() {
    itemInfo = new ItemInfo(0, string, "myName", true);
    assertThat(itemInfo.toString())
        .isEqualTo("String myName");
  }

  @Test
  public void params_to_string() {
    List<ItemInfo> names = new ArrayList<>();
    names.add(new ItemInfo(0, string, "param1", true));
    names.add(new ItemInfo(0, string, "param2-with-very-long", true));
    names.add(new ItemInfo(0, arrayType(blob), "param3", true));

    assertThat(ItemInfo.iterableToString(names))
        .isEqualTo(unlines(
            "  String: param1               ",
            "  String: param2-with-very-long",
            "  [Blob]: param3               ",
            ""));
  }
}
