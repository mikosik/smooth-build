package org.smoothbuild.parse.ast;

import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.parse.ast.StructNode.constructorNameToTypeName;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.jupiter.api.Test;

public class StructNodeTest {
  private StructNode struct;

  @Test
  public void constructor_name_is_lower_camelcase_of_type_name() {
    given(struct = new StructNode("MyType", list(), unknownLocation()));
    when(() -> struct.constructor().name());
    thenReturned("myType");
  }

  @Test
  public void constructor_name_is_lower_camelcase_of_type_name_preserving_underscores() {
    given(struct = new StructNode("My_Pretty_Type", list(), unknownLocation()));
    when(() -> struct.constructor().name());
    thenReturned("my_Pretty_Type");
  }

  @Test
  public void type_Name_is_upper_camelcase_of_constructor_name() {
    when(() -> constructorNameToTypeName("myType"));
    thenReturned("MyType");
  }
}
