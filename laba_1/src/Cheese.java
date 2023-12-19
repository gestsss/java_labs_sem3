public class Cheese extends Food { // отвечает наследование интерфейса
    public Cheese() {
        super("Сыр");
    }
    public void consume() {
        System.out.println(this + " не съеден");
    }
}