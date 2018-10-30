package directory.plant;

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



}
