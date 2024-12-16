package org.smoothbuild.compilerfrontend.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Result.error;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.lang.base.Fqn.fqn;
import static org.smoothbuild.compilerfrontend.lang.base.Fqn.parseReference;
import static org.smoothbuild.compilerfrontend.lang.base.Name.parseReferenceableName;
import static org.smoothbuild.compilerfrontend.lang.base.Name.parseStructName;
import static org.smoothbuild.compilerfrontend.lang.base.Name.referenceableName;
import static org.smoothbuild.compilerfrontend.lang.base.Name.structName;

import com.google.common.testing.EqualsTester;
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
        var name = parseReferenceableName(string).right();
        assertThat(name.toString()).isEqualTo(string);
      }

      static List<Arguments> legal() {
        return legal_referenceable_names();
      }

      @ParameterizedTest
      @MethodSource
      void illegal(String string, String expectedErrorMessage) {
        assertThat(parseReferenceableName(string)).isEqualTo(error(expectedErrorMessage));
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
      return list(arguments("name"), arguments("name_"), arguments("a_name"), arguments("name123"));
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
        var name = parseStructName(string).right();
        assertThat(name.toString()).isEqualTo(string);
      }

      static List<Arguments> legal() {
        return legal_struct_names();
      }

      @ParameterizedTest
      @MethodSource
      void illegal(String string, String expectedErrorMessage) {
        assertThat(parseStructName(string)).isEqualTo(error(expectedErrorMessage));
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
      return list(arguments("Name"), arguments("Name_"), arguments("A_name"), arguments("Name123"));
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
          arguments("abc", "It must start with uppercase letter."));
    }
  }

  @Nested
  class _fqn {
    @Nested
    class _parse_reference {
      @ParameterizedTest
      @MethodSource
      void legal(String string) {
        var name = parseReference(string).right();
        assertThat(name.toString()).isEqualTo(string);
      }

      static List<Arguments> legal() {
        return legal_references();
      }

      @ParameterizedTest
      @MethodSource
      void illegal(String string, String expectedErrorMessage) {
        assertThat(parseReference(string)).isEqualTo(error(expectedErrorMessage));
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
          arguments("Abc"));
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
    var name1 = parseReferenceableName("abc").right();
    var name2 = parseReferenceableName("def").right();
    assertThat(name1.append(name2)).isEqualTo(parseReference("abc:def").right());
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
  void to_string() {
    assertThat(parseReferenceableName("abc").right().toString()).isEqualTo("abc");
    assertThat(parseStructName("Struct").right().toString()).isEqualTo("Struct");
    assertThat(parseReference("name:abc").right().toString()).isEqualTo("name:abc");
  }

  private static Name name(String name) {
    return new Name(name);
  }
}
