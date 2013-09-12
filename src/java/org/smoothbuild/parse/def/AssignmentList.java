package org.smoothbuild.parse.def;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.function.def.FileSetNode;
import org.smoothbuild.function.def.StringSetNode;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class AssignmentList {
  private final List<Assignment> assignments;
  private final Set<String> namesAlreadyAdded;

  public AssignmentList() {
    this.assignments = Lists.newArrayList();
    this.namesAlreadyAdded = Sets.newHashSet();
  }

  public void add(Param param, Argument argument) {
    Assignment assignment = new Assignment(param, argument);
    checkState(!namesAlreadyAdded.contains(param.name()));
    assignments.add(assignment);
    namesAlreadyAdded.add(param.name());
  }

  public Map<String, DefinitionNode> createNodesMap() {
    Builder<String, DefinitionNode> builder = ImmutableMap.builder();

    for (Assignment assignment : assignments) {
      Param param = assignment.param();
      DefinitionNode node = assignment.argument().definitionNode();
      builder.put(param.name(), convert(param.type(), node));
    }

    return builder.build();
  }

  private static DefinitionNode convert(Type type, DefinitionNode argNode) {
    if (argNode.type() == Type.EMPTY_SET) {
      if (type == Type.STRING_SET) {
        return new StringSetNode(ImmutableList.<DefinitionNode> of());
      } else if (type == Type.FILE_SET) {
        return new FileSetNode(ImmutableList.<DefinitionNode> of());
      } else {
        throw new RuntimeException("Cannot convert from " + argNode.type() + " to " + type + ".");
      }
    } else {
      return argNode;
    }
  }

  private static class Assignment {
    private final Param param;
    private final Argument argument;

    public Assignment(Param param, Argument argument) {
      boolean isAssignable = param.type().isAssignableFrom(argument.type());
      if (!isAssignable) {
        throw new IllegalArgumentException("Param " + param + " cannot be assigned from "
            + argument + " argument.");
      }
      Preconditions.checkArgument(isAssignable);
      this.param = checkNotNull(param);
      this.argument = checkNotNull(argument);
    }

    public Param param() {
      return param;
    }

    public Argument argument() {
      return argument;
    }
  }
}
