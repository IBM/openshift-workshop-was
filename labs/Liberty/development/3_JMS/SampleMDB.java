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

package wasdev.sample.jms.mdb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;


@MessageDriven
public class SampleMDB implements MessageListener {

	@Resource
	MessageDrivenContext ejbcontext;

	@SuppressWarnings("unused")
	private void setMessageDrivenContext(EJBContext ejbcontext) {

	}

	@PostConstruct
	public void postConstruct() {

	}

	@Override
	public void onMessage(Message message) {
		try {
			System.out.println("Message Received in MDB !!!" + message);
			// send message to MDBREPLYQ
			System.out.println("Sending message to MDBREPLYQ");
			SendMDBResponse(message);
			System.out.println("Message sent to MDBREPLYQ");
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	@Resource(lookup="jndi_JMS_BASE_QCF")
	private QueueConnectionFactory cf1;
	
	@Resource(lookup="jndi/MDBREPLYQ")
	private Queue queue;
	
	public void SendMDBResponse(Message msg) throws Exception {
		System.out
		.println("**************************************************************************");
		System.out.println("Test testQueueSendMessageMDB Started from MDB");

		System.out.println("QCF and Queue lookup completed");
		QueueConnection con = cf1.createQueueConnection();
		con.start();
		System.out.println("QueueConnection Created");
		QueueSession sessionSender = con.createQueueSession(false,
				javax.jms.Session.AUTO_ACKNOWLEDGE);
		System.out.println("QueueSession Created");

		QueueSender send = sessionSender.createSender(queue);
		System.out.println("QueueSender Created");

		send.send(msg);
		System.out.println("Message sent to Queue MDBREPLYQ");

		if (con != null)
			con.close();
		System.out.println("Test testQueueSendMessageMDB Completed");

	}
}

