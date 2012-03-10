package mytest;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = MyParameterized.class)
public class NewTest {

    private Object[] obj;

    public NewTest(Object[] obj) {
        this.obj = obj;
    }

    @Parameters
    public static Collection<Object[]> data() {
        List<Object[]> ret = new ArrayList<Object[]>();
        String[] p1 = new String[] { "mytest1", "1", "1" };
        String[] p2 = new String[] { "mytest2", "2", "2" };
        String[] p3 = new String[] { "mytest3", "3", "3" };
        ret.add(new Object[] { p1 });
        ret.add(new Object[] { p2 });
        ret.add(new Object[] { p3 });
        return ret;
    }

    @Test
    public void test() {
        assertEquals(obj[1], obj[2]);
    }

}
