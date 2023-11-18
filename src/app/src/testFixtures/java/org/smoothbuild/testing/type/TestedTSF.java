package org.smoothbuild.testing.type;

import static org.smoothbuild.common.collect.Lists.list;
import static org.smoothbuild.common.collect.Lists.map;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compile.frontend.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.testing.TestContext.arrayTS;
import static org.smoothbuild.testing.TestContext.blobTS;
import static org.smoothbuild.testing.TestContext.boolTS;
import static org.smoothbuild.testing.TestContext.funcTS;
import static org.smoothbuild.testing.TestContext.intTS;
import static org.smoothbuild.testing.TestContext.stringTS;
import static org.smoothbuild.testing.TestContext.structTS;
import static org.smoothbuild.testing.TestContext.varS;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.smoothbuild.compile.frontend.lang.define.ItemSigS;
import org.smoothbuild.testing.type.TestedTS.TestedArrayTS;
import org.smoothbuild.testing.type.TestedTS.TestedFuncTS;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

public class TestedTSF {
  private static final AtomicLong UNIQUE_IDENTIFIER = new AtomicLong();

  public static final TestedTS A = new TestedTS(varS("A"));
  public static final TestedTS B = new TestedTS(varS("B"));
  public static final TestedTS BLOB = new TestedTS(blobTS()
  );
  public static final TestedTS BOOL = new TestedTS(boolTS());

  public static final TestedTS INT = new TestedTS(intTS());
  public static final TestedTS STRING = new TestedTS(stringTS());
  public static final TestedTS STRUCT = new TestedTS(
      structTS("Person", nlist(itemSigS(stringTS(), "name"))),
      Set.of("Person(String name)"),
      Set.of("Person(String name)")
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

  private static TestedTS a(TestedTS type) {
    return new TestedArrayTS(
        type,
        arrayTS(type.type()),
        type.typeDeclarations(),
        type.allDeclarations()
    );
  }

  public TestedTS func(TestedTS resultT, ImmutableList<TestedTS> paramTs) {
    return f(resultT, paramTs);
  }

  public static TestedTS f(TestedTS resultT, TestedTS... paramTestedTs) {
    return f(resultT, list(paramTestedTs));
  }

  public static TestedFuncTS f(TestedTS resultT, ImmutableList<TestedTS> paramTestedTs) {
    var paramSigs = toSigs(paramTestedTs);
    String name = "f" + UNIQUE_IDENTIFIER.getAndIncrement();
    String declaration = "@Native(\"impl\") %s %s(%s);".formatted(
        resultT.name(),
        name,
        toParamDeclarationString(paramTestedTs));
    Set<String> declarations = ImmutableSet.<String>builder()
        .add(declaration)
        .addAll(resultT.allDeclarations())
        .addAll(paramTestedTs.stream()
            .flatMap(t -> t.allDeclarations().stream())
            .toList())
        .build();
    Set<String> typeDeclarations = ImmutableSet.<String>builder()
        .addAll(resultT.typeDeclarations())
        .addAll(paramTestedTs.stream()
            .flatMap(t -> t.typeDeclarations().stream())
            .toList())
        .build();
    return new TestedFuncTS(
        resultT,
        paramTestedTs,
        funcTS(map(paramSigs, ItemSigS::type), resultT.type()),
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
