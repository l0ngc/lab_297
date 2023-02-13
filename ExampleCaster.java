import java.util.ArrayList;
import java.lang.Math.*;
import mcgui.*;

public class ExampleCaster extends Multicaster {
    // Token init
    String Token;
    int seqnum;
    ArrayList<String> tosends = new ArrayList<String>();
    // first buffer size
    int first_buffer_size = 20;
    // recv buffer and count init
    int nextdeliver = 1;
    ExampleMessage[] pending = new ExampleMessage[200];
    int pending_length = 0;

    public void init() {
        mcui.debug("The network has "+hosts+" hosts!");
        // Token start with Node 0
        if(id == 0){
            bcom.basicsend(id,new ExampleMessage(id, "Token", 1));
        }
    }
        
    public void cast(String messagetext) {
        tosends.add(messagetext);
        mcui.debug("The thing: \""+ tosends.get(tosends.size() - 1) +"\"is added");

        if (Token != null){
            mcui.debug("Token is here~");
            broadcast();
        }
    }

    public void broadcast(){

        int to_be_sent = Math.min(first_buffer_size, tosends.size());
        
        if (tosends.size() != 0){
            for(int i = 0; i < to_be_sent; i++){
                mcui.debug("Sent out: \""+ tosends.get(0) + "\"");
                for(int n = 0; n < hosts; n++) {
                    bcom.basicsend(n,new ExampleMessage(id, tosends.get(0), seqnum));
                }
                mcui.debug("come to transfer"+to_be_sent);
                
                tosends.remove(0);
                seqnum += 1;
                mcui.debug("current seqnum = " + seqnum + " deliver num is " + nextdeliver);
            }
        }
        token_trans();
    }

    public void token_trans() {
        int next_id = (id + 1) % 3;
        bcom.basicsend(next_id, new ExampleMessage(id, Token, seqnum));
        // mcui.debug("Control Transfer: id: " + id + " trans to " + next_id + " seq: " + seqnum);
        this.Token = null;
    }

    public int generateChecksum(String text) {
        int checksum = 0;
        for (int i = 0; i < text.length(); i++) {
            checksum += (int) text.charAt(i);
        }
        return checksum;
    }

    // store another buffer and fetch out them by the sequence
    public void basicreceive(int peer, Message message) {
        // check sum
        ExampleMessage receivedMessage = (ExampleMessage)message;
        if (receivedMessage.checksum != generateChecksum(receivedMessage.text)) {
            mcui.debug("Message corrupted, ignoring");
            return;
        }
        // receive token
        if(receivedMessage.text.startsWith("Token")){
            this.Token = receivedMessage.text;
            this.seqnum = receivedMessage.seqnum;
            broadcast();
        }
        // deliver message
        else{
            pending[pending_length] = receivedMessage;
            pending_length += 1;
            for(int i = 0; i < pending_length; i++){
                if (pending[i].seqnum == nextdeliver){
                    mcui.debug("Deliver the " + nextdeliver + "number");
                    mcui.deliver(pending[i].id, pending[i].text, "received");
                    nextdeliver += 1;
                }
            }
        }
    }

    public void basicpeerdown(int peer) {
        mcui.debug("Peer "+peer+" has been dead for a while now!");
    }
}
