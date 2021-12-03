package org.smoothbuild.lang.base.type;

import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.stream.Collectors.toList;
import static okio.ByteString.encodeString;
import static org.smoothbuild.lang.base.define.ItemSigS.itemSigS;
import static org.smoothbuild.lang.base.type.TestingTypesS.struct;
import static org.smoothbuild.lang.base.type.TestingTypesS.var;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import org.smoothbuild.lang.base.define.ItemSigS;
import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.base.type.impl.NothingTS;
import org.smoothbuild.lang.base.type.impl.TypeS;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

public class TestedType {
  private static final AtomicLong UNIQUE_IDENTIFIER = new AtomicLong();

  public static final TestedType A = new TestedType(
      var("A"),
      null,
      null
  );
  public static final TestedType B = new TestedType(
      var("B"),
      null,
      null
  );
  public static final TestedType ANY = new TestedType(
      TestingTypesS.ANY,
      "createAny()",
      null,
      Set.of(),
      Set.of("Any createAny() = \"abc\";"));
  public static final TestedType BLOB = new TestedType(
      TestingTypesS.BLOB,
      "0x" + encodeString("xyz", US_ASCII).hex(),
      "xyz"
  );
  public static final TestedType BOOL = new TestedType(
      TestingTypesS.BOOL,
      "true",
      new String(new byte[] {1})
  );
  public static final TestedType INT = new TestedType(
      TestingTypesS.INT,
      "123",
      new String(new byte[] {123})
  );
  public static final TestedType NOTHING = new TestedType(
      TestingTypesS.NOTHING,
      "reportError(\"e\")",
      null,
      Set.of(),
      Set.of("@Native(\"impl\") Nothing reportError(String message);"));
  public static final TestedType STRING = new TestedType(
      TestingTypesS.STRING,
      "\"abc\"",
      "abc"
  );
  public static final TestedType STRUCT = new TestedType(
      struct("Person", nList(itemSigS("name", TestingTypesS.STRING))),
      "person(\"John\")",
      null,
      Set.of("Person{ String name }"),
      Set.of("Person{ String name }")
  );
  public static final TestedType STRUCT_WITH_BLOB = new TestedType(
      struct("Data", nList(itemSigS("value", TestingTypesS.BLOB))),
      "data(0xAB)",
      null,
      Set.of("Data{ Blob value }"),
      Set.of("Data{ Blob value }")
  );
  public static final TestedType STRUCT_WITH_BOOL = new TestedType(
      struct("Flag", nList(itemSigS("value", TestingTypesS.BOOL))),
      "flag(true)",
      null,
      Set.of("Flag{ Bool value }"),
      Set.of("Flag{ Bool value }")
  );

  public static final ImmutableList<TestedType> ELEMENTARY_TYPES = list(
      ANY,
      BLOB,
      BOOL,
      NOTHING,
      STRING,
      STRUCT,
      A);

  public static final List<TestedType> TESTED_MONOTYPES = list(
      BLOB,
      BOOL,
      NOTHING,
      STRING,
      STRUCT,
      a(BLOB),
      a(NOTHING),
      a(STRUCT),
      a(a(BLOB)),
      a(a(NOTHING)),
      a(a(STRUCT)),
      f(BLOB),
      f(NOTHING),
      f(f(BLOB)),
      f(f(NOTHING)),
      f(BLOB, BOOL),
      f(BLOB, NOTHING),
      f(BLOB, f(BOOL)),
      f(BLOB, f(NOTHING))
  );

  /**
   * Polytypes that can be used in any place. Each var in such a polytype occurs more than
   * once.
   */
  public static final List<TestedType> TESTED_VALID_POLYTYPES = list(
      a(f(A, A)),
      a(a(f(A, A))),
      f(A, A),
      f(A, a(A)),
      f(f(A), f(A)),
      f(f(A), a(A)),
      f(f(A, A)),
      f(BOOL, f(A, A))
  );

  /**
   * Polytypes that can be used in any place. Each var in such a polytype occurs exactly once.
   */
  public static final List<TestedType> TESTED_SINGLE_VARIABLE_POLYTYPES = list(
      A,
      a(A),
      a(a(A)),
      f(A),
      f(BOOL, A),
      f(f(A)),
      f(BOOL, f(A)),
      f(BOOL, f(BOOL, A))
  );

  public static final List<TestedType> TESTED_TYPES = ImmutableList.<TestedType>builder()
      .addAll(TESTED_MONOTYPES)
      .addAll(TESTED_VALID_POLYTYPES)
      .addAll(TESTED_SINGLE_VARIABLE_POLYTYPES)
      .build();

  public static TestedType a2(TestedType type) {
    return a(a(type));
  }

  public static TestedType a(TestedType type) {
    if (type == NOTHING) {
      return new TestedArrayType(
          type,
          TestingTypesS.a(TestingTypesS.NOTHING),
          "[]",
          list(),
          Set.of(),
          Set.of()
      );
    } else {
      Object value = type.value == null ? null : list(type.value);
      return a(type, value);
    }
  }

  private static TestedType a(TestedType type, Object value) {
    return new TestedArrayType(
        type,
        TestingTypesS.a(type.type),
        "[" + type.literal + "]",
        value,
        type.typeDeclarations(),
        type.allDeclarations()
    );
  }

