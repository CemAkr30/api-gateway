package tr.gov.gib.ebyn.api.gateway.enums;

public enum ServiceStatusCode {
    SUCCESS("1000"),
    ERROR_INVALID("1005"),
    ERROR_OTHER("2000");

    private final String code;

    ServiceStatusCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ServiceStatusCode fromCode(String code) {
        for (ServiceStatusCode status : ServiceStatusCode.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
