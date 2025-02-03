package org.smoothbuild.compilerfrontend.lang.name;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Result.err;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.parseReference;
import static org.smoothbuild.compilerfrontend.lang.name.Name.parseReferenceableName;
import static org.smoothbuild.compilerfrontend.lang.name.Name.parseStructName;
import static org.smoothbuild.compilerfrontend.lang.name.Name.parseTypeVarName;
import static org.smoothbuild.compilerfrontend.lang.name.Name.referenceableName;
import static org.smoothbuild.compilerfrontend.lang.name.Name.structName;
import static org.smoothbuild.compilerfrontend.lang.name.Name.typeVarName;

import com.google.common.testing.EqualsTester;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;

public class IdTest {
  @Nested
  class _name {
    @Nested
    class _parse_referenceable_name {
      @ParameterizedTest
      @MethodSource
      void legal(String string) {
        var name = parseReferenceableName(string).ok();
        assertThat(name.toString()).isEqualTo(string);
      }

      static List<Arguments> legal() {
        return legal_referenceable_names();
      }

      @ParameterizedTest
      @MethodSource
      void illegal(String string, String expectedErrorMessage) {
        assertThat(parseReferenceableName(string)).isEqualTo(err(expectedErrorMessage));
      }

      public static List<Arguments> illegal() {
        return illegal_referenceable_names();
      }
    }

    @Nested
    class _referenceable_name {
      @ParameterizedTest
      @MethodSource
      void legal(String string) {
        var name = referenceableName(string);
        assertThat(name.toString()).isEqualTo(string);
      }

      static List<Arguments> legal() {
        return legal_referenceable_names();
      }

      @ParameterizedTest
      @MethodSource
      void illegal(String string, String expectedErrorMessage) {
        assertCall(() -> referenceableName(string))
            .throwsException(new IllegalArgumentException(
                "Illegal referenceable name. " + expectedErrorMessage));
      }

      public static List<Arguments> illegal() {
        return illegal_referenceable_names();
      }
    }

    static List<Arguments> legal_referenceable_names() {
      return list(
          arguments("name"),
          arguments("name_"),
          arguments("a_name"),
          arguments("name123"),
          arguments("name~"),
          arguments("name~name"));
    }

    static List<Arguments> illegal_referenceable_names() {
      return list(
          arguments("", "It must not be empty string."),
          arguments("a^", "It must not contain '^' character."),
          arguments("^a", "It must not contain '^' character."),
          arguments("ab c", "It must not contain ' ' character."),
          arguments("_", "`_` is reserved for future use."),
          arguments("_abc", "It must start with lowercase letter."),
          arguments("3", "It must start with lowercase letter."),
          arguments("123", "It must start with lowercase letter."),
          arguments("3abc", "It must start with lowercase letter."),
          arguments("Abc", "It must start with lowercase letter."),
          arguments("~", "It must start with lowercase letter."),
          arguments("~name", "It must start with lowercase letter."),
          arguments("a:", "It must not contain ':' character."),
          arguments(":a", "It must not contain ':' character."),
          arguments(":", "It must not contain ':' character."),
          arguments("a:a", "It must not contain ':' character."));
    }

    @Nested
    class _parse_struct_name {
      @ParameterizedTest
      @MethodSource
      void legal(String string) {
        var name = parseStructName(string).ok();
        assertThat(name.toString()).isEqualTo(string);
      }

      static List<Arguments> legal() {
        return legal_struct_names();
      }

      @ParameterizedTest
      @MethodSource
      void illegal(String string, String expectedErrorMessage) {
        assertThat(parseStructName(string)).isEqualTo(err(expectedErrorMessage));
      }

      static List<Arguments> illegal() {
        return illegal_struct_names();
      }
    }

    @Nested
    class _struct_name {
      @ParameterizedTest
      @MethodSource
      void legal(String string) {
        var name = structName(string);
        assertThat(name.toString()).isEqualTo(string);
      }

      static List<Arguments> legal() {
        return legal_struct_names();
      }

      @ParameterizedTest
      @MethodSource
      void illegal(String string, String expectedErrorMessage) {
        assertCall(() -> structName(string))
            .throwsException(
                new IllegalArgumentException("Illegal struct name. " + expectedErrorMessage));
      }

      static List<Arguments> illegal() {
        return illegal_struct_names();
      }
    }

    static List<Arguments> legal_struct_names() {
      return list(
          arguments("Name"),
          arguments("Name_"),
          arguments("A_name"),
          arguments("Name123"),
          arguments("Name~"),
          arguments("Name~1"));
    }

