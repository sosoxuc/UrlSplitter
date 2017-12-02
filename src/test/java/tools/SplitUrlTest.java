package tools;
import static org.junit.Assert.*;

import org.junit.Test;

public class SplitUrlTest {

    @Test
    public void test() {
        String url;
        String[] split;

        url = "http://host:8090/path?params";
        split = SplitUrl.regexSplit(url);
        assertEquals(split[0], "http");
        assertEquals(split[1], "host");
        assertEquals(split[2], "8090");
        assertEquals(split[3], "path");
        assertEquals(split[4], "params");

        url = "https://yahoo.com";
        split = SplitUrl.regexSplit(url);
        assertEquals(split[0], "https");
        assertEquals(split[1], "yahoo.com");
        assertEquals(split[2], "");
        assertEquals(split[3], "");
        assertEquals(split[4], "");
        
        url = "http://host:8090/path?params";
        split = SplitUrl.stateSplit(url);
        assertEquals(split[0], "http");
        assertEquals(split[1], "host");
        assertEquals(split[2], "8090");
        assertEquals(split[3], "path");
        assertEquals(split[4], "params");

        url = "https://yahoo.com";
        split = SplitUrl.stateSplit(url);
        assertEquals(split[0], "https");
        assertEquals(split[1], "yahoo.com");
        assertEquals(split[2], "");
        assertEquals(split[3], "");
        assertEquals(split[4], "");
        
    }

}
