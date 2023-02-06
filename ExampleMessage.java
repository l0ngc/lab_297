
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
    public ExampleMessage(int sender,String text, int seqnum) {
        super(sender);
        this.id = sender;
        this.text = text;
        this.seqnum = seqnum;

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
    public static final long serialVersionUID = 0;
}