    static List<Arguments> illegal_struct_names() {
      return list(
          arguments("", "It must not be empty string."),
          arguments("Abc^", "It must not contain '^' character."),
          arguments("^Abc", "It must not contain '^' character."),
          arguments("Abc:", "It must not contain ':' character."),
          arguments(":Abc", "It must not contain ':' character."),
          arguments("Abc:Abc", "It must not contain ':' character."),
          arguments(":", "It must not contain ':' character."),
          arguments("Ab c", "It must not contain ' ' character."),
          arguments("_", "`_` is reserved for future use."),
          arguments("_Abc", "It must start with uppercase letter."),
          arguments("A", "All-uppercase names are reserved for type variables."),
          arguments("ABC", "All-uppercase names are reserved for type variables."),
          arguments("3", "It must start with uppercase letter."),
          arguments("123", "It must start with uppercase letter."),
          arguments("3abc", "It must start with uppercase letter."),
          arguments("~abc", "It must start with uppercase letter."),
          arguments("~", "It must start with uppercase letter."),
          arguments("abc", "It must start with uppercase letter."));
    }

    @Nested
    class _parse_type_var_name {
      @ParameterizedTest
      @MethodSource
      void legal(String string) {
        var name = parseTypeVarName(string).ok();
        assertThat(name.toString()).isEqualTo(string);
      }

      static List<Arguments> legal() {
        return legal_type_var_names();
      }

      @ParameterizedTest
      @MethodSource
      void illegal(String string, String expectedErrorMessage) {
        assertThat(parseTypeVarName(string)).isEqualTo(err(expectedErrorMessage));
      }

      static List<Arguments> illegal() {
        return illegal_type_var_names();
      }
    }

    @Nested
    class _type_var_name {
      @ParameterizedTest
      @MethodSource
      void legal(String string) {
        var name = typeVarName(string);
        assertThat(name.toString()).isEqualTo(string);
      }

      static List<Arguments> legal() {
        return legal_type_var_names();
      }

      @ParameterizedTest
      @MethodSource
      void illegal(String string, String expectedErrorMessage) {
        assertCall(() -> typeVarName(string))
            .throwsException(
                new IllegalArgumentException("Illegal type var name. " + expectedErrorMessage));
      }

      static List<Arguments> illegal() {
        return illegal_type_var_names();
      }
    }

    static List<Arguments> legal_type_var_names() {
      return list(
          arguments("A"),
          arguments("B"),
          arguments("C"),
          arguments("X"),
          arguments("ABC"),
          arguments("ABCDEF"));
    }

    static List<Arguments> illegal_type_var_names() {
      return list(
          arguments("", "It must not be empty string."),
          arguments("A^", "It must not contain '^' character."),
          arguments("^A", "It must not contain '^' character."),
          arguments("A:", "It must not contain ':' character."),
          arguments(":A", "It must not contain ':' character."),
          arguments("A:A", "It must not contain ':' character."),
          arguments(":", "It must not contain ':' character."),
          arguments("A B", "It must not contain ' ' character."),
          arguments("_", "`_` is reserved for future use."),
          arguments("_A", "Type variable must be UPPERCASE."),
          arguments("3", "Type variable must be UPPERCASE."),
          arguments("123", "Type variable must be UPPERCASE."),
          arguments("3ABC", "Type variable must be UPPERCASE."),
          arguments("~ABC", "Type variable must be UPPERCASE."),
          arguments("~", "Type variable must be UPPERCASE."));
    }
  }

  @Nested
  class _fqn {
    @Nested
    class _parse_reference {
      @ParameterizedTest
      @MethodSource
      void legal(String string) {
        var name = parseReference(string).ok();
        assertThat(name.toString()).isEqualTo(string);
      }

      static List<Arguments> legal() {
        return legal_references();
      }

      @ParameterizedTest
      @MethodSource
      void illegal(String string, String expectedErrorMessage) {
        assertThat(parseReference(string)).isEqualTo(err(expectedErrorMessage));
      }

      static List<Arguments> illegal() {
        return illegal_references();
      }
    }

    @Nested
    class _fqn_ {
      @ParameterizedTest
      @MethodSource
      void legal(String string) {
        assertThat(fqn(string).toString()).isEqualTo(string);
      }

      static List<Arguments> legal() {
        return legal_references();
      }

