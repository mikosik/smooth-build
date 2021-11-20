package org.smoothbuild.lang.base.type.api;

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

  public sealed abstract static class Side<T> permits Sides.Upper, Sides.Lower {
    private final T edge;

    protected Side(T edge) {
      this.edge = edge;
    }

    public abstract Side<T> reversed();

    public T edge() {
      return edge;
    }
  }

  public final class Upper extends Side<T> {
    public Upper(T edge) {
      super(edge);
    }

    @Override
    public Side<T> reversed() {
      return lower();
    }
  }

  public final class Lower extends Side<T> {
    public Lower(T edge) {
      super(edge);
    }

    @Override
    public Side<T> reversed() {
      return upper();
    }
  }
}
