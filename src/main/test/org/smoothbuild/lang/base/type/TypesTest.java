package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.lang.base.type.Types.array;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.bool;
import static org.smoothbuild.lang.base.type.Types.generic;
import static org.smoothbuild.lang.base.type.Types.missing;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.Location;

import com.google.common.testing.EqualsTester;

public class TypesTest {
  private static final Location LOCATION = unknownLocation();

  @Test
  public void equality() {
    new EqualsTester()
        .addEqualityGroup(
            missing(),
            missing())
        .addEqualityGroup(
            generic("A"),
            generic("A"))
        .addEqualityGroup(
            generic("B"),
            generic("B"))
        .addEqualityGroup(
            blob(),
            blob())
        .addEqualityGroup(
            bool(),
            bool())
        .addEqualityGroup(
            nothing(),
            nothing())
        .addEqualityGroup(
            string(),
            string())
        .addEqualityGroup(
            struct("MyStruct", list()),
            struct("MyStruct", list()))
        .addEqualityGroup(
            struct("MyStruct", list(new Field(string(), "field", LOCATION))),
            struct("MyStruct", list(new Field(string(), "field", LOCATION))))
        .addEqualityGroup(
            struct("MyStruct2", list(new Field(string(), "field", LOCATION))),
            struct("MyStruct2", list(new Field(string(), "field", LOCATION))))
        .addEqualityGroup(
            struct("MyStruct", list(new Field(string(), "field2", LOCATION))),
            struct("MyStruct", list(new Field(string(), "field2", LOCATION))))
        .addEqualityGroup(
            array(generic("A")),
            array(generic("A")))
        .addEqualityGroup(
            array(generic("B")),
            array(generic("B")))
        .addEqualityGroup(
            array(blob()),
            array(blob()))
        .addEqualityGroup(
            array(bool()),
            array(bool()))
        .addEqualityGroup(
            array(nothing()),
            array(nothing()))
        .addEqualityGroup(
            array(string()),
            array(string()))
        .addEqualityGroup(
            array(struct("MyStruct", list())),
            array(struct("MyStruct", list())))
        .addEqualityGroup(
            array(struct("MyStruct", list(new Field(string(), "field", LOCATION)))),
            array(struct("MyStruct", list(new Field(string(), "field", LOCATION)))))
        .addEqualityGroup(
            array(struct("MyStruct2", list(new Field(string(), "field", LOCATION)))),
            array(struct("MyStruct2", list(new Field(string(), "field", LOCATION)))))
        .addEqualityGroup(
            array(struct("MyStruct", list(new Field(string(), "field2", LOCATION)))),
            array(struct("MyStruct", list(new Field(string(), "field2", LOCATION)))))
        .addEqualityGroup(
            array(array(generic("A"))),
            array(array(generic("A"))))
        .addEqualityGroup(
            array(array(generic("B"))),
            array(array(generic("B"))))
        .addEqualityGroup(
            array(array(blob())),
            array(array(blob())))
        .addEqualityGroup(
            array(array(bool())),
            array(array(bool())))
        .addEqualityGroup(
            array(array(nothing())),
            array(array(nothing())))
        .addEqualityGroup(
            array(array(string())),
            array(array(string())))
        .addEqualityGroup(
            array(array(struct("MyStruct", list()))),
            array(array(struct("MyStruct", list()))))
        .addEqualityGroup(
            array(array(struct("MyStruct", list(new Field(string(), "field", LOCATION))))),
            array(array(struct("MyStruct", list(new Field(string(), "field", LOCATION))))))
        .addEqualityGroup(
            array(array(struct("MyStruct2", list(new Field(string(), "field", LOCATION))))),
            array(array(struct("MyStruct2", list(new Field(string(), "field", LOCATION))))))
        .addEqualityGroup(
            array(array(struct("MyStruct", list(new Field(string(), "field2", LOCATION))))),
            array(array(struct("MyStruct", list(new Field(string(), "field2", LOCATION))))))
        .testEquals();
  }
}
