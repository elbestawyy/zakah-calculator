package ntg.project.ZakahCalculator.entity.util;

public enum ZakahStatus {
    NOT_CALCULATED("No calculation performed yet"),
    BELOW_NISAB("Wealth below nisab threshold - No zakah required"),
    HAWL_NOT_COMPLETED("Above nisab but hawl period not completed"),
    ZAKAH_DUE("Zakah calculated and payment is due"),
    PAID("Zakah has been paid");
    private final String description;

    ZakahStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
