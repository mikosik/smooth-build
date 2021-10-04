package org.smoothbuild.lang.base.type;

import java.util.function.Supplier;

public class Sides {
  private final Upper upper;
  private final Lower lower;

  public Side upper() {
    return upper;
  }

  public Side lower() {
    return lower;
  }

  public Sides(AnyType anyType, NothingType nothingType) {
    this.upper = new Upper(anyType);
    this.lower = new Lower(nothingType);
  }

  public abstract static class Side {
    private final Type edge;

    protected Side(Type edge) {
      this.edge = edge;
    }

    // TODO on java 17 use sealed classes so we can replace calls to dispatch with
    // pattern matching switch

    public abstract <T> T dispatch(Supplier<T> lower, Supplier<T> upper);

    public abstract Side reversed();

    public Type edge() {
      return edge;
    }
  }

  public class Upper extends Side {
    public Upper(Type edge) {
      super(edge);
    }

    @Override
    public <T> T dispatch(Supplier<T> lower, Supplier<T> upper) {
      return upper.get();
    }

    @Override
    public Side reversed() {
      return lower();
    }
  }

  public class Lower extends Side {
    public Lower(Type edge) {
      super(edge);
    }

    @Override
    public <T> T dispatch(Supplier<T> lower, Supplier<T> upper) {
      return lower.get();
    }

    @Override
    public Side reversed() {
      return upper();
    }
  }
}
