package shenken.net.udp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
	{ 
		shenken.net.udp.client.UnitTest.class,
		shenken.net.udp.server.UnitTest.class,
	}
)
public class UnitTest
{

}
