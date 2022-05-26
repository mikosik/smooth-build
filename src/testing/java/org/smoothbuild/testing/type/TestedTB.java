package org.smoothbuild.testing.type;

import static org.smoothbuild.util.collect.Lists.allMatch;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.smoothbuild.bytecode.type.cnst.TypeB;

import com.google.common.collect.ImmutableList;

public class TestedTB {
  private final TypeB type;

  public TestedTB(TypeB type) {
    this.type = type;
  }

  public TypeB type() {
    return type;
  }

  public String name() {
    return type().name();
  }

  public String q() {
    return type().q();
  }

  public boolean isFunc(Predicate<? super TestedTB> resPredicate,
      List<? extends Predicate<? super TestedTB>> paramPredicates) {
    return false;
  }

  public boolean isArray() {
    return false;
  }

  public boolean isArrayOfArrays() {
    return false;
  }

  public boolean isArrayOf(TestedTB elemT) {
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
    if (obj == this) {
      return true;
    }
    return obj instanceof TestedTB that
        && Objects.equals(this.type, that.type);
  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }

  @Override
  public String toString() {
    return "TestedTH(" + type + ")";
  }

  public static class TestedFuncTB extends TestedTB {
    private final TestedTB resT;
    private final ImmutableList<TestedTB> paramTs;

    public TestedFuncTB(TypeB type, TestedTB resT, ImmutableList<TestedTB> paramTs) {
      super(type);
      this.resT = resT;
      this.paramTs = paramTs;
    }

    @Override
    public String name() {
      return resT.name() + "(" + toCommaSeparatedString(paramTs, TestedTB::name) + ")";
    }

    @Override
    public boolean isFunc(Predicate<? super TestedTB> resPredicate,
        List<? extends Predicate<? super TestedTB>> paramPredicates) {
      return paramTs.size() == paramPredicates.size()
          && resPredicate.test(resT)
          && allMatch(paramPredicates, paramTs, Predicate::test);
    }
  }

  public static class TestedArrayTB extends TestedTB {
    private final TestedTB elemT;

    public TestedArrayTB(TestedTB elemT, TypeB type) {
      super(type);
      this.elemT = elemT;
    }

    @Override
    public boolean isArrayOf(TestedTB elemT) {
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

  public static class TestedTupleTB extends TestedTB {
    private final ImmutableList<TestedTB> items;

    public TestedTupleTB(TypeB type, ImmutableList<TestedTB> items) {
      super(type);
      this.items = items;
    }

    @Override
    public boolean isTuple() {
      return true;
    }

    @Override
    public boolean isTupleOfTuple() {
      return items.size() == 1 && items.get(0).isTuple();
    }
  }
}
