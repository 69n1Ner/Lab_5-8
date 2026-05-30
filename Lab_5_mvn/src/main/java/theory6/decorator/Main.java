package theory6.decorator;


public class Main {
    public static void main(String[] args) {
        Person person = new ImplPerson();
        person = new SurnameImpl(person);
        person.introduce();


    }
}
