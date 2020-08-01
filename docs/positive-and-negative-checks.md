When checking upon a condition in a conditional statement, the type and structure of jump commands will be based on appenders within the condition. A single part of the condition that is resolved to a boolean inside of a conditional will be referred to as either a positve or negative jump.

*positive jump*: skip to block when true

`if(true | true)
  assert true;`

Positive jumps perform a jump when the condition resolves to true. In this example, the first part of the condition makes the program jump to the assertion. The second part would have the same effect if ever encountered. Otherwise, the program would skip the assertion.


*negative jump*: skip out of block when false

`if(true & true)
  assert true;`

Negative jumps work in the opposite manner of positive jumps. Negative jumps perform a jump when the condition resolves to false. In this example, the first part of the condition doesn't make the program jump at all. The second part does the same and doesn't make the program jump. Otherwise, the program would skip the assertion.
