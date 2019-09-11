package lamcomis.landaya.deliverytracker.List;

/**
 * Created by landaya on 3/19/2019.
 */

public class SIList {
    String sinumber;
    boolean checkbox;

    public SIList() {
         /*Empty Constructor*/
    }
    public SIList(String sinumber, boolean status){
        this.sinumber = sinumber;
        this.checkbox = status;
    }
    //Getter and Setter
    public String getSI() {
        return sinumber;
    }

    public void setSI(String sinumber) {
        this.sinumber = sinumber;
    }

    public boolean isCheckbox() {
        return checkbox;
    }

    public void setCheckbox(boolean checkbox) {
        this.checkbox = checkbox;
    }

}