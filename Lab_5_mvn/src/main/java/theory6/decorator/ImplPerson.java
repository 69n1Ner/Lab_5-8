package theory6.decorator;

public class ImplPerson implements Person{
    @Override
    public void introduce() {
        System.out.println("Меня зовут Ваня");
    }
}
