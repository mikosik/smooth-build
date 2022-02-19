package org.smoothbuild.lang.define;

import java.util.Objects;

import org.smoothbuild.lang.type.impl.TypeS;

/**
 * Annotated value (one that has not a body).
 *
 * This class is immutable.
 */
public final class AnnValS extends ValS {
  private final AnnS ann;

  public AnnValS(AnnS ann, TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
    this.ann = ann;
  }

  public AnnS ann() {
    return ann;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof AnnValS that
        && this.ann().equals(that.ann())
        && this.type().equals(that.type())
        && this.modPath().equals(that.modPath())
        && this.name().equals(that.name())
        && this.loc().equals(that.loc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(ann(), type(), modPath(), name(), loc());
  }

  @Override
  public String toString() {
    return "DefVal(`" + ann() + " " + type().name() + " " + name() + "`)";
  }
}

