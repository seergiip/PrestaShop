package metabase.model;

/**
 *
 * @author sergi
 */
public class MetaBase {
    private String data;
    
    
    public MetaBase() {
        data = "";
    }
    
    public MetaBase(String data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return this.data;
    }
}
