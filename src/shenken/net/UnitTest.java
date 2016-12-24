package shenken.net;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
	{ 
		shenken.net.udp.UnitTest.class,
	}
)
public class UnitTest
{

}
