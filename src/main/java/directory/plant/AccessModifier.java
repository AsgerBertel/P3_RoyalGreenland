package directory.plant;

import com.google.gson.Gson;
import directory.files.Document;
import directory.files.DocumentBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AccessModifier {

    public ArrayList<Integer> documents = new ArrayList<>();

    public AccessModifier(ArrayList<Integer> documentIDs){
        this.documents = documents;
    }

    public AccessModifier(){
    }

    public ArrayList<Integer> getDocumentIDs(){
        return documents;
    }

    public boolean contains(Integer documentID){
        // todo
        return true;
    }

    public void addDocument(Integer ID){
        // todo
    }

    public boolean removeDocument(Integer ID){
        // todo
        return false;
    }

    public static void testJson(){
        AccessModifier am = new AccessModifier();
        Path p = Paths.get("Sample files/Main Files/01_SALTFISK/FL 01 GR_01 Flowdiagram Produktion af saltfisk.pdf");
        Document d = DocumentBuilder.createDocument(p);
        am.documents.add(d.getID());

        Gson g = new Gson();

        String jsonVers = g.toJson(am);

        System.out.println(jsonVers);

        AccessModifier am2 = g.fromJson(jsonVers, AccessModifier.class);

        System.out.println(am2.documents);

        Plant plant = new Plant(1000, "Nuuk", am2);
        String plantJson = g.toJson(plant);

        // Write object to JSON file.
        try (FileWriter writer = new FileWriter("Sample files/allPlants.JSON")){
            g.toJson(plant, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Reader reader = new FileReader("Sample files/allPlants.JSON")){
            Plant plant2 = g.fromJson(reader, Plant.class);
            System.out.println(plant2.getAccessModifier().documents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
