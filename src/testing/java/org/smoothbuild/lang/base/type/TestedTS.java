package org.smoothbuild.lang.base.type;

import static java.lang.String.join;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.base.type.impl.NothingTS;
import org.smoothbuild.lang.base.type.impl.TypeS;

import com.google.common.collect.ImmutableList;

public class TestedTS implements TestedT<TypeS> {
  private final TypeS type;
  private final String literal;
  private final Object value;
  private final Set<String> typeDeclarations;
  private final Set<String> allDeclarations;

  public TestedTS(TypeS type, String literal, Object value) {
    this(type, literal, value, Set.of(), Set.of());
  }

  public TestedTS(TypeS type, String literal, Object value, Set<String> typeDeclarations,
      Set<String> allDeclarations) {
    this.type = type;
    this.literal = literal;
    this.value = value;
    this.typeDeclarations = typeDeclarations;
    this.allDeclarations = allDeclarations;
  }

  @Override
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

  public String declarationsAsString() {
    return join("\n", allDeclarations);
  }

  public String typeDeclarationsAsString() {
    return join("\n", typeDeclarations);
  }

  @Override
  public boolean isArrayOf(TestedT<? extends Type> elem) {
    return false;
  }

  @Override
  public boolean isArray() {
    return false;
  }

  @Override
  public boolean isArrayOfArrays() {
    return false;
  }

  public boolean isNothing() {
    return type instanceof NothingTS;
  }

  @Override
  public boolean isFunc(
      Predicate<? super TestedT<? extends Type>> result,
      List<? extends Predicate<? super TestedT<? extends Type>>> params) {
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (TestedTS) obj;
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

  public static class TestedFuncT extends TestedTS {
    public final TestedTS resT;
    public final ImmutableList<TestedTS> params;

    public TestedFuncT(TestedTS resT, ImmutableList<TestedTS> params, FuncTS type,
        String literal, Object value, Set<String> typeDeclarations, Set<String> allDeclarations) {
      super(type, literal, value, typeDeclarations, allDeclarations);
      this.resT = resT;
      this.params = params;
    }

    @Override
    public boolean isFunc(
        Predicate<? super TestedT<? extends Type>> result,
        List<? extends Predicate<? super TestedT<? extends Type>>> params) {
      if (result.test(resT) && this.params.size() == params.size()) {
        for (int i = 0; i < this.params.size(); i++) {
          if (!params.get(i).test(this.params.get(i))) {
            return false;
          }
        }
        return true;
      } else {
        return false;
      }
    }
  }

  public static class TestedArrayT extends TestedTS {
    public final TestedTS elemT;

    public TestedArrayT(TestedTS elemT, TypeS type, String literal, Object value,
        Set<String> typeDeclarations, Set<String> allDeclarations) {
      super(type, literal, value, typeDeclarations, allDeclarations);
      this.elemT = elemT;
    }

    @Override
    public boolean isArrayOf(TestedT<? extends Type> elemT) {
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
