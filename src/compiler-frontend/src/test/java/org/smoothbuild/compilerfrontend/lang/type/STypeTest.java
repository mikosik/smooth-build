package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;
import static org.smoothbuild.compilerfrontend.lang.name.Name.typeName;

import com.google.common.testing.EqualsTester;
import java.util.function.Function;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestContext;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.lang.name.NList;

public class STypeTest extends FrontendCompilerTestContext {
  @Test
  void verify_all_base_types_are_tested() {
    assertThat(STypes.baseTypes()).hasSize(4);
  }

  @ParameterizedTest
  @MethodSource("specifiers")
  public void specifier(SType type, String specifier) {
    assertThat(type.specifier()).isEqualTo(specifier);
  }

  @ParameterizedTest
  @MethodSource("specifiers")
  public void quoted_specifier(SType type, String specifier) {
    assertThat(type.q()).isEqualTo("`" + specifier + "`");
  }

  @ParameterizedTest
  @MethodSource("to_string")
  public void to_string(SType type, String toString) {
    assertThat(type.toString()).isEqualTo(toString);
  }

  public static List<Arguments> specifiers() {
    return new STypeTest().specifiers_non_static();
  }

  public List<Arguments> specifiers_non_static() {
    return specifier_or_to_string()
        .addAll(list(
            arguments(sStructType("MyStruct", nlist()), "MyStruct"),
            arguments(sStructType("MyStruct", nlist(sSig(sIntType(), "field"))), "MyStruct")));
  }

  public static List<Arguments> to_string() {
    return new STypeTest().to_string_non_static();
  }

  public List<Arguments> to_string_non_static() {
    return specifier_or_to_string()
        .addAll(list(
            arguments(sStructType("MyStruct", nlist()), "MyStruct{}"),
            arguments(
                sStructType("MyStruct", nlist(sSig(sIntType(), "field"))), "MyStruct{Int field}")));
  }

  public List<Arguments> specifier_or_to_string() {
    return list(
        arguments(sBlobType(), "Blob"),
        arguments(sBoolType(), "Bool"),
        arguments(sIntType(), "Int"),
        arguments(sStringType(), "String"),
        arguments(varA(), "A"),
        arguments(sTupleType(), "{}"),
        arguments(sTupleType(sIntType()), "{Int}"),
        arguments(sTupleType(sIntType(), sBoolType()), "{Int,Bool}"),
        arguments(sTupleType(varA()), "{A}"),
        arguments(sTupleType(varA(), varB()), "{A,B}"),
        arguments(sBlobArrayT(), "[Blob]"),
        arguments(sBoolArrayT(), "[Bool]"),
        arguments(sIntArrayT(), "[Int]"),
        arguments(sStringArrayT(), "[String]"),
        arguments(sArrayType(sTupleType()), "[{}]"),
        arguments(sArrayType(sTupleType(sIntType())), "[{Int}]"),
        arguments(sArrayType(sTupleType(sIntType(), sBoolType())), "[{Int,Bool}]"),
        arguments(sArrayType(sTupleType(varA())), "[{A}]"),
        arguments(sArrayType(sTupleType(varA(), varB())), "[{A,B}]"),
        arguments(sArrayType(sStructType("MyStruct", nlist())), "[MyStruct]"),
        arguments(
            sArrayType(sStructType("MyStruct", nlist(sSig(sIntType(), "field")))), "[MyStruct]"),
        arguments(sVarAArrayT(), "[A]"),
        arguments(sArrayType(sVarAArrayT()), "[[A]]"),
        arguments(sArrayType(sBlobArrayT()), "[[Blob]]"),
        arguments(sArrayType(sBoolArrayT()), "[[Bool]]"),
        arguments(sArrayType(sIntArrayT()), "[[Int]]"),
        arguments(sArrayType(sArrayType(sTupleType())), "[[{}]]"),
        arguments(sArrayType(sArrayType(sTupleType(sIntType()))), "[[{Int}]]"),
        arguments(sArrayType(sArrayType(sTupleType(sIntType(), sBoolType()))), "[[{Int,Bool}]]"),
        arguments(sArrayType(sArrayType(sTupleType(varA()))), "[[{A}]]"),
        arguments(sArrayType(sArrayType(sTupleType(varA(), varB()))), "[[{A,B}]]"),
        arguments(sArrayType(sArrayType(sStructType("MyStruct", nlist()))), "[[MyStruct]]"),
        arguments(
            sArrayType(sArrayType(sStructType("MyStruct", nlist(sSig(sIntType(), "filed"))))),
            "[[MyStruct]]"),
        arguments(sArrayType(sStringArrayT()), "[[String]]"),
        arguments(sFuncType(sVarAArrayT(), varA()), "([A])->A"),
        arguments(sFuncType(sVarAArrayT(), sStringType()), "([A])->String"),
        arguments(sFuncType(varA(), varA()), "(A)->A"),
        arguments(sStringFuncType(), "()->String"),
        arguments(sFuncType(sStringType(), sStringType()), "(String)->String"),
        arguments(sFuncType(sTupleType(sIntType()), sStringType()), "({Int})->String"),
        arguments(sInterfaceType(), "{}"),
        arguments(sInterfaceType(sSig(sIntType(), "field1")), "{Int field1}"),
        arguments(
            sInterfaceType(sSig(sIntType(), "field1"), sSig(sBlobType(), "field2")),
            "{Int field1,Blob field2}"));
  }

