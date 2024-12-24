package org.smoothbuild.compilerfrontend.compile.ast.define;

import com.google.common.base.Objects;
import org.smoothbuild.common.base.Strings;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.HasLocationImpl;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.HasNameText;

/**
 * Annotation.
 */
public final class PAnnotation extends HasLocationImpl implements HasNameText {
  private final String nameText;
  private final PString value;

  public PAnnotation(String nameText, PString value, Location location) {
    super(location);
    this.nameText = nameText;
    this.value = value;
  }

  @Override
  public String nameText() {
    return nameText;
  }

  public String q() {
    return Strings.q(nameText);
  }

  public PString value() {
    return value;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof PAnnotation that
        && this.nameText().equals(that.nameText())
        && this.value().equals(that.value())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.nameText(), this.value, this.location());
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PAnnotation")
        .addField("name", nameText())
        .addField("value", value)
        .addField("location", location())
        .toString();
  }
}
