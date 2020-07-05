package ntou.jt.apbotmessenger;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLog {

	private static final String EXCHANGE_NAME = "exchangeString";
    
   	public static boolean send(String content)
	{
   		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("140.121.197.130");
		factory.setPort(9002);
		try (Connection connection = factory.newConnection();
	             	Channel channel = connection.createChannel()) {
	           	channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
			channel.basicPublish(EXCHANGE_NAME, "", null, content.getBytes("UTF-8"));
	           	System.out.println(" [x] Sent '" + content + "'");
			return true;
	        } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}

   	 public static void main(String[] args)
   	 {
		send("Hello!");
   	 }	

}


