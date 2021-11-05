package org.smoothbuild.lang.base.type.impl;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Named.named;
import static org.smoothbuild.util.collect.NamedList.namedList;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.type.AbstractTypeGenericTest;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.util.collect.NamedList;

public class TypeSGenericTest extends AbstractTypeGenericTest<TypeS> {
  @Override
  public TypeFactory<TypeS> typeFactory() {
    return typeFactoryS();
  }

  @Nested
  class _struct {
    @Test
    public void _without_fields_can_be_created() {
      structST("MyStruct", namedList(list()));
    }

    @Test
    public void first_field_type_can_be_nothing() {
      structST("MyStruct", namedList(list(named("fieldName", nothingST()))));
    }

    @Test
    public void first_field_type_can_be_nothing_array() {
      structST("MyStruct", namedList(list(named("fieldName", arrayST(nothingST())))));
    }

    @ParameterizedTest
    @MethodSource("struct_name_cases")
    public void struct_name(Function<TypeFactoryS, StructType> factoryCall, String expected) {
      assertThat(invoke(factoryCall).name())
          .isEqualTo(expected);
    }

    public static List<Arguments> struct_name_cases() {
      return asList(
          args(f -> f.struct("MyStruct", namedList(list())), "MyStruct"),
          args(f -> f.struct("", namedList(list())), "")
      );
    }

    @ParameterizedTest
    @MethodSource("struct_fields_cases")
    public void struct_fields(
        Function<TypeFactoryS, StructType> factoryCall,
        Function<TypeFactoryS, NamedList<Type>> expected) {
      assertThat(invoke(factoryCall).fields())
          .isEqualTo(invoke(expected));
    }

    public static List<Arguments> struct_fields_cases() {
      return asList(
          args(f -> f.struct("Person", namedList(list())), f -> namedList(list())),
          args(f -> f.struct("Person", namedList(list(named("field", f.string())))),
              f -> namedList(list(named("field", f.string())))),
          args(f -> f.struct("Person",
              namedList(list(named("field", f.string()), named("field2", f.int_())))),
              f -> namedList(list(named("field", f.string()), named("field2", f.int_()))))
      );
    }
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(
      Function<TypeFactoryS, R> factoryCall1,
      Function<TypeFactoryS, R> factoryCall2) {
    return arguments(factoryCall1, factoryCall2);
  }

  /**
   * We need this chaining method because without it java compiler is not able to infer
   * exact type of lambda expression passed to factoryCall.
   */
  private static <R> Arguments args(Function<TypeFactoryS, R> factoryCall, Object arg) {
    return arguments(factoryCall, arg);
  }

  private <R> R invoke(Function<TypeFactoryS, R> f) {
    return f.apply(typeFactoryS());
  }
}
