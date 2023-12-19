public abstract class Food implements Consumable {
    String name = null;//Переменная экземпляра name,для хранения имени еды
    public Food(String name) {
        this.name = name;
    } // Принимает имя в качестве параметра и присваивает его полю name.
    public boolean equals(Object arg0) { // Переопределяет метод equals из класса Object. Он сравнивает переданный объект arg0 с текущим объектом this
        if (!(arg0 instanceof Food)) return false; // Шаг 1
        if (name==null || ((Food)arg0).name==null) return false; // Шаг 2
        return name.equals(((Food)arg0).name); // Шаг 3
    }
    public String toString() {
        return name;
    }//Переопределяет метод toString из класса Object. Возвращает строковое представление имени еды.
    public String getName() {
        return name;
    }//Возвращает имя текущего объекта
    public void setName(String name) {//Устанавливает имя текущего объекта в переданное значение name.
        this.name = name;
    }
}
