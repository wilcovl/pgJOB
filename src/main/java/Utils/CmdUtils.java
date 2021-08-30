package Utils;

public class CmdUtils {

    static public boolean hasOption(String[] args, String optionKey) {
        for (String arg : args) {
            if (optionKey.equals(arg)) {
                return true;
            }
        }
        return false;
    }

    static public String getOption(String[] args, String optionKey) {
        for (int i=0; i<args.length; i++) {
            if (optionKey.equals(args[i])) {
                if (args.length > i+1) {
                    return args[i + 1];
                }
            }
        }
        return null;
    }

}
