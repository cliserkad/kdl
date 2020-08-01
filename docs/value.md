Anything that can be put on the JVM stack is considered a `value`.
Of these values there are several ways they may appear in the source code
 - Literal
 - Variable
 - Array Length
 - Array Access
 - Constant

Each of these have separate opcodes required to load them on to the stack. Literals use
the LDC functions available in the ASM library. Constants are translated to literals,
and pushed using LDC functions.

