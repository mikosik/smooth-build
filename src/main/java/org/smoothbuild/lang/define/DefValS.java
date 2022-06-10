package org.smoothbuild.lang.define;

import java.util.Objects;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.MonoTS;

/**
 * Defined value (one that has a body).
 *
 * This class is immutable.
 */
public final class DefValS extends ValS {
  private final MonoObjS body;

  public DefValS(MonoTS type, ModPath modPath, String name, MonoObjS body, Loc loc) {
    super(type, modPath, name, loc);
    this.body = body;
  }

  public MonoObjS body() {
    return body;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof DefValS that
        && this.type().equals(that.type())
        && this.name().equals(that.name())
        && this.body().equals(that.body())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(type(), name(), body(), loc());
  }

  @Override
  public String toString() {
    return "DefVal(`" + type().name() + " " + name() + " = " + body + "`)";
  }
}


