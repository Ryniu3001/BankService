package bsr.bank.dao.message;

/**
 * Created by marcin on 29.12.16.
 */
public enum OperationEnum {
    przelew(0), wpłata(1), wypłata(2), opłata(3);

    private int value;

    OperationEnum(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public static OperationEnum fromValue(int v){
        for (OperationEnum e: OperationEnum.values()) {
            if (v == e.getValue()) {
                return e;
            }
        }
        return null;
    }
}
