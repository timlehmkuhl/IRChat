grammar IRC;

irc: commands;

commands: (nick | user | privmsg |  ping | notice | quit | pong | unknown);

nick: 'NICK' WS para1;

user: 'USER' WS para1 WS para2;

privmsg: 'PRIVMSG' WS para1 WS sentence;

notice: 'NOTICE' WS para1 WS sentence;

ping: 'PING';

pong: 'PONG';

quit: 'QUIT' WS sentence;



para1: STRING | LETTER;
para2: STRING | LETTER;

sentence: STRING (STRING | WS)*;

unknown: LETTER*;


LETTER :  [a-zA-Z] ;

STRING :  LETTER (LETTER | [0-9])* ;


WS: [ ];
