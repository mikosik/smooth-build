package org.smoothbuild.compilerfrontend.testing;

import static java.lang.String.join;

import java.util.Objects;
import java.util.Set;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public class TestedTS {
  private final SType type;
  private final Set<String> typeDeclarations;
  private final Set<String> allDeclarations;

  public TestedTS(SType type) {
    this(type, Set.of(), Set.of());
  }

  public TestedTS(SType type, Set<String> typeDeclarations, Set<String> allDeclarations) {
    this.type = type;
    this.typeDeclarations = typeDeclarations;
    this.allDeclarations = allDeclarations;
  }

  public SType type() {
    return type;
  }

  public String name() {
    return type().name();
  }

  public String q() {
    return type().q();
  }

  public Set<String> allDeclarations() {
    return allDeclarations;
  }

  public Set<String> typeDeclarations() {
    return typeDeclarations;
  }

  public String declarationsAsString() {
    return join("\n", allDeclarations);
  }

  public String typeDeclarationsAsString() {
    return join("\n", typeDeclarations);
  }

  public boolean isArray() {
    return false;
  }

  public boolean isTuple() {
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    return obj instanceof TestedTS that
        && Objects.equals(this.type, that.type)
        && Objects.equals(this.allDeclarations, that.allDeclarations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, allDeclarations);
  }

  @Override
  public String toString() {
    return "TestedTS(" + type + ")";
  }

  public static class TestedFuncTS extends TestedTS {
    public final TestedTS resultT;
    public final List<TestedTS> paramTs;

    public TestedFuncTS(
        TestedTS resultT,
        List<TestedTS> paramTs,
        SFuncType type,
        Set<String> typeDeclarations,
        Set<String> allDeclarations) {
      super(type, typeDeclarations, allDeclarations);
      this.resultT = resultT;
      this.paramTs = paramTs;
    }

    @Override
    public String name() {
      return paramTs.map(TestedTS::name).toString("(", ",", ")->") + resultT.name();
    }
  }

  public static class TestedArrayTS extends TestedTS {
    public final TestedTS elemT;

    public TestedArrayTS(
        TestedTS elemT, SType type, Set<String> typeDeclarations, Set<String> allDeclarations) {
      super(type, typeDeclarations, allDeclarations);
      this.elemT = elemT;
    }

    @Override
    public boolean isArray() {
      return true;
    }
  }
}
