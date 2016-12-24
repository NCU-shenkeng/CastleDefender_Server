package shenken.net.udp.server;

/**
 * 伺服器接受訊息後動作
 */
public interface ReceiveAction
{
	void run(String msg);
}
