import java.time.LocalDate;

public class Organization {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int annualTurnover; //Значение поля должно быть больше 0
    private long employeesCount; //Значение поля должно быть больше 0
    private OrganizationType type; //Поле может быть null
    private Address postalAddress; //Поле не может быть null

    public Organization( String name,int annualTurnover, Coordinates coordinates, LocalDate creationDate, long employeesCount, Long id, Address postalAddress, OrganizationType type) {
        this.annualTurnover = annualTurnover;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.employeesCount = employeesCount;
        this.id = id;
        this.name = name;
        this.postalAddress = postalAddress;
        this.type = type;
    }
    public Organization(String string){
        super();
    }
}