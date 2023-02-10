import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import mcgui.*;

/**
 * Priviledged based broad alogorithm
 *
 * @author Jonas Hedlund; Long Cheng;
 */
public class ExampleCaster extends Multicaster {

    // Token init
    String Token;
    int seqnum;
    // Sendbuffer and count init
    int count = 0;
    String[] tosends = new String[50];
    // recv buffer and count init
    int nextdeliver = 1;
    ExampleMessage[] pending = new ExampleMessage[200];
    int pending_length = 0;
    Timer timer;

    public void init() {
        mcui.debug("The network has "+hosts+" hosts!");
        // Token start with Node 0
        if(id == 0){
            bcom.basicsend(id,new ExampleMessage(id, "Token", 1));
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            public void run(){
                token_trans();
            }
        },0 , 10);
        
    }
        
    public void cast(String messagetext) {
        // tosends buffer, count is a tmp value here
        tosends[count] = messagetext;
        mcui.debug("The thing: \""+tosends[count]+"\"is added");
        count += 1;
    }

    public void token_trans(){
        if (this.Token != null){
            broadcast();
            int next_id = (id + 1) % 3;
            bcom.basicsend(next_id ,new ExampleMessage(id, Token, seqnum));
            // mcui.debug("Control Transfer: id: " + id + " trans to " + next_id + " seq " + seqnum);
            this.Token = null;
        }
    }

    public void broadcast(){
        for(int i = 0; i < count; i++){
            mcui.debug("Sent out: \""+tosends[i]+"\"");
            for(int n = 0; n < hosts; n++) {
                bcom.basicsend(n,new ExampleMessage(id, tosends[i], seqnum));
            }
            seqnum+=1;
        }
        // reset tosends
        for(int i = 0; i < tosends.length; i++){
            tosends[i] = null;
        }
        count = 0;
    }

    // store another buffer and fetch out them by the sequence
    public void basicreceive(int peer,Message message) {
        if(((ExampleMessage)message).text.startsWith("Token")){
            this.Token = ((ExampleMessage)message).text;
            this.seqnum = ((ExampleMessage)message).seqnum;
        }
        else{
            pending[pending_length] = (ExampleMessage)message;
            pending_length+=1;
            for(int i = 0; i < pending_length; i++){
                if (pending[i].seqnum == nextdeliver){
                    mcui.debug("Fetch out the " + nextdeliver + "number");
                    mcui.deliver(pending[i].id, 
                                pending[i].text, "received");
                    nextdeliver += 1;
                }
            }
        }
    }

    /**
     * Signals that a peer is down and has been down for a while to
     * allow for messages taking different paths from this peer to
     * arrive.
     * @param peer	The dead peer
     */
    public void basicpeerdown(int peer) {
        mcui.debug("Peer "+peer+" has been dead for a while now!");
    }
}