  @ParameterizedTest
  @MethodSource("vars_test_data")
  public void typeVars(SType type, org.smoothbuild.common.collect.Set<STypeVar> expected) {
    assertThat(type.typeVars()).isEqualTo(expected);
  }

  public static List<Arguments> vars_test_data() {
    return new STypeTest().vars_test_data_non_static();
  }

  public List<Arguments> vars_test_data_non_static() {
    return list(
        arguments(varA(), set(varA())),
        arguments(sBlobType(), set()),
        arguments(sBoolType(), set()),
        arguments(sIntType(), set()),
        arguments(sStringType(), set()),
        arguments(sTupleType(sIntType()), set()),
        arguments(sTupleType(varA(), varB()), set(varA(), varB())),
        arguments(sIntArrayT(), set()),
        arguments(sVarAArrayT(), set(varA())),
        arguments(sFuncType(sBoolType(), sBlobType()), set()),
        arguments(sFuncType(sBoolType(), varA()), set(varA())),
        arguments(sFuncType(varA(), sBlobType()), set(varA())),
        arguments(sFuncType(varB(), varA()), set(varA(), varB())),
        arguments(sStructType(sIntType()), set()),
        arguments(sStructType(sIntType(), varA()), set(varA())),
        arguments(sStructType(varB(), varA()), set(varA(), varB())),
        arguments(sInterfaceType(sIntType()), set()),
        arguments(sInterfaceType(sIntType(), varA()), set(varA())),
        arguments(sInterfaceType(varB(), varA()), set(varA(), varB())));
  }

  @ParameterizedTest
  @MethodSource("map_type_vars")
  public void map_type_vars(SType type, SType expected) {
    Function<STypeVar, SType> addPrefix = (STypeVar v) -> new STypeVar(typeName("X" + v.name()));
    assertThat(type.mapTypeVars(addPrefix)).isEqualTo(expected);
  }

  public static List<Arguments> map_type_vars() {
    return new STypeTest().map_type_vars_non_static();
  }

