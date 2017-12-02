package tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitUrl {

    // Regulat expression pattern for splitting
    private final static String urlPattern = "^(([^:/?#]+):)?(//([^/?#]*))/?([^?#]*)(\\?([^#]*)?)?(#(.*))?";
    private final static Pattern pattern = Pattern.compile(urlPattern);

    /**
     * Split URL using regular expressions
     * @param url 
     * @return Array of strings { scheme, host, port, path, params }
     */
    public static String[] regexSplit(String url) {
        Matcher matcher = pattern.matcher(url);
        matcher.find();
        String scheme = matcher.group(2);
        String addr = matcher.group(4);
        String host = addr.contains(":") ? addr.split(":")[0] : addr;
        String port = addr.contains(":") ? addr.split(":")[1] : "";
        String path = matcher.group(5) != null ? matcher.group(5) : "";
        String params = matcher.group(7) != null ? matcher.group(7) : "";
        return new String[] { scheme, host, port, path, params };
    }

    /**
     * Split URL using state machine
     * @param url 
     * @return Array of strings { scheme, host, port, path, params }
     */
    public static String[] stateSplit(String url) {
        String[] data = new String[5];
        State state = ParserState.START;
        while (!state.equals(ParserState.END)) {
            StateWrapper stateWrapper = state.next(url, data);
            state = stateWrapper.state;
            url = stateWrapper.url;
        }
        return data;
    }

    public static void main(String[] args) {
        String url = args[0];
        long start;

        start = System.currentTimeMillis();
        String[] splitRegex = regexSplit(url);
        long timeRegex = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        String[] splitState = stateSplit(url);
        long timeState = System.currentTimeMillis() - start;

        System.out.println(splitRegex[0]);
        System.out.println(splitRegex[1]);
        System.out.println(splitRegex[2]);
        System.out.println(splitRegex[3]);
        System.out.println(splitRegex[4]);
        System.out.println("Regex: " + timeRegex + "msec");
        System.out.println("State: " + timeState + "msec");

    }
}

/**
 * State interface
 */
interface State {
    StateWrapper next(String url, String[] data);
}

/**
 * Class for saving current state and remeining url
 */
class StateWrapper {
    State state;
    String url;

    public StateWrapper(ParserState state) {
        this.state = state;
    }

    public StateWrapper(ParserState state, String url) {
        this.state = state;
        this.url = url;
    }
}

/**
 * Actual states
 */
enum ParserState implements State {
    START {
        public StateWrapper next(String url, String[] data) {
            return new StateWrapper(SCHEME, url);
        }
    },
    SCHEME {
        public StateWrapper next(String url, String[] data) {
            //Extract scheme
            if (url.contains("://")) {
                String[] split = url.split("://");
                data[0] = split[0];
                url = split[1];
            } else {
                data[0] = "";
            }
            return new StateWrapper(ADDR, url);
        }
    },
    ADDR {
        public StateWrapper next(String url, String[] data) {
            // Extract address "host:port"
            String[] split = url.split("\\?|/|#", 2);
            String addr = split[0];
            data[1] = addr.contains(":") ? addr.split(":")[0] : addr;
            data[2] = addr.contains(":") ? addr.split(":")[1] : "";
            url = split.length > 1 ? split[1] : "";
            return new StateWrapper(PATH, url);
        }
    },
    PATH {
        public StateWrapper next(String url, String[] data) {
            // Extract path
            String[] split = url.split("\\?|#", 2);
            data[3] = split[0];
            url = split.length > 1 ? split[1] : "";
            return new StateWrapper(PARAMS, url);
        }
    },
    PARAMS {
        public StateWrapper next(String url, String[] data) {
            // Extract params
            data[4] = url;
            url = "";
            return new StateWrapper(END, url);
        }
    },
    END {
        public StateWrapper next(String url, String[] data) {
            return new StateWrapper(this, "");
        }
    };
}
