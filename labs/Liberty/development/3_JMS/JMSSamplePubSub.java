/*
 * COPYRIGHT LICENSE: This information contains sample code provided in source code form.
 * You may copy, modify, and distribute these sample programs in any form without payment
 * to IBM for the purposes of developing, using, marketing or distributing application
 * programs conforming to the application programming interface for the operating platform
 * for which the sample code is written.
 *
 * Notwithstanding anything to the contrary, IBM PROVIDES THE SAMPLE SOURCE CODE ON
 * AN "AS IS" BASIS AND IBM DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY,
 * SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY OR
 * CONDITION OF NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR OPERATION OF
 * THE SAMPLE SOURCE CODE. IBM HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
 * UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE SOURCE CODE.
 *
 * (C) Copyright IBM Corp. 2001, 2013.
 * All Rights Reserved. Licensed Materials - Property of IBM.
 */


package wasdev.sample.jms.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.annotation.Resource;

/**
 * Servlet implementation class JMSSamplePubSub
 */
@WebServlet("/JMSSamplePubSub")
public class JMSSamplePubSub extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(lookup="jmsTCF") 
	private TopicConnectionFactory cf1;
	
	@Resource(lookup="jmsTopic")
	private  Topic topic;
	

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JMSSamplePubSub() {
		super();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		String strAction = request.getParameter("ACTION");
		PrintWriter out = response.getWriter();
		try{


			if(strAction == null){
				out.println("Please specify the Action");
				out.println("Example : http://<host>:<port>/JMSApp/JMSSamplePubSub?ACTION=nonDurableSubscriber");
			}else if(strAction.equalsIgnoreCase("nonDurableSubscriber")){
				// Create a non durable subscriber and publish and receive the message from topic
				nonDurableSubscriber(request, response);
			}else if(strAction.equalsIgnoreCase("durableSubscriber")){
				// Create a Durable subscriber and publish and receive the message from topic
				durableSubscriber(request, response);
			}else if(strAction.equalsIgnoreCase("publishMessages")){
				// Publish 5 messages to the topic
				publishMessages(request, response);
			}else if(strAction.equalsIgnoreCase("unsubscribeDurableSubscriber")){
				// Unsubscribe the registered durable subscriber
				unsubscribeDurableSubscriber(request, response);
			}else{
				out.println("Incorrect Action Specified, the valid actions are");
				out.println("ACTION=nonDurableSubscriber");
				out.println("ACTION=durableSubscriber");
				out.println("ACTION=publishMessages");
				out.println("ACTION=unsubscribeDurableSubscriber");
			}

		}catch(Exception e){
			out.println("Something unexpected happened, check the logs or restart the server");
			e.printStackTrace();
		}
	}

	/**
	 * scenario: Performs Non-Durable pub/sub flow</br>
	 * Connects to ME using connection factory jmsTCF </br>
	 * Creates a NON-Durable subscriber for topic jmsTopic </br>
	 * Publishes a single message to the topic jmsTopic </br>
	 * Subscriber receives the message from topic jmsTopic and the message is printed on console </br>
	 *
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws Exception if an error occurs.
	 */
	
	
	public void nonDurableSubscriber(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("NonDurableSubscriber Started");

		TopicConnection con = cf1.createTopicConnection();

		con.start();
		TopicSession session = con.createTopicSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

		// create a NON-Durable subscriber
		TopicSubscriber sub = session.createSubscriber(topic);

		// create a topic publisher
		TopicPublisher publisher = session.createPublisher(topic);

		// Publish a message to the topic
		publisher.publish(session.createTextMessage("Liberty PubSub Message"));

		TextMessage msg = (TextMessage) sub.receive(2000);
		if (null == msg) {
			throw new Exception("No message received");
		}else {
			out.println("Received message for non-durable subscriber " + msg);
		}
		if (sub != null)
			sub.close();
		if (con != null)
			con.close();

		out.println("NonDurableSubscriber Completed");

	} // NonDurableSubscriber

	/**
	 * Test scenario: Performs Durable pub/sub flow</br>
	 * Connects to ME using connection factory jmsTCF </br>
	 * Creates durable subscriber(named DURATEST) for topic jmsTopic </br>
	 * Publishes a single message to the topic jmsTopic </br>
	 * Subscriber receives the message from topic jmsTopic </br>
	 *
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws Exception if an error occurs.
	 */
	public void durableSubscriber(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("DurableSubscriber Started");

		// create topic connection
		TopicConnection con = cf1.createTopicConnection();
		con.start();
		TopicSession session = con.createTopicSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

		// create a Durable Subscriber
		TopicSubscriber sub = session.createDurableSubscriber(topic, "DURATEST");

		// create a publisher
		TopicPublisher publisher = session.createPublisher(topic);

		// publish the message
		publisher.publish(session.createTextMessage("Liberty PubSub Message"));


		TextMessage msg = null;
		do {
			msg = (TextMessage) sub.receive(2000);
			if(msg!=null)
				out.println("Received  messages " + msg);
		} while (msg != null);


		if (sub != null)
			sub.close();
		if (con != null)
			con.close();


		out.println("DurableSubscriber Completed");
	}// end of DurableSubscriber

	/**
	 * Test scenario: Publish messages to Topic</br>
	 * Connects to ME using connection factory jmsTCF </br>
	 * Publishes 5 messages to the topic jmsTopic </br>
	 *
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws Exception if an error occurs.
	 */
	public void publishMessages(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("PublishMessage Started");

		TopicConnection con = cf1.createTopicConnection();
		int msgs = 5;

		TopicSession session = con.createTopicSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

		TopicPublisher publisher = session.createPublisher(topic);
		// send 5 messages
		for (int i = 0; i < msgs; i++) {
			publisher.publish(session.createTextMessage("Liberty PubSub Message : " + i));
		}
		if (con != null)
			con.close();
		out.println(msgs+ "Messages published");
		out.println("PublishMessage Completed");
	}// PublishMessage


	/**
	 * Test scenario: Unsubscribe the durable subscriber</br>
	 * Connects to ME using connection factory jmsTCF </br>
	 * Creates/Opens durable subscriber (named DURATEST) for topic jmsTopic </br>
	 * Consumes all messages to the topic jmsTopic </br>
	 * Subscriber unsubscribes from topic jmsTopic </br>
	 *
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws Exception if an error occurs.
	 */
	public void unsubscribeDurableSubscriber(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		out.println("UnsubscribeDurableSubscriber Started");

		TopicConnection con = cf1.createTopicConnection();

		con.start();
		TopicSession session = con.createTopicSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

		TopicSubscriber sub = session.createDurableSubscriber(topic, "DURATEST");
		// Consume all the existing messages for durable subscriber DURATEST
		TextMessage msg = null;
		do {
			msg = (TextMessage) sub.receive(2000);
			if(msg!=null)
				out.println("Received  messages " + msg);
		} while (msg != null);

		if (sub != null)
			sub.close();

		// Unsubscribe the durable subscriber
		session.unsubscribe("DURATEST");

		if (con != null)
			con.close();

		out.println("UnsubscribeDurableSubscriber Completed");
	}//UnsubscribeDurableSubscriber

}

