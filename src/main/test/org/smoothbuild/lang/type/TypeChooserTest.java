package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.TestingTypes.a;
import static org.smoothbuild.lang.type.TestingTypes.arrayA;
import static org.smoothbuild.lang.type.TestingTypes.arrayString;
import static org.smoothbuild.lang.type.TestingTypes.blob;
import static org.smoothbuild.lang.type.TestingTypes.string;
import static org.smoothbuild.lang.type.TypeChooser.inferCallType;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.function.IntFunction;

import org.junit.Test;
import org.smoothbuild.lang.base.ParameterInfo;

public class TypeChooserTest {
  @Test
  public void concrete_result_type_is_returned_as_inferred() throws Exception {
    when(inferCallType(string, list(), arguments(-1, null)));
    thenReturned(string);
  }

  @Test
  public void generic_result_type_is_infered_from_generic_parameter() throws Exception {
    when(inferCallType(a, list(param(a), param(blob)), arguments(0, string)));
    thenReturned(string);
  }

  @Test
  public void generic_result_type_is_infered_from_generic_array_parameter() throws Exception {
    when(inferCallType(a, list(param(arrayA), param(blob)), arguments(0, arrayString)));
    thenReturned(string);
  }

  @Test
  public void generic_array_result_type_is_infered_from_generic_parameter() throws Exception {
    when(inferCallType(arrayA, list(param(a), param(blob)), arguments(0, string)));
    thenReturned(arrayString);
  }

  @Test
  public void generic_array_result_type_is_infered_from_generic_array_parameter() throws Exception {
    when(inferCallType(arrayA, list(param(arrayA), param(blob)), arguments(0, arrayString)));
    thenReturned(arrayString);
  }

  private ParameterInfo param(Type type) {
    return new ParameterInfo(type, "name", true);
  }

  private IntFunction<Type> arguments(int assignedArg, Type type) {
    return (index) -> {
      if (index == assignedArg) {
        return type;
      } else {
        throw new IllegalArgumentException("unexpected argument index");
      }
    };
  }
}
