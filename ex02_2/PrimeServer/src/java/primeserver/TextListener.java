/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package primeserver;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;



/**
 *
 * @author sarun
 */
public class TextListener implements MessageListener {
    private MessageProducer replyProducer;
    private Session session;
    private PrimeCal primecal;
    
    public TextListener(Session session) {
              
        this.session = session;
        try {
            replyProducer = session.createProducer(null);
        } catch (JMSException ex) {
            Logger.getLogger(TextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void onMessage(Message message) {
        TextMessage msg = null;
        PrimeCal primecal = new PrimeCal();

        try {
            if (message instanceof TextMessage) {
                msg = (TextMessage) message;
                System.out.println("Reading message: " + msg.getText());
            } else {
                System.err.println("Message is not a TextMessage");
            }
            String [] range =msg.getText().split(",");
            
            int amount = 0;
            for(int i=Integer.parseInt(range[0]);i<Integer.parseInt(range[1]);i++){
                if(PrimeCal.isPrime(i)){ 
                    amount++;
                }
                
            }
            // set up the reply message 
            String responseText = "the number of primes between " + range[0]+" and "+range[1]+"is "+amount;
            TextMessage response = session.createTextMessage(responseText); 
            response.setJMSCorrelationID(message.getJMSCorrelationID());
            System.out.println("sending message "+responseText);
            replyProducer.send(message.getJMSReplyTo(), response);
        } catch (JMSException e) {
            System.err.println("JMSException in onMessage(): " + e.toString());
        } catch (Throwable t) {
            System.err.println("Exception in onMessage():" + t.getMessage());
        }
        
    }
}
