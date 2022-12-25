package org.smoothbuild.testing.type;

import static java.lang.String.join;
import static org.smoothbuild.util.collect.Iterables.toCommaSeparatedString;

import java.util.Objects;
import java.util.Set;

import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.TypeS;

import com.google.common.collect.ImmutableList;

public class TestedTS {
  private final TypeS type;
  private final Set<String> typeDeclarations;
  private final Set<String> allDeclarations;

  public TestedTS(TypeS type) {
    this(type, Set.of(), Set.of());
  }

  public TestedTS(TypeS type, Set<String> typeDeclarations, Set<String> allDeclarations) {
    this.type = type;
    this.typeDeclarations = typeDeclarations;
    this.allDeclarations = allDeclarations;
  }

  public TypeS type() {
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
    public final TestedTS resT;
    public final ImmutableList<TestedTS> paramTs;

    public TestedFuncTS(TestedTS resT, ImmutableList<TestedTS> paramTs, FuncTS type,
        Set<String> typeDeclarations, Set<String> allDeclarations) {
      super(type, typeDeclarations, allDeclarations);
      this.resT = resT;
      this.paramTs = paramTs;
    }

    @Override
    public String name() {
      return "(" + toCommaSeparatedString(paramTs, TestedTS::name) + ")->" + resT.name();
    }
  }

  public static class TestedArrayTS extends TestedTS {
    public final TestedTS elemT;

    public TestedArrayTS(TestedTS elemT, TypeS type,
        Set<String> typeDeclarations, Set<String> allDeclarations) {
      super(type, typeDeclarations, allDeclarations);
      this.elemT = elemT;
    }


    @Override
    public boolean isArray() {
      return true;
    }
  }
}
