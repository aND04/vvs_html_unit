package webapp.utils;

public class AddressTestHelper {

    private String address;
    private String door;
    private String postalCode;

    public AddressTestHelper(String address, String door, String postalCode, String locality) {
        this.address = address;
        this.door = door;
        this.postalCode = postalCode;
        this.locality = locality;
    }

    private String locality;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDoor() {
        return door;
    }

    public void setDoor(String door) {
        this.door = door;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }
}
