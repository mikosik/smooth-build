package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.Location.commandLineLocation;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.lang.base.type.TestingTypes.bool;
import static org.smoothbuild.lang.base.type.TestingTypes.string;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class FieldTest {
  @Test
  public void equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(
            new Field(string, "name", unknownLocation()),
            new Field(string, "name", unknownLocation()),
            new Field(string, "name", commandLineLocation())
        )
        .addEqualityGroup(
            new Field(string, "name2", unknownLocation()),
            new Field(string, "name2", unknownLocation())
        )
        .addEqualityGroup(
            new Field(bool, "name2", unknownLocation()),
            new Field(bool, "name2", unknownLocation())
        );
  }
}