  public List<Arguments> map_type_vars_non_static() {
    return list(
        arguments(sBlobType(), sBlobType()),
        arguments(sBoolType(), sBoolType()),
        arguments(sIntType(), sIntType()),
        arguments(sStringType(), sStringType()),
        arguments(sVar("A"), sVar("XA")),
        arguments(sTupleType(sIntType()), sTupleType(sIntType())),
        arguments(sTupleType(varA(), varB()), sTupleType(sVar("XA"), sVar("XB"))),
        arguments(sTupleType(sTupleType(varA())), sTupleType(sTupleType(sVar("XA")))),
        arguments(sIntArrayT(), sIntArrayT()),
        arguments(sArrayType(sVar("A")), sArrayType(sVar("XA"))),
        arguments(sArrayType(sArrayType(sVar("A"))), sArrayType(sArrayType(sVar("XA")))),
        arguments(sFuncType(sBoolType(), sBlobType()), sFuncType(sBoolType(), sBlobType())),
        arguments(sFuncType(sBoolType(), sVar("A")), sFuncType(sBoolType(), sVar("XA"))),
        arguments(sFuncType(sVar("A"), sBlobType()), sFuncType(sVar("XA"), sBlobType())),
        arguments(sFuncType(sVarAFuncType()), sFuncType(sFuncType(sVar("XA")))),
        arguments(
            sFuncType(sFuncType(sVar("A"), sIntType()), sIntType()),
            sFuncType(sFuncType(sVar("XA"), sIntType()), sIntType())),
        arguments(sStructType("MyStruct", sIntType()), sStructType("MyStruct", sIntType())),
        arguments(sStructType(varA(), varB()), sStructType(sVar("XA"), sVar("XB"))),
        arguments(
            sStructType("S1", sStructType("S2", sVar("A"))),
            sStructType("S1", sStructType("S2", sVar("XA")))),
        arguments(sInterfaceType(sIntType()), sInterfaceType(sIntType())),
        arguments(sInterfaceType(varA(), varB()), sInterfaceType(sVar("XA"), sVar("XB"))),
        arguments(
            sInterfaceType(sInterfaceType(sVar("A"))), sInterfaceType(sInterfaceType(sVar("XA")))));
  }

  @ParameterizedTest
  @MethodSource
  void is_flexible_type_var(SType var, boolean isFlexible) {
    assertThat(var.isFlexibleTypeVar()).isEqualTo(isFlexible);
  }

  static List<Arguments> is_flexible_type_var() {
    return new STypeTest().non_static_is_flexible_var();
  }

  List<Arguments> non_static_is_flexible_var() {
    return list(
        arguments(sBlobType(), false),
        arguments(sBoolType(), false),
        arguments(sIntType(), false),
        arguments(sStringType(), false),
        arguments(sTupleType(sIntType()), false),
        arguments(sTupleType(varA(), varB()), false),
        arguments(sIntArrayT(), false),
        arguments(sVarAArrayT(), false),
        arguments(sFuncType(sBoolType(), sBlobType()), false),
        arguments(sFuncType(sBoolType(), varA()), false),
        arguments(sFuncType(varA(), sBlobType()), false),
        arguments(sFuncType(varB(), varA()), false),
        arguments(sStructType(sIntType()), false),
        arguments(sStructType(sIntType(), varA()), false),
        arguments(sStructType(varB(), varA()), false),
        arguments(sInterfaceType(sIntType()), false),
        arguments(sInterfaceType(sIntType(), varA()), false),
        arguments(sInterfaceType(varB(), varA()), false),
        arguments(var1(), true),
        arguments(var2(), true),
        arguments(var3(), true),
        arguments(sVar("TT~1"), false),
        arguments(varA(), false),
        arguments(varB(), false));
  }

  @Nested
  class _array {
    @ParameterizedTest
    @MethodSource("elemType_test_data")
    public void elemType(SType type) {
      var array = sArrayType(type);
      assertThat(array.elem()).isEqualTo(type);
    }

    public static List<Arguments> elemType_test_data() {
      return new STypeTest().elemType_test_data_non_static();
    }
  }

  public List<Arguments> elemType_test_data_non_static() {
    return list(
        arguments(sBlobType()),
        arguments(sBoolType()),
        arguments(sStringFuncType()),
        arguments(sIntType()),
        arguments(sStringType()),
        arguments(sStructType("MyStruct", nlist())),
        arguments(varA()),
        arguments(sBlobArrayT()),
        arguments(sBoolArrayT()),
        arguments(sArrayType(sStringFuncType())),
        arguments(sIntArrayT()),
        arguments(sStringArrayT()),
        arguments(sVarAArrayT()));
  }

