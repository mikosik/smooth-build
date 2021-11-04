package org.smoothbuild.lang.base.type.api;

import java.util.function.Supplier;

public class Sides<T extends Type> {
  private final Upper upper;
  private final Lower lower;

  public Side<T> upper() {
    return upper;
  }

  public Side<T> lower() {
    return lower;
  }

  public Sides(T anyType, T nothingType) {
    this.upper = new Upper(anyType);
    this.lower = new Lower(nothingType);
  }

  public abstract static class Side<T> {
    private final T edge;

    protected Side(T edge) {
      this.edge = edge;
    }

    // TODO on java 17 use sealed classes so we can replace calls to dispatch with
    // pattern matching switch

    public abstract <R> R dispatch(Supplier<R> lower, Supplier<R> upper);

    public abstract Side<T> reversed();

    public T edge() {
      return edge;
    }
  }

  public class Upper extends Side<T> {
    public Upper(T edge) {
      super(edge);
    }

    @Override
    public <R> R dispatch(Supplier<R> lower, Supplier<R> upper) {
      return upper.get();
    }

    @Override
    public Side<T> reversed() {
      return lower();
    }
  }

  public class Lower extends Side<T> {
    public Lower(T edge) {
      super(edge);
    }

    @Override
    public <R> R dispatch(Supplier<R> lower, Supplier<R> upper) {
      return lower.get();
    }

    @Override
    public Side<T> reversed() {
      return upper();
    }
  }
}
