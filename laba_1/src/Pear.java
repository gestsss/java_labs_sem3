public class Pear extends Food {
    private String size;

    public Pear(String size) {
        super("Груша");
        this.size = size;
    }

    @Override
    public void consume() {
        System.out.println(this + " eaten");
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String toString() {
        return super.toString() + " размера '" + size + "'";
    }

}