  @Nested
  class _func {
    @ParameterizedTest
    @MethodSource("func_result_cases")
    public void func_result(SFuncType type, SType expected) {
      assertThat(type.result()).isEqualTo(expected);
    }

    public static List<Arguments> func_result_cases() {
      return new STypeTest().func_result_cases_non_static();
    }

    @ParameterizedTest
    @MethodSource("func_params_cases")
    public void func_params(SFuncType type, Object expected) {
      assertThat(type.params()).isEqualTo(expected);
    }

    public static List<Arguments> func_params_cases() {
      return new STypeTest().func_params_cases_non_static();
    }
  }

  public List<Arguments> func_params_cases_non_static() {
    return list(
        arguments(sIntFuncType(), sTupleType()),
        arguments(sFuncType(sBoolType(), sBlobType()), sTupleType(sBoolType())),
        arguments(
            sFuncType(sBoolType(), sIntType(), sBlobType()), sTupleType(sBoolType(), sIntType())));
  }

  public List<Arguments> func_result_cases_non_static() {
    return list(
        arguments(sIntFuncType(), sIntType()),
        arguments(sFuncType(sBoolType(), sBlobType()), sBlobType()),
        arguments(sFuncType(sBoolType(), sIntType(), sBlobType()), sBlobType()));
  }

  @Nested
  class _struct {
    @Test
    void without_fields_can_be_created() {
      sStructType("MyStruct", nlist());
    }

    @Test
    void struct_specifier() {
      var struct = sStructType("MyStruct", nlist());
      assertThat(struct.specifier()).isEqualTo("MyStruct");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void illegal_struct_name(String name) {
      assertCall(() -> sStructType(name, nlist())).throwsException(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("struct_fields_cases")
    public void struct_fields(SStructType struct, NList<SItemSig> expected) {
      assertThat(struct.fields()).isEqualTo(expected);
    }

    public static List<Arguments> struct_fields_cases() {
      return new STypeTest().struct_fields_cases_non_static();
    }
  }

  public List<Arguments> struct_fields_cases_non_static() {
    return list(
        arguments(sStructType("Person", nlist()), nlist()),
        arguments(
            sStructType("Person", nlist(sSig(sStringType(), "field"))),
            nlist(sSig(sStringType(), "field"))),
        arguments(
            sStructType("Person", nlist(sSig(sStringType(), "field"), sSig(sIntType(), "field2"))),
            nlist(sSig(sStringType(), "field"), sSig(sIntType(), "field2"))));
  }

  @Test
  void equality() {
    EqualsTester equalsTester = new EqualsTester();
    List<SType> types = list(
        sBlobType(),
        sBoolType(),
        sIntType(),
        sStringType(),
        sTupleType(),
        sTupleType(sIntType(), sBoolType()),
        sStructType("MyStruct", nlist()),
        sStructType("MyStruct", nlist(sSig(sIntType(), "field"))),
        varA(),
        varB(),
        varC(),
        sBlobFuncType(),
        sStringFuncType(),
        sFuncType(sStringType(), sBlobType()),
        sFuncType(sBlobType(), sBlobType()));

    for (SType type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(sTupleType(type), sTupleType(type));
      equalsTester.addEqualityGroup(sArrayType(type), sArrayType(type));
      equalsTester.addEqualityGroup(sArrayType(sArrayType(type)), sArrayType(sArrayType(type)));
    }
    equalsTester.testEquals();
  }

  @Nested
  class _tuple {
    @ParameterizedTest
    @MethodSource("tuple_items_cases")
    public void func_params(STupleType type, Object expected) {
      assertThat(type.elements()).isEqualTo(expected);
    }

    public static List<Arguments> tuple_items_cases() {
      return new STypeTest().tuple_items_cases();
    }
  }

  public List<Arguments> tuple_items_cases() {
    return list(
        arguments(sTupleType(), list()),
        arguments(sTupleType(sBoolType()), list(sBoolType())),
        arguments(sTupleType(sBoolType(), sIntType()), list(sBoolType(), sIntType())));
  }
}
