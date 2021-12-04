package org.smoothbuild.lang.base.type;

import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.stream.Collectors.toList;
import static okio.ByteString.encodeString;
import static org.smoothbuild.lang.base.define.ItemSigS.itemSigS;
import static org.smoothbuild.lang.base.type.TestingTsS.struct;
import static org.smoothbuild.lang.base.type.TestingTsS.var;
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

public class TestedT {
  private static final AtomicLong UNIQUE_IDENTIFIER = new AtomicLong();

  public static final TestedT A = new TestedT(var("A"), null, null);
  public static final TestedT B = new TestedT(var("B"), null, null);
  public static final TestedT ANY = new TestedT(
      TestingTsS.ANY,
      "createAny()",
      null,
      Set.of(),
      Set.of("Any createAny() = \"abc\";"));
  public static final TestedT BLOB = new TestedT(
      TestingTsS.BLOB,
      "0x" + encodeString("xyz", US_ASCII).hex(),
      "xyz"
  );
  public static final TestedT BOOL = new TestedT(
      TestingTsS.BOOL,
      "true",
      new String(new byte[] {1})
  );
  public static final TestedT INT = new TestedT(
      TestingTsS.INT,
      "123",
      new String(new byte[] {123})
  );
  public static final TestedT NOTHING = new TestedT(
      TestingTsS.NOTHING,
      "reportError(\"e\")",
      null,
      Set.of(),
      Set.of("@Native(\"impl\") Nothing reportError(String message);"));
  public static final TestedT STRING = new TestedT(
      TestingTsS.STRING,
      "\"abc\"",
      "abc"
  );
  public static final TestedT STRUCT = new TestedT(
      struct("Person", nList(itemSigS("name", TestingTsS.STRING))),
      "person(\"John\")",
      null,
      Set.of("Person{ String name }"),
      Set.of("Person{ String name }")
  );
  public static final TestedT STRUCT_WITH_BLOB = new TestedT(
      struct("Data", nList(itemSigS("value", TestingTsS.BLOB))),
      "data(0xAB)",
      null,
      Set.of("Data{ Blob value }"),
      Set.of("Data{ Blob value }")
  );
  public static final TestedT STRUCT_WITH_BOOL = new TestedT(
      struct("Flag", nList(itemSigS("value", TestingTsS.BOOL))),
      "flag(true)",
      null,
      Set.of("Flag{ Bool value }"),
      Set.of("Flag{ Bool value }")
  );

  public static final ImmutableList<TestedT> ELEMENTARY_TYPES = list(
      ANY,
      BLOB,
      BOOL,
      NOTHING,
      STRING,
      STRUCT,
      A);

  public static final List<TestedT> TESTED_MONOTYPES = list(
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
  public static final List<TestedT> TESTED_VALID_POLYTYPES = list(
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
  public static final List<TestedT> TESTED_SINGLE_VARIABLE_POLYTYPES = list(
      A,
      a(A),
      a(a(A)),
      f(A),
      f(BOOL, A),
      f(f(A)),
      f(BOOL, f(A)),
      f(BOOL, f(BOOL, A))
  );

  public static final List<TestedT> TESTED_TYPES = ImmutableList.<TestedT>builder()
      .addAll(TESTED_MONOTYPES)
      .addAll(TESTED_VALID_POLYTYPES)
      .addAll(TESTED_SINGLE_VARIABLE_POLYTYPES)
      .build();

  public static TestedT a2(TestedT type) {
    return a(a(type));
  }

  public static TestedT a(TestedT type) {
    if (type == NOTHING) {
      return new TestedArrayT(
          type,
          TestingTsS.a(TestingTsS.NOTHING),
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

  private static TestedT a(TestedT type, Object value) {
    return new TestedArrayT(
        type,
        TestingTsS.a(type.type),
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

  public TestedT(TypeS type, String literal, Object value) {
    this(type, literal, value, Set.of(), Set.of());
  }

  public TestedT(TypeS type, String literal, Object value, Set<String> typeDeclarations,
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

  public boolean isArrayOf(TestedT elem) {
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

  public boolean isFunc(Predicate<TestedT> result, Predicate<TestedT>... params) {
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (TestedT) obj;
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

  public static TestedT f(TestedT resT, TestedT... paramTestedTs) {
    ImmutableList<TestedT> paramTestedTs2 = list(paramTestedTs);
    var paramSignatures = toSigs(paramTestedTs2);
    String name = "f" + UNIQUE_IDENTIFIER.getAndIncrement();
    String declaration = "@Native(\"impl\") %s %s(%s);".formatted(
        resT.name(),
        name,
        join(",", map(paramSignatures, ItemSigS::toString)));
    Set<String> declarations = ImmutableSet.<String>builder()
        .add(declaration)
        .addAll(resT.allDeclarations())
        .addAll(paramTestedTs2.stream()
            .flatMap(t -> t.allDeclarations().stream())
            .collect(toList()))
        .build();
    Set<String> typeDeclarations = ImmutableSet.<String>builder()
        .addAll(resT.typeDeclarations())
        .addAll(paramTestedTs2.stream()
            .flatMap(t -> t.typeDeclarations().stream())
            .collect(toList()))
        .build();
    return new TestedFuncT(
        resT,
        ImmutableList.copyOf(paramTestedTs),
        TestingTsS.f(resT.type, map(paramSignatures, ItemSigS::type)),
        name,
        null,
        typeDeclarations,
        declarations
    );
  }

  private static ImmutableList<ItemSigS> toSigs(List<TestedT> paramTestedTs) {
    Builder<ItemSigS> builder = ImmutableList.builder();
    for (int i = 0; i < paramTestedTs.size(); i++) {
      builder.add(new ItemSigS(paramTestedTs.get(i).type(), "p" + i, Optional.empty()));
    }
    return builder.build();
  }

  public static class TestedFuncT extends TestedT {
    public final TestedT resT;
    public final ImmutableList<TestedT> params;

    public TestedFuncT(TestedT resT, ImmutableList<TestedT> params, FuncTS type,
        String literal, Object value, Set<String> typeDeclarations, Set<String> allDeclarations) {
      super(type, literal, value, typeDeclarations, allDeclarations);
      this.resT = resT;
      this.params = params;
    }

    @Override
    public boolean isFunc(Predicate<TestedT> result, Predicate<TestedT>... params) {
      if (result.test(resT) && this.params.size() == params.length) {
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

  public static class TestedArrayT extends TestedT {
    public final TestedT elemT;

    public TestedArrayT(TestedT elemT, TypeS type, String literal, Object value,
        Set<String> typeDeclarations, Set<String> allDeclarations) {
      super(type, literal, value, typeDeclarations, allDeclarations);
      this.elemT = elemT;
    }

    @Override
    public boolean isArrayOf(TestedT elemT) {
      return this.elemT.equals(elemT);
    }

    @Override
    public boolean isArray() {
      return true;
    }

    @Override
    public boolean isArrayOfArrays() {
      return elemT.isArray();
    }
  }
}
