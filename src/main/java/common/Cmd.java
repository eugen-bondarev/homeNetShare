package common;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Cmd {
    private List<String> args;
    private String cmd;

    private static final String DELIMITER = "><";

    public static Cmd fromString(String str) {
        List<String> splitMsg = Arrays.stream(str.split(Pattern.quote(DELIMITER))).toList();
        List<String> args = splitMsg.subList(1, splitMsg.size());
        String cmd = splitMsg.get(0);
        return new Cmd(cmd, args);
    }

    public Cmd(String cmd, List<String> args) {
        this.cmd = cmd;
        this.args = args;
    }

    public Cmd(String cmd, String arg) {
        this(cmd, Arrays.asList(new String[] { arg }));
    }

    public String toString() {
        String fullCmd = cmd;
        for (int i = 0; i < args.size(); ++i) {
            fullCmd += DELIMITER + args.get(i);
        }
        return fullCmd;
    }

    public String getCmd() {
        return cmd;
    }

    public List<String> getArgs() {
        return args;
    }
}
