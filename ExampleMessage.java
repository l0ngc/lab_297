
import mcgui.*;

/**
 * Message implementation for ExampleCaster.
 *
 * @author Andreas Larsson &lt;larandr@chalmers.se&gt;
 */
public class ExampleMessage extends Message {
    
    String text;
    int seqnum;
    int id;
    int checksum;
    public ExampleMessage(int sender,String text, int seqnum) {
        super(sender);
        this.id = sender;
        this.text = text;
        this.seqnum = seqnum;
        this.checksum = generateChecksum(text);
    }
    
    /**
     * Returns the text of the message only. The toString method can
     * be implemented to show additional things useful for debugging
     * purposes.
     */
    public String getText() {
        return text;
    }
    
    public int getSeqnum() {
        return seqnum;
    }

    private int generateChecksum(String text) {
        int checksum = 0;
        for (int i = 0; i < text.length(); i++) {
            checksum += (int) text.charAt(i);
        }
        return checksum;
    }

    public static final long serialVersionUID = 0;
}
