package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.lang.define.SItemSig.itemSigS;
import static org.smoothbuild.compilerfrontend.lang.type.SVarSet.varSetS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sArrayType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBlobType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBoolType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sSig;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStringType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sStructType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sVar;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varA;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varB;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.varC;

import com.google.common.testing.EqualsTester;
import java.util.function.Function;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.testing.TestingSExpression;

public class STypeTest {
  @Test
  public void verify_all_base_types_are_tested() {
    assertThat(STypes.baseTypes()).hasSize(4);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void name(SType type, String name) {
    assertThat(type.name()).isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(SType type, String name) {
    assertThat(type.q()).isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("to_string")
  public void to_string(SType type, String name) {
    assertThat(type.toString()).isEqualTo(name);
  }

  public static List<Arguments> names() {
    return name_or_to_string()
        .appendAll(list(
            arguments(sStructType("MyStruct", nlist()), "MyStruct"),
            arguments(sStructType("MyStruct", nlist(itemSigS(sIntType(), "field"))), "MyStruct")));
  }

  public static List<Arguments> to_string() {
    return name_or_to_string()
        .appendAll(list(
            arguments(sStructType("MyStruct", nlist()), "MyStruct()"),
            arguments(
                sStructType("MyStruct", nlist(itemSigS(sIntType(), "field"))),
                "MyStruct(Int field)")));
  }

  public static List<Arguments> name_or_to_string() {
    return list(
        arguments(sBlobType(), "Blob"),
        arguments(sBoolType(), "Bool"),
        arguments(sIntType(), "Int"),
        arguments(sStringType(), "String"),
        arguments(varA(), "A"),
        arguments(TestingSExpression.sTupleType(), "()"),
        arguments(TestingSExpression.sTupleType(sIntType()), "(Int)"),
        arguments(TestingSExpression.sTupleType(sIntType(), sBoolType()), "(Int,Bool)"),
        arguments(TestingSExpression.sTupleType(varA()), "(A)"),
        arguments(TestingSExpression.sTupleType(varA(), varB()), "(A,B)"),
        arguments(sArrayType(sBlobType()), "[Blob]"),
        arguments(sArrayType(sBoolType()), "[Bool]"),
        arguments(sArrayType(sIntType()), "[Int]"),
        arguments(sArrayType(sStringType()), "[String]"),
        arguments(sArrayType(TestingSExpression.sTupleType()), "[()]"),
        arguments(sArrayType(TestingSExpression.sTupleType(sIntType())), "[(Int)]"),
        arguments(
            sArrayType(TestingSExpression.sTupleType(sIntType(), sBoolType())), "[(Int,Bool)]"),
        arguments(sArrayType(TestingSExpression.sTupleType(varA())), "[(A)]"),
        arguments(sArrayType(TestingSExpression.sTupleType(varA(), varB())), "[(A,B)]"),
        arguments(sArrayType(sStructType("MyStruct", nlist())), "[MyStruct]"),
        arguments(
            sArrayType(sStructType("MyStruct", nlist(itemSigS(sIntType(), "field")))),
            "[MyStruct]"),
        arguments(sArrayType(varA()), "[A]"),
        arguments(sArrayType(sArrayType(varA())), "[[A]]"),
        arguments(sArrayType(sArrayType(sBlobType())), "[[Blob]]"),
        arguments(sArrayType(sArrayType(sBoolType())), "[[Bool]]"),
        arguments(sArrayType(sArrayType(sIntType())), "[[Int]]"),
        arguments(sArrayType(sArrayType(TestingSExpression.sTupleType())), "[[()]]"),
        arguments(sArrayType(sArrayType(TestingSExpression.sTupleType(sIntType()))), "[[(Int)]]"),
        arguments(
            sArrayType(sArrayType(TestingSExpression.sTupleType(sIntType(), sBoolType()))),
            "[[(Int,Bool)]]"),
        arguments(sArrayType(sArrayType(TestingSExpression.sTupleType(varA()))), "[[(A)]]"),
        arguments(
            sArrayType(sArrayType(TestingSExpression.sTupleType(varA(), varB()))), "[[(A,B)]]"),
        arguments(sArrayType(sArrayType(sStructType("MyStruct", nlist()))), "[[MyStruct]]"),
        arguments(
            sArrayType(sArrayType(sStructType("MyStruct", nlist(itemSigS(sIntType(), "filed"))))),
            "[[MyStruct]]"),
        arguments(sArrayType(sArrayType(sStringType())), "[[String]]"),
        arguments(TestingSExpression.sFuncType(sArrayType(varA()), varA()), "([A])->A"),
        arguments(TestingSExpression.sFuncType(sArrayType(varA()), sStringType()), "([A])->String"),
        arguments(TestingSExpression.sFuncType(varA(), varA()), "(A)->A"),
        arguments(TestingSExpression.sFuncType(sStringType()), "()->String"),
        arguments(TestingSExpression.sFuncType(sStringType(), sStringType()), "(String)->String"),
        arguments(
            TestingSExpression.sFuncType(TestingSExpression.sTupleType(sIntType()), sStringType()),
            "((Int))->String"),
        arguments(TestingSExpression.sInterfaceType(), "()"),
        arguments(TestingSExpression.sInterfaceType(sSig(sIntType(), "field1")), "(Int field1)"),
        arguments(
            TestingSExpression.sInterfaceType(
                sSig(sIntType(), "field1"), sSig(sBlobType(), "field2")),
            "(Int field1,Blob field2)"));
  }

  @ParameterizedTest
  @MethodSource("vars_test_data")
  public void vars(SType type, SVarSet expected) {
    assertThat(type.vars()).isEqualTo(expected);
  }

  public static List<Arguments> vars_test_data() {
    return list(
        arguments(sBlobType(), varSetS()),
        arguments(sBoolType(), varSetS()),
        arguments(sIntType(), varSetS()),
        arguments(sStringType(), varSetS()),
        arguments(TestingSExpression.sTupleType(sIntType()), varSetS()),
        arguments(TestingSExpression.sTupleType(varA(), varB()), varSetS(varA(), varB())),
        arguments(sArrayType(sIntType()), varSetS()),
        arguments(sArrayType(varA()), varSetS(varA())),
        arguments(TestingSExpression.sFuncType(sBoolType(), sBlobType()), varSetS()),
        arguments(TestingSExpression.sFuncType(sBoolType(), varA()), varSetS(varA())),
        arguments(TestingSExpression.sFuncType(varA(), sBlobType()), varSetS(varA())),
        arguments(TestingSExpression.sFuncType(varB(), varA()), varSetS(varA(), varB())),
        arguments(TestingSExpression.sStructType(sIntType()), varSetS()),
        arguments(TestingSExpression.sStructType(sIntType(), varA()), varSetS(varA())),
        arguments(TestingSExpression.sStructType(varB(), varA()), varSetS(varA(), varB())),
        arguments(TestingSExpression.sInterfaceType(sIntType()), varSetS()),
        arguments(TestingSExpression.sInterfaceType(sIntType(), varA()), varSetS(varA())),
        arguments(TestingSExpression.sInterfaceType(varB(), varA()), varSetS(varA(), varB())));
  }

  @ParameterizedTest
  @MethodSource("map_vars")
  public void map_vars(SType type, SType expected) {
    Function<SVar, SType> addPrefix = (SVar v) -> new SVar("prefix." + v.name());
    assertThat(type.mapVars(addPrefix)).isEqualTo(expected);
  }

  public static List<Arguments> map_vars() {
    return list(
        arguments(sBlobType(), sBlobType()),
        arguments(sBoolType(), sBoolType()),
        arguments(sIntType(), sIntType()),
        arguments(sStringType(), sStringType()),
        arguments(sVar("A"), sVar("prefix.A")),
        arguments(sVar("pre.A"), sVar("prefix.pre.A")),
        arguments(
            TestingSExpression.sTupleType(sIntType()), TestingSExpression.sTupleType(sIntType())),
        arguments(
            TestingSExpression.sTupleType(varA(), varB()),
            TestingSExpression.sTupleType(sVar("prefix.A"), sVar("prefix.B"))),
        arguments(
            TestingSExpression.sTupleType(TestingSExpression.sTupleType(varA())),
            TestingSExpression.sTupleType(TestingSExpression.sTupleType(sVar("prefix.A")))),
        arguments(sArrayType(sIntType()), sArrayType(sIntType())),
        arguments(sArrayType(sVar("A")), sArrayType(sVar("prefix.A"))),
        arguments(sArrayType(sVar("p.A")), sArrayType(sVar("prefix.p.A"))),
        arguments(sArrayType(sArrayType(sVar("A"))), sArrayType(sArrayType(sVar("prefix.A")))),
        arguments(
            TestingSExpression.sFuncType(sBoolType(), sBlobType()),
            TestingSExpression.sFuncType(sBoolType(), sBlobType())),
        arguments(
            TestingSExpression.sFuncType(sBoolType(), sVar("A")),
            TestingSExpression.sFuncType(sBoolType(), sVar("prefix.A"))),
        arguments(
            TestingSExpression.sFuncType(sVar("A"), sBlobType()),
            TestingSExpression.sFuncType(sVar("prefix.A"), sBlobType())),
        arguments(
            TestingSExpression.sFuncType(sBoolType(), sVar("p.A")),
            TestingSExpression.sFuncType(sBoolType(), sVar("prefix.p.A"))),
        arguments(
            TestingSExpression.sFuncType(sVar("p.A"), sBlobType()),
            TestingSExpression.sFuncType(sVar("prefix.p.A"), sBlobType())),
        arguments(
            TestingSExpression.sFuncType(TestingSExpression.sFuncType(sVar("A"))),
            TestingSExpression.sFuncType(TestingSExpression.sFuncType(sVar("prefix.A")))),
        arguments(
            TestingSExpression.sFuncType(
                TestingSExpression.sFuncType(sVar("A"), sIntType()), sIntType()),
            TestingSExpression.sFuncType(
                TestingSExpression.sFuncType(sVar("prefix.A"), sIntType()), sIntType())),
        arguments(
            TestingSExpression.sStructType("MyStruct", sIntType()),
            TestingSExpression.sStructType("MyStruct", sIntType())),
        arguments(
            TestingSExpression.sStructType(varA(), varB()),
            TestingSExpression.sStructType(sVar("prefix.A"), sVar("prefix.B"))),
        arguments(
            TestingSExpression.sStructType("S1", TestingSExpression.sStructType("S2", sVar("A"))),
            TestingSExpression.sStructType(
                "S1", TestingSExpression.sStructType("S2", sVar("prefix.A")))),
        arguments(
            TestingSExpression.sInterfaceType(sIntType()),
            TestingSExpression.sInterfaceType(sIntType())),
        arguments(
            TestingSExpression.sInterfaceType(varA(), varB()),
            TestingSExpression.sInterfaceType(sVar("prefix.A"), sVar("prefix.B"))),
        arguments(
            TestingSExpression.sInterfaceType(TestingSExpression.sInterfaceType(sVar("A"))),
            TestingSExpression.sInterfaceType(
                TestingSExpression.sInterfaceType(sVar("prefix.A")))));
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
      return list(
          arguments(sBlobType()),
          arguments(sBoolType()),
          arguments(TestingSExpression.sFuncType(sStringType())),
          arguments(sIntType()),
          arguments(sStringType()),
          arguments(sStructType("MyStruct", nlist())),
          arguments(varA()),
          arguments(sArrayType(sBlobType())),
          arguments(sArrayType(sBoolType())),
          arguments(sArrayType(TestingSExpression.sFuncType(sStringType()))),
          arguments(sArrayType(sIntType())),
          arguments(sArrayType(sStringType())),
          arguments(sArrayType(varA())));
    }
  }

  @Nested
  class _func {
    @ParameterizedTest
    @MethodSource("func_result_cases")
    public void func_result(SFuncType type, SType expected) {
      assertThat(type.result()).isEqualTo(expected);
    }

    public static List<Arguments> func_result_cases() {
      return list(
          arguments(TestingSExpression.sFuncType(sIntType()), sIntType()),
          arguments(TestingSExpression.sFuncType(sBoolType(), sBlobType()), sBlobType()),
          arguments(
              TestingSExpression.sFuncType(sBoolType(), sIntType(), sBlobType()), sBlobType()));
    }

    @ParameterizedTest
    @MethodSource("func_params_cases")
    public void func_params(SFuncType type, Object expected) {
      assertThat(type.params()).isEqualTo(expected);
    }

    public static List<Arguments> func_params_cases() {
      return list(
          arguments(TestingSExpression.sFuncType(sIntType()), TestingSExpression.sTupleType()),
          arguments(
              TestingSExpression.sFuncType(sBoolType(), sBlobType()),
              TestingSExpression.sTupleType(sBoolType())),
          arguments(
              TestingSExpression.sFuncType(sBoolType(), sIntType(), sBlobType()),
              TestingSExpression.sTupleType(sBoolType(), sIntType())));
    }
  }

  @Nested
  class _struct {
    @Test
    public void without_fields_can_be_created() {
      sStructType("MyStruct", nlist());
    }

    @Test
    public void struct_name() {
      var struct = sStructType("MyStruct", nlist());
      assertThat(struct.name()).isEqualTo("MyStruct");
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
      return list(
          arguments(sStructType("Person", nlist()), nlist()),
          arguments(
              sStructType("Person", nlist(itemSigS(sStringType(), "field"))),
              nlist(itemSigS(sStringType(), "field"))),
          arguments(
              sStructType(
                  "Person",
                  nlist(itemSigS(sStringType(), "field"), itemSigS(sIntType(), "field2"))),
              nlist(itemSigS(sStringType(), "field"), itemSigS(sIntType(), "field2"))));
    }
  }

  @Test
  public void equality() {
    EqualsTester equalsTester = new EqualsTester();
    List<SType> types = list(
        sBlobType(),
        sBoolType(),
        sIntType(),
        sStringType(),
        TestingSExpression.sTupleType(),
        TestingSExpression.sTupleType(sIntType(), sBoolType()),
        sStructType("MyStruct", nlist()),
        sStructType("MyStruct", nlist(itemSigS(sIntType(), "field"))),
        varA(),
        varB(),
        varC(),
        TestingSExpression.sFuncType(sBlobType()),
        TestingSExpression.sFuncType(sStringType()),
        TestingSExpression.sFuncType(sStringType(), sBlobType()),
        TestingSExpression.sFuncType(sBlobType(), sBlobType()));

    for (SType type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(
          TestingSExpression.sTupleType(type), TestingSExpression.sTupleType(type));
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
      return list(
          arguments(TestingSExpression.sTupleType(), list()),
          arguments(TestingSExpression.sTupleType(sBoolType()), list(sBoolType())),
          arguments(
              TestingSExpression.sTupleType(sBoolType(), sIntType()),
              list(sBoolType(), sIntType())));
    }
  }

  @Nested
  class _temp_var {
    @Test
    public void is_older_than_1_1() {
      assertThat(new STempVar("1").isOlderThan(new STempVar("1"))).isFalse();
    }

    @Test
    public void is_older_than_1_2() {
      assertThat(new STempVar("1").isOlderThan(new STempVar("2"))).isTrue();
    }

    @Test
    public void is_older_than_1_10() {
      assertThat(new STempVar("1").isOlderThan(new STempVar("10"))).isTrue();
    }

    @Test
    public void is_older_than_2_1() {
      assertThat(new STempVar("2").isOlderThan(new STempVar("1"))).isFalse();
    }
  }
}
