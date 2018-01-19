package org.smoothbuild.lang.message;

import static com.google.common.collect.Streams.stream;

public class Messages {
  public static boolean containsErrors(Iterable<Message> messages) {
    return stream(messages)
        .anyMatch(Message::isError);
  }
}
