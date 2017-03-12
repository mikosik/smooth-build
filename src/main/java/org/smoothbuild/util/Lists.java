package org.smoothbuild.util;

import java.util.ArrayList;
import java.util.List;

public class Lists {
  public static <E> List<E> concat(List<E> list, E element) {
    List<E> result = new ArrayList<>(list);
    result.add(element);
    return result;
  }
}
