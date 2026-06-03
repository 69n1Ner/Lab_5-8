package theory6.decorator;

public class SurnameImpl extends ImplPerson{
    private Person person;

    public SurnameImpl(Person person){
        this.person = person;
    }

    @Override
    public  void  introduce(){
        System.out.println("Моя фамилия Иванов");
        person.introduce();
    }
}
