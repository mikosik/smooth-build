package org.smoothbuild.compilerfrontend.acceptance;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.type.SArrayType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public class Util {
  public static String illegalCallMessage(SType called, List<? extends SType> args) {
    return "Illegal call: Instance of " + called.q()
        + " cannot be called with arguments "
        + args.map(SType::toSourceCode).toString("`(", ", ", ")`")
        + ".";
  }

  public static String bodyTypeMessage(
      String evaluableName, SType actualBodyType, SType declaredEvaluableType) {
    return "`" + evaluableName
        + "` body type " + actualBodyType.q()
        + " is not equal to declared type " + declaredEvaluableType.q()
        + ".";
  }

  public static String arrayTypeMessage(int index, SType inferred, SType element) {
    return "Cannot infer array type. After unifying first "
        + (index + 1)
        + " elements, array type is inferred as "
        + new SArrayType(inferred).q()
        + ". However type of element at index 1 is "
        + element.q()
        + ".";
  }

  public static String missingField(SType type, String fieldName) {
    return "Instance of " + type.q() + " has no field `" + fieldName + "`.";
  }
}
