package org.smoothbuild.compilerfrontend.lang.define;

public record SModule(SScope members, SScope membersAndImported) {}
