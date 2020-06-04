grammar kdl;

@header {
package main.com.xarql.kdl;
}

// skip over whitespace
WS : [ \t\r\n]+ -> skip;

DIGIT      : [0-9];
UPLETTER   : [A-Z];
DNLETTER   : [a-z];
LETTER     : UPLETTER | DNLETTER;
ALPHANUM   : LETTER | DIGIT;
UNDERSCORE : '_';

CLASSNAME : UPLETTER LETTER+;
PROCNAME  : DNLETTER ALPHANUM+;
CONSTNAME : UPLETTER (UPLETTER | DIGIT | UNDERSCORE0

ESCAPED_QUOTE : '\\"';

// literals
string : '"' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '"';
number: DIGIT+;
bool: 'true' | 'false';
literal: number | bool | string;

type: basetype | CLASSNAME;
basetype: 'boolean' | 'int' | 'string';

source: clazz;
clazz: 'class' CLASSNAME '{' constant* '}';
constant: 'const' CONSTNAME '=' literal ';';
