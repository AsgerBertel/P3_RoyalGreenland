package directory.plant;

import java.util.Objects;

public class Plant {

    private int id;
    private String name;
    private AccessModifier accessModifier;

    public Plant(int id, String name, AccessModifier accessModifier) {
        this.id = id;
        this.name = name;
        this.accessModifier = accessModifier;
    }

    public AccessModifier getAccessModifier(){
        return this.accessModifier;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        PlantManager.getInstance().updateJsonFile();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        PlantManager.getInstance().updateJsonFile();
    }

    @Override

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plant plant = (Plant) o;
        return id == plant.id &&
                Objects.equals(name, plant.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public String toString() {
        return id +" - "+ name;

    }
}
