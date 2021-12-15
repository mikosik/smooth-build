package org.smoothbuild.lang.base.type;

import static org.smoothbuild.util.collect.Lists.allMatch;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;

public class TestedTH implements TestedT<TypeH> {
  private final TypeH type;

  public TestedTH(TypeH type) {
    this.type = type;
  }

  @Override
  public TypeH type() {
    return type;
  }

  @Override
  public boolean isFunc(Predicate<? super TestedT<? extends Type>> resPredicate,
      List<? extends Predicate<? super TestedT<? extends Type>>> paramPredicates) {
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

  @Override
  public boolean isArrayOf(TestedT<? extends Type> nothing) {
    return false;
  }

  @Override
  public boolean isTuple() {
    return false;
  }

  @Override
  public boolean isTupleOfTuple() {
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    return obj instanceof TestedTH that
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

  public static class TestedFuncTH extends TestedTH {
    private final TestedTH resT;
    private final ImmutableList<TestedTH> paramTs;

    public TestedFuncTH(TypeH type, TestedTH resT, ImmutableList<TestedTH> paramTs) {
      super(type);
      this.resT = resT;
      this.paramTs = paramTs;
    }

    @Override
    public boolean isFunc(Predicate<? super TestedT<? extends Type>> resPredicate,
        List<? extends Predicate<? super TestedT<? extends Type>>> paramPredicates) {
      return paramTs.size() == paramPredicates.size()
          && resPredicate.test(resT)
          && allMatch(paramPredicates, paramTs, Predicate::test);
    }
  }

  public static class TestedArrayTH extends TestedTH {
    private final TestedTH elemT;

    public TestedArrayTH(TestedTH elemT, TypeH type) {
      super(type);
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

  public static class TestedTupleTH extends TestedTH {
    private final ImmutableList<TestedTH> items;

    public TestedTupleTH(TypeH type, ImmutableList<TestedTH> items) {
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
