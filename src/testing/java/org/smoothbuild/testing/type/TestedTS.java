package org.smoothbuild.testing.type;

import static java.lang.String.join;
import static org.smoothbuild.util.collect.Lists.allMatch;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.smoothbuild.lang.type.MonoFuncTS;
import org.smoothbuild.lang.type.MonoTS;

import com.google.common.collect.ImmutableList;

public class TestedTS {
  private final MonoTS type;
  private final String literal;
  private final Object value;
  private final Set<String> typeDeclarations;
  private final Set<String> allDeclarations;

  public TestedTS(MonoTS type, String literal, Object value) {
    this(type, literal, value, Set.of(), Set.of());
  }

  public TestedTS(MonoTS type, String literal, Object value, Set<String> typeDeclarations,
      Set<String> allDeclarations) {
    this.type = type;
    this.literal = literal;
    this.value = value;
    this.typeDeclarations = typeDeclarations;
    this.allDeclarations = allDeclarations;
  }

  public MonoTS type() {
    return type;
  }

  public String name() {
    return type().name();
  }

  public String q() {
    return type().q();
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

  public String declarationsAsString() {
    return join("\n", allDeclarations);
  }

  public String typeDeclarationsAsString() {
    return join("\n", typeDeclarations);
  }

  public boolean isArrayOf(TestedTS elem) {
    return false;
  }

  public boolean isArray() {
    return false;
  }

  public boolean isArrayOfArrays() {
    return false;
  }

  public boolean isFunc(
      Predicate<? super TestedTS> resPredicate,
      List<? extends Predicate<? super TestedTS>> paramPredicates) {
    return false;
  }

  public boolean isTuple() {
    return false;
  }

  public boolean isTupleOfTuple() {
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    return obj instanceof TestedTS that
        && Objects.equals(this.type, that.type)
        && Objects.equals(this.literal, that.literal)
        && Objects.equals(this.value, that.value)
        && Objects.equals(this.allDeclarations, that.allDeclarations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, literal, value, allDeclarations);
  }

  @Override
  public String toString() {
    return "TestedTS(" + type + ")";
  }

  public static class TestedFuncTS extends TestedTS {
    public final TestedTS resT;
    public final ImmutableList<TestedTS> paramTs;

    public TestedFuncTS(TestedTS resT, ImmutableList<TestedTS> paramTs, MonoFuncTS type,
        String literal, Object value, Set<String> typeDeclarations, Set<String> allDeclarations) {
      super(type, literal, value, typeDeclarations, allDeclarations);
      this.resT = resT;
      this.paramTs = paramTs;
    }

    @Override
    public String name() {
      return resT.name() + "(" + toCommaSeparatedString(paramTs, TestedTS::name) + ")";
    }

    @Override
    public boolean isFunc(
        Predicate<? super TestedTS> resPredicate,
        List<? extends Predicate<? super TestedTS>> paramPredicates) {
      return paramTs.size() == paramPredicates.size()
          && resPredicate.test(resT)
          && allMatch(paramPredicates, paramTs, Predicate::test);
    }
  }

  public static class TestedArrayTS extends TestedTS {
    public final TestedTS elemT;

    public TestedArrayTS(TestedTS elemT, MonoTS type, String literal, Object value,
        Set<String> typeDeclarations, Set<String> allDeclarations) {
      super(type, literal, value, typeDeclarations, allDeclarations);
      this.elemT = elemT;
    }

    @Override
    public boolean isArrayOf(TestedTS elemT) {
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
