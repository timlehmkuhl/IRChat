grammar IRC;

irc: commands;

commands: (nick | user | privmsg |  ping | notice | quit | pong | join | part | topic | userLong| unknown);

nick: 'NICK' WS para1;

user: 'USER' WS para1 WS para2;

userLong: 'USER' WS para1 WS '* *' WS ':' sentence;
//USER elisabeth * * :Chloe Elisabeth Price

privmsg: 'PRIVMSG' WS para1 WS ':' sentence;

notice: 'NOTICE' WS para1 WS sentence;

ping: 'PING';

pong: 'PONG';

quit: 'QUIT' WS sentence;

join: 'JOIN' WS channelname;

part: 'PART' WS channelname;

topic: 'TOPIC' WS channelname WS ':' sentence;


para1: (STRING | LETTER) | channelname;
para2: STRING | LETTER;
channelname: '#'  (STRING | LETTER | ZEICHEN);

sentence: (STRING |LETTER) | (STRING | WS | LETTER)*;

unknown: para1;

ZEICHEN: ('-' | '!');
LETTER :  [a-zA-Z] ;

STRING :  LETTER (LETTER | [0-9] | ZEICHEN)* ;


WS: [ ];
