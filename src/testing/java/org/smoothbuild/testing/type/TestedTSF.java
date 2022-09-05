package org.smoothbuild.testing.type;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.stream.Collectors.toList;
import static okio.ByteString.encodeString;
import static org.smoothbuild.compile.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.testing.type.TestingTS.var;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nlist;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.smoothbuild.compile.lang.define.ItemSigS;
import org.smoothbuild.testing.type.TestedTS.TestedArrayTS;
import org.smoothbuild.testing.type.TestedTS.TestedFuncTS;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

public class TestedTSF {
  private static final AtomicLong UNIQUE_IDENTIFIER = new AtomicLong();

  public static final TestedTS A = new TestedTS(var("A"), null, null);
  public static final TestedTS B = new TestedTS(var("B"), null, null);
  public static final TestedTS BLOB = new TestedTS(
      TestingTS.BLOB,
      "0x" + encodeString("xyz", US_ASCII).hex(),
      "xyz"
  );
  public static final TestedTS BOOL = new TestedTS(
      TestingTS.BOOL,
      "true",
      new String(new byte[] {1})
  );

  public static final TestedTS INT = new TestedTS(
      TestingTS.INT,
      "123",
      new String(new byte[] {123})
  );
  public static final TestedTS STRING = new TestedTS(
      TestingTS.STRING,
      "\"abc\"",
      "abc"
  );
  public static final TestedTS STRUCT = new TestedTS(
      TestingTS.struct("Person", nlist(itemSigS(TestingTS.STRING, "name"))),
      "person(\"John\")",
      null,
      Set.of("Person{ String name }"),
      Set.of("Person{ String name }")
  );
  public static final List<TestedTS> TESTED_TYPES = list(
      BLOB,
      BOOL,
      STRING,
      STRUCT,
      a(BLOB),
      a(STRUCT),
      a(a(BLOB)),
      a(a(STRUCT)),
      f(BLOB),
      f(f(BLOB)),
      f(BLOB, BOOL),
      f(BLOB, f(BOOL))
  );

  public TestedTS blob() {
    return BLOB;
  }

  public TestedTS bool() {
    return BOOL;
  }

  public TestedTS int_() {
    return INT;
  }

  public TestedTS string() {
    return STRING;
  }

  public TestedTS struct() {
    return STRUCT;
  }

  public TestedTS tuple() {
    throw new UnsupportedOperationException();
  }

  public TestedTS tuple(ImmutableList<TestedTS> items) {
    throw new UnsupportedOperationException();
  }

  public TestedTS array(TestedTS type) {
    return a(type);
  }

  public static TestedTS a(TestedTS type) {
    Object value = type.value() == null ? null : list(type.value());
    return a(type, value);
  }

  private static TestedTS a(TestedTS type, Object value) {
    return new TestedArrayTS(
        type,
        TestingTS.a(type.type()),
        "[" + type.literal() + "]",
        value,
        type.typeDeclarations(),
        type.allDeclarations()
    );
  }

  public TestedTS func(TestedTS resT, ImmutableList<TestedTS> paramTs) {
    return f(resT, paramTs);
  }

  public static TestedTS f(TestedTS resT, TestedTS... paramTestedTs) {
    return f(resT, list(paramTestedTs));
  }

  public static TestedFuncTS f(TestedTS resT, ImmutableList<TestedTS> paramTestedTs) {
    var paramSigs = toSigs(paramTestedTs);
    String name = "f" + UNIQUE_IDENTIFIER.getAndIncrement();
    String declaration = "@Native(\"impl\") %s %s(%s);".formatted(
        resT.name(),
        name,
        toParamDeclarationString(paramTestedTs));
    Set<String> declarations = ImmutableSet.<String>builder()
        .add(declaration)
        .addAll(resT.allDeclarations())
        .addAll(paramTestedTs.stream()
            .flatMap(t -> t.allDeclarations().stream())
            .collect(toList()))
        .build();
    Set<String> typeDeclarations = ImmutableSet.<String>builder()
        .addAll(resT.typeDeclarations())
        .addAll(paramTestedTs.stream()
            .flatMap(t -> t.typeDeclarations().stream())
            .collect(toList()))
        .build();
    return new TestedFuncTS(
        resT,
        paramTestedTs,
        TestingTS.f(resT.type(), map(paramSigs, ItemSigS::type)),
        name,
        null,
        typeDeclarations,
        declarations
    );
  }

  private static String toParamDeclarationString(ImmutableList<TestedTS> paramTestedTs) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < paramTestedTs.size(); i++) {
      builder.append(paramTestedTs.get(i).name() + " p" + i);
    }
    return builder.toString();
  }

  private static ImmutableList<ItemSigS> toSigs(List<TestedTS> paramTestedTs) {
    Builder<ItemSigS> builder = ImmutableList.builder();
    for (int i = 0; i < paramTestedTs.size(); i++) {
      builder.add(new ItemSigS(paramTestedTs.get(i).type(), "p" + i));
    }
    return builder.build();
  }
}
