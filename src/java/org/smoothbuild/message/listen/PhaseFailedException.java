package org.smoothbuild.message.listen;

/**
 * Thrown to stop execution of given phase. Indicates that error message have
 * been already reported to appropriate MessageGroup.
 */
@SuppressWarnings("serial")
public class PhaseFailedException extends RuntimeException {}