      @ParameterizedTest
      @MethodSource
      void illegal(String string, String expectedErrorMessage) {
        assertCall(() -> fqn(string))
            .throwsException(
                new IllegalArgumentException("Illegal reference. " + expectedErrorMessage));
      }

      static List<Arguments> illegal() {
        return illegal_references();
      }
    }

    private static List<Arguments> legal_references() {
      return list(
          arguments("name"),
          arguments("name_"),
          arguments("a_name"),
          arguments("a3"),
          arguments("name123"),
          arguments("_"),
          arguments("namespace:_"),
          arguments("namespace:_:name"),
          arguments("namespace:name"),
          arguments("name_space:name"),
          arguments("name_:name_"),
          arguments("namespace:a_name"),
          arguments("name123:name456"),
          arguments("Struct:name"),
          arguments("name:Struct"),
          arguments("_abc"),
          arguments("Abc"),
          arguments("~"),
          arguments("~1"),
          arguments("name~"),
          arguments("name~:~"));
    }

    static List<Arguments> illegal_references() {
      return list(
          arguments("", "It must not be empty string."),
          arguments("a^", "It must not contain '^' character."),
          arguments("^a", "It must not contain '^' character."),
          arguments("ab c", "It must not contain ' ' character."),
          arguments("a:", "It must not end with ':' character."),
          arguments("a::", "It must not end with ':' character."),
          arguments(":a", "It must not start with ':' character."),
          arguments("::a", "It must not start with ':' character."),
          arguments("a::b", "It must not contain \"::\" substring."),
          arguments("a:::b", "It must not contain \"::\" substring."),
          arguments("3", "It must not contain part that starts with digit."),
          arguments("123", "It must not contain part that starts with digit."),
          arguments("3abc", "It must not contain part that starts with digit."),
          arguments("name:3abc", "It must not contain part that starts with digit."));
    }
  }

  @Test
  void append() {
    var name1 = parseReferenceableName("abc").ok();
    var name2 = parseReferenceableName("def").ok();
    assertThat(name1.append(name2)).isEqualTo(parseReference("abc:def").ok());
  }

  @ParameterizedTest
  @MethodSource
  void parts(Id id, List<Name> expected) {
    assertThat(id.parts()).isEqualTo(expected);
  }

  public static List<Arguments> parts() {
    return list(
        arguments(fqn("abc"), list(name("abc"))),
        arguments(fqn("abc:def"), list(name("abc"), name("def"))),
        arguments(fqn("abc:def:ghi"), list(name("abc"), name("def"), name("ghi"))),
        arguments(fqn("abc:_"), list(name("abc"), name("_"))));
  }

  @ParameterizedTest
  @MethodSource
  public void compare_to(Id left, Id right, int result) {
    var comparison = left.compareTo(right);
    assertThat(Math.clamp(comparison, -1, 1)).isEqualTo(result);
  }

  public static Stream<Arguments> compare_to() {
    return Stream.of(
        arguments(fqn("abc"), referenceableName("abc"), 0),
        arguments(fqn("abc"), fqn("def"), -1),
        arguments(referenceableName("abc"), fqn("def"), -1),
        arguments(fqn("abc"), referenceableName("def"), -1),
        arguments(fqn("abc"), fqn("abcd"), -1),
        arguments(fqn("abc:def"), fqn("abc:ghi"), -1),
        arguments(fqn("abc:def"), fqn("abcd:ef"), -1),
        arguments(fqn("abc"), fqn("abc:def"), -1),
        arguments(referenceableName("abc"), fqn("abc:ghi"), -1));
  }

  @Test
  void equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(parseReference("abc"), parseReference("abc"))
        .addEqualityGroup(parseReference("abc1"), parseReference("abc1"))
        .addEqualityGroup(parseReference("def"), parseReference("def"))
        .addEqualityGroup(parseReference("abc:def"), parseReference("abc:def"))
        .addEqualityGroup(parseReference("abc:def:jkl"), parseReference("abc:def:jkl"))
        .testEquals();
  }

  @Test
  void to_source_code() {
    assertThat(fqn("abc:def:ghi").toSourceCode()).isEqualTo("abc:def:ghi");
    assertThat(name("abc").toSourceCode()).isEqualTo("abc");
  }

  @Test
  void to_string() {
    assertThat(parseReferenceableName("abc").ok().toString()).isEqualTo("abc");
    assertThat(parseStructName("Struct").ok().toString()).isEqualTo("Struct");
    assertThat(parseReference("name:abc").ok().toString()).isEqualTo("name:abc");
  }

  private static Name name(String name) {
    return new Name(name);
  }
}
