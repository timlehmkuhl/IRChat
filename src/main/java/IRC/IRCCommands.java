package IRC;

import IRC.Antlr.IRCBaseListener;
import IRC.Antlr.IRCParser;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;

public class IRCCommands extends IRCBaseListener {

    IRCMaster ircMaster;
    IRCSlave ircSlave;
    private STGroup templates = new STGroupFile("/Users/timmichaellehmkuhl/InfProjekte/irc2/src/main/java/replies.stg");

    public IRCCommands(IRCMaster ircMaster, IRCSlave ircSlave) {
        this.ircMaster = ircMaster;
        this.ircSlave = ircSlave;
    }

    @Override
    public void exitNick(IRC.Antlr.IRCParser.NickContext ctx) {
        System.out.print(ctx.para1().getText());
        String ret = ircMaster.nick(ctx.para1().getText(), ircSlave.getUser());
        try {
            ircSlave.tell(ret, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exitUser(IRC.Antlr.IRCParser.UserContext ctx) {
        System.out.print(ctx.para2() == null);
        if (ctx.para2() == null) {
            ST st461 = templates.getInstanceOf("ERR_NEEDMOREPARAMS");
            try {
                ircSlave.tell(st461.add("command", "USER").render(), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            ircSlave.tell(ircMaster.addUser(ctx.para1().getText(),
                   ctx.para2().getText(), ircSlave.getClientAdress(), ircSlave), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void exitPrivmsg(IRC.Antlr.IRCParser.PrivmsgContext ctx) {
        if (ctx.para1() == null || ctx.sentence() == null) {
            ST st461 = templates.getInstanceOf("ERR_NEEDMOREPARAMS");
            try {
                ircSlave.tell(st461.add("command", "PRIVMSG").render(), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            ircMaster.sendPrivateMessage(ctx.para1().getText(), ctx.sentence().getText(), ircSlave.getUser(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exitNotice(IRCParser.NoticeContext ctx) {
        if (ctx.para1() == null || ctx.sentence() == null) {
            ST st461 = templates.getInstanceOf("ERR_NEEDMOREPARAMS");
            try {
                ircSlave.tell(st461.add("command", "NOTICE").render(), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            ircMaster.sendPrivateMessage(ctx.para1().getText(), ctx.sentence().getText(), ircSlave.getUser(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void exitQuit(IRCParser.QuitContext ctx) {
        if (ctx.sentence() == null) {
            ST st461 = templates.getInstanceOf("ERR_NEEDMOREPARAMS");
            try {
                ircSlave.tell(st461.add("command", "QUIT").render(), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            ircMaster.removeUser(ircSlave.getUser(), ctx.sentence().getText());
            ircSlave.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exitPing(IRC.Antlr.IRCParser.PingContext ctx) {
        try {
            ircSlave.tell(ircMaster.pong(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
