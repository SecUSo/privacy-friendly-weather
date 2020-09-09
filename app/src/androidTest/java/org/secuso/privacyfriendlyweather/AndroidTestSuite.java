package org.secuso.privacyfriendlyweather;

        import org.junit.runner.RunWith;
        import org.junit.runners.Suite;
        import org.secuso.privacyfriendlyweather.database.DatabaseTest;


@RunWith(Suite.class)
@Suite.SuiteClasses({DatabaseTest.class})
public class AndroidTestSuite {
}
