package mytest;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 * <p>
 * The custom runner <code>Parameterized</code> implements parameterized tests. When running a parameterized test class,
 * instances are created for the cross-product of the test methods and the test data elements.
 * </p>
 * 
 */
public class MyParameterized extends Suite {

    private class TestClassRunnerForParameters extends org.junit.runners.BlockJUnit4ClassRunner {
        private final int fParameterSetNumber;

        private final List<Object[]> fParameterList;

        TestClassRunnerForParameters(Class<?> type, List<Object[]> parameterList, int i) throws InitializationError {
            super(type);
            this.fParameterList = parameterList;
            this.fParameterSetNumber = i;
        }

        @Override
        public Object createTest() throws Exception {
            return this.getTestClass().getOnlyConstructor().newInstance(this.computeParams());
        }

        private Object[] computeParams() throws Exception {
            try {
                return this.fParameterList.get(this.fParameterSetNumber);
            } catch (ClassCastException e) {
                throw new Exception(String.format("%s.%s() must return a Collection of arrays.", this.getTestClass()
                        .getName(), MyParameterized.this.getParametersMethod(this.getTestClass()).getName()));
            }
        }

        @Override
        protected String getName() {
            /*
             * The Object[] return in your
             * @Parameters public static Collection<Object[]> data() {}
             */

            Object[] _o = this.fParameterList.get(this.fParameterSetNumber);
            String[] _s = (String[]) _o[0];
            return (String) _s[0];
        }

        @Override
        protected String testName(final FrameworkMethod method) {
            /*
             * The Object[] return in your
             * @Parameters public static Collection<Object[]> data() {}
             */
            Object[] _o = this.fParameterList.get(this.fParameterSetNumber);
            String[] _s = (String[]) _o[0];
            return method.getName() + "_" + (String) _s[0];
        }

        // @Override
        @Override
        protected void validateConstructor(List<Throwable> errors) {
            // validateOnlyOneConstructor(errors);
        }

        @Override
        protected Statement classBlock(RunNotifier notifier) {
            return this.childrenInvoker(notifier);
        }
    }

    private final ArrayList<Runner> runners = new ArrayList<Runner>();

    /**
     * Only called reflectively. Do not use programmatically.
     */
    public MyParameterized(Class<?> klass) throws Throwable {
        super(klass, Collections.<Runner> emptyList());
        List<Object[]> parametersList = this.getParametersList(this.getTestClass());
        for (int i = 0; i < parametersList.size(); i++) {
            this.runners.add(new TestClassRunnerForParameters(this.getTestClass().getJavaClass(), parametersList, i));
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return this.runners;
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> getParametersList(TestClass klass) throws Throwable {
        return (List<Object[]>) this.getParametersMethod(klass).invokeExplosively(null);
    }

    private FrameworkMethod getParametersMethod(TestClass testClass) throws Exception {
        List<FrameworkMethod> methods = testClass.getAnnotatedMethods(Parameters.class);
        for (FrameworkMethod each : methods) {
            int modifiers = each.getMethod().getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                return each;
            }
        }

        throw new Exception("No public static parameters method on class " + testClass.getName());
    }

}
