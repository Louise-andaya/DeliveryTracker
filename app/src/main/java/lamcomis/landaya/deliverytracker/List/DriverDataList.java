package lamcomis.landaya.deliverytracker.List;

public class DriverDataList {
    String customer_name;
    String dr_number;
    String first_man;
    String second_man;
    String contact_person;
    String status;
    String si_number;

    public String getSi_number() {
        return si_number;
    }

    public void setSi_number(String si_number) {
        this.si_number = si_number;
    }


    private boolean isChecked = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getDr_number() {
        return dr_number;
    }

    public void setDr_number(String dr_number) {
        this.dr_number = dr_number;
    }

    public String getFirst_man() {
        return first_man;
    }

    public void setFirst_man(String first_man) {
        this.first_man = first_man;
    }

    public String getSecond_man() {
        return second_man;
    }

    public void setSecond_man(String second_man) {
        this.second_man = second_man;
    }

    public String getContact_person() {
        return contact_person;
    }

    public void setContact_person(String contact_person) {
        this.contact_person = contact_person;
    }

}
