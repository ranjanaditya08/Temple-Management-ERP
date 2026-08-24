public class Address {

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String district;
    private String state;
    private String pincode;


    public Address(
            String addressLine1,
            String addressLine2,
            String city,
            String district,
            String state,
            String pincode) {

        this.addressLine1 = validateRequiredFields(addressLine1, "addressLine1", 150);
        this.addressLine2 = validateOptionalFields(addressLine2, "addressLine2", 150);
        this.city = validateRequiredFields(city, "city", 50);
        this.district = validateRequiredFields(district, "district", 50);
        this.state = validateRequiredFields(state, "state", 50);
        this.pincode = validatePincode(pincode);
    }

    private String validateRequiredFields(String value, String filedName, int maxLength) {
        if (value == null || value.trim().length() == 0) {
            throw new IllegalArgumentException(filedName + " is required");
        }
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(filedName + " must not exceed " + maxLength + " characters");
        }
        return value;
    }

    private String validateOptionalFields(String value, String filedName, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(filedName + " must not exceed " + maxLength + " characters");
        }
        return value;
    }

    private String validatePincode(String pincode) {
        if (pincode == null || pincode.trim().length() == 0) {
            throw new IllegalArgumentException("PIN is required");
        }
        String noramlized = pincode.replaceAll("\\s","");
        if (noramlized.length() != 6 || !noramlized.matches("^[1-9]\\d{5}$")) {
            throw new IllegalArgumentException("PIN must be 6 digits");
        }
        return noramlized;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }


    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }


    public String getPincode() {
        return pincode;
    }

}