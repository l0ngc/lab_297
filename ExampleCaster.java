import java.util.ArrayList;
import mcgui.*;

/**
 * Simple example of how to use the Multicaster interface.
 *
 * @author Andreas Larsson &lt;larandr@chalmers.se&gt;
 */
public class ExampleCaster extends Multicaster {

    // Token init
    String Token;
    int seqnum;
    // Sendbuffer and count init
    // int ptr_lev1 = 0;
    // String[] tosends_lev1 = new String[20];
    // int ptr_lev2 = 0;
    // String[] tosends_lev2 = new String[200];

    List<String> tosends = new ArrayList<String>();

    // recv buffer and count init
    int nextdeliver = 1;
    ExampleMessage[] pending = new ExampleMessage[200];
    int pending_length = 0;

// check the buffers

    public void init() {
        mcui.debug("The network has "+hosts+" hosts!");
        // Token start with Node 0
        if(id == 0){
            bcom.basicsend(id,new ExampleMessage(id, "Token", 1));
        }
    }
        
    public void cast(String messagetext) {
        // tosends buffer, count is a tmp value here
        tosends.add(messagetext);
        // tosends_lev2[ptr_lev2] = messagetext;
        mcui.debug("The thing: \""+ tosends[ptr_lev2] +"\"is added");
        // ptr_lev2 += 1;

        if (Token != null){
            mcui.debug("Token is here~");
            broadcast();
        }
    }

    public void broadcast(){
        // load first level beffer
        // for(int i = 0; i < min(20, ptr_lev2); i++){
        //     tosends_lev1[i] = tosends_lev2[(i + ptr_lev2)]
        //     ptr_lev1 += 1;
        // }
        // for(int i = 0; i < min(20, ptr_lev2); i++){
        //     tosends_lev1[i] = tosends_lev2[(i + ptr_lev2)]
        //     ptr_lev1 += 1;
        // }
        // how to reset lev2 cache

        for(int i = 0; i < 20; i++){
            mcui.debug("Sent out: \""+tosends.get(i)+"\"");
            for(int n = 0; n < hosts; n++) {
                bcom.basicsend(n,new ExampleMessage(id, tosends.get(i), seqnum));
            }
            seqnum+=1;
        }
        for(int i = 0; i < 20; i++){
            tosends.remove(0);
        }
        // // reset first level tosends
        // for(int i = 0; i < tosends_lev1.length; i++){
        //     tosends_lev1[i] = null;
        // }
        // ptr_lev1 = 0;

        // reset second level tosends
        // 
        
        // transfer Token
        token_trans();

    }
    public void token_trans() {
        int next_id = (id + 1) % 3;
        bcom.basicsend(next_id ,new ExampleMessage(id, Token, seqnum));
        mcui.debug("Control Transfer: id: " + id + " trans to " + next_id + " seq " + seqnum);
        this.Token = null;
    }

    // store another buffer and fetch out them by the sequence
    public void basicreceive(int peer,Message message) {
        if(((ExampleMessage)message).text.startsWith("Token")){
            if (tosends.isEmpty()){
                token_trans();
            }
            else{
                this.Token = ((ExampleMessage)message).text;
                this.seqnum = ((ExampleMessage)message).seqnum;
            }
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
