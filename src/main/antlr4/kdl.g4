grammar kdl;

@header {
package main.com.xarql.kdl;
}

// skip over whitespace
WS : [ \t\r\n]+ -> skip;

fragment DIGIT      : [0-9];
fragment UPLETTER   : [A-Z];
fragment DNLETTER   : [a-z];
fragment LETTER     : UPLETTER | DNLETTER;
fragment ALPHANUM   : LETTER | DIGIT;
fragment UNDERSCORE : '_';

CONSTNAME : UPLETTER (UPLETTER | DIGIT | UNDERSCORE)+;
CLASSNAME : UPLETTER (LETTER)+;
VARNAME   : DNLETTER (LETTER)+;

ESCAPED_QUOTE : '\\"';

// literals
STRING : '"' (ESCAPED_QUOTE | ~'"')* '"';
number: DIGIT+;
bool: 'true' | 'false';
literal: number | bool | STRING;



// method calls
methodCallStatement: methodCall ('.' regularMethodCall)* ';';
methodCall: regularMethodCall | objectiveMethodCall | staticMethodCall;
regularMethodCall: VARNAME parameterSet;
objectiveMethodCall: VARNAME '.' regularMethodCall;
staticMethodCall: CLASSNAME '.' regularMethodCall;
parameterSet: '(' parameter? (',' parameter)* ')';
parameter: methodCall | VARNAME | CONSTNAME;

type: basetype | CLASSNAME;
basetype: 'boolean' | 'int' | 'string';

source: clazz;
clazz: 'class' CLASSNAME '{' (constant | run)* '}';
constant: 'const' CONSTNAME '=' literal ';';
run: 'run' '{' methodCallStatement '}';