  private final TypeS type;
  private final String literal;
  private final Object value;
  private final Set<String> typeDeclarations;
  private final Set<String> allDeclarations;

  public TestedType(TypeS type, String literal, Object value) {
    this(type, literal, value, Set.of(), Set.of());
  }

  public TestedType(TypeS type, String literal, Object value, Set<String> typeDeclarations,
      Set<String> allDeclarations) {
    this.type = type;
    this.literal = literal;
    this.value = value;
    this.typeDeclarations = typeDeclarations;
    this.allDeclarations = allDeclarations;
  }

  private static ImmutableSet<String> unionOf(Set<String> typeDeclarations,
      Set<String> instanceDeclarations) {
    return ImmutableSet.<String>builder()
        .addAll(typeDeclarations).addAll(instanceDeclarations).build();
  }

  public TypeS type() {
    return type;
  }

  public String literal() {
    return literal;
  }

  public Object value() {
    return value;
  }

  public Set<String> allDeclarations() {
    return allDeclarations;
  }

  public Set<String> typeDeclarations() {
    return typeDeclarations;
  }

  public String name() {
    return type.name();
  }

  public String q() {
    return "`" + type().name() + "`";
  }

  public String declarationsAsString() {
    return join("\n", allDeclarations);
  }

  public String typeDeclarationsAsString() {
    return join("\n", typeDeclarations);
  }

  public boolean isArrayOf(TestedType elem) {
    return false;
  }

  public boolean isArray() {
    return false;
  }

  public boolean isArrayOfArrays() {
    return false;
  }

  public boolean isNothing() {
    return type instanceof NothingTS;
  }

  public boolean isFunc(Predicate<TestedType> result, Predicate<TestedType>... params) {
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (TestedType) obj;
    return Objects.equals(this.type, that.type) &&
        Objects.equals(this.literal, that.literal) &&
        Objects.equals(this.value, that.value) &&
        Objects.equals(this.allDeclarations, that.allDeclarations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, literal, value, allDeclarations);
  }

  @Override
  public String toString() {
    return "TestedType(" + type + ")";
  }

  public static TestedType f(TestedType resultType, TestedType... paramTestedTypes2) {
    ImmutableList<TestedType> paramTestedTypes3 = list(paramTestedTypes2);
    var paramSignatures = toSigs(paramTestedTypes3);
    String name = "f" + UNIQUE_IDENTIFIER.getAndIncrement();
    String declaration = "@Native(\"impl\") %s %s(%s);".formatted(
        resultType.name(),
        name,
        join(",", map(paramSignatures, ItemSigS::toString)));
    Set<String> declarations = ImmutableSet.<String>builder()
        .add(declaration)
        .addAll(resultType.allDeclarations())
        .addAll(paramTestedTypes3.stream()
            .flatMap(t -> t.allDeclarations().stream())
            .collect(toList()))
        .build();
    Set<String> typeDeclarations = ImmutableSet.<String>builder()
        .addAll(resultType.typeDeclarations())
        .addAll(paramTestedTypes3.stream()
            .flatMap(t -> t.typeDeclarations().stream())
            .collect(toList()))
        .build();
    return new TestedFuncType(
        resultType,
        ImmutableList.copyOf(paramTestedTypes2),
        TestingTypesS.f(resultType.type, map(paramSignatures, ItemSigS::type)),
        name,
        null,
        typeDeclarations,
        declarations
    );
  }

  private static ImmutableList<ItemSigS> toSigs(List<TestedType> paramTestedTypes) {
    Builder<ItemSigS> builder = ImmutableList.builder();
    for (int i = 0; i < paramTestedTypes.size(); i++) {
      builder.add(new ItemSigS(paramTestedTypes.get(i).type(), "p" + i, Optional.empty()));
    }
    return builder.build();
  }

  public static class TestedFuncType extends TestedType {
    public final TestedType resType;
    public final ImmutableList<TestedType> params;

    public TestedFuncType(TestedType resType, ImmutableList<TestedType> params, FuncTS type,
        String literal, Object value, Set<String> typeDeclarations, Set<String> allDeclarations) {
      super(type, literal, value, typeDeclarations, allDeclarations);
      this.resType = resType;
      this.params = params;
    }

    @Override
    public boolean isFunc(Predicate<TestedType> result, Predicate<TestedType>... params) {
      if (result.test(resType) && this.params.size() == params.length) {
        for (int i = 0; i < this.params.size(); i++) {
          if (!params[i].test(this.params.get(i))) {
            return false;
          }
        }
        return true;
      } else {
        return false;
      }
    }
  }

  public static class TestedArrayType extends TestedType {
    public final TestedType elemType;

    public TestedArrayType(TestedType elemType, TypeS type, String literal,
        Object value, Set<String> typeDeclarations, Set<String> allDeclarations) {
      super(type, literal, value, typeDeclarations, allDeclarations);
      this.elemType = elemType;
    }

    @Override
    public boolean isArrayOf(TestedType elemType) {
      return this.elemType.equals(elemType);
    }

    @Override
    public boolean isArray() {
      return true;
    }

    @Override
    public boolean isArrayOfArrays() {
      return elemType.isArray();
    }
  }
}
