package directory.plant;

import directory.files.AbstractFile;
import directory.files.Document;

import java.util.ArrayList;

public class AccessModifier {

    private ArrayList<Document> documents;

    public AccessModifier(ArrayList<Document> documents){
        this.documents = documents;
    }

    public AccessModifier(){

    }

    public ArrayList<Document> getDocuments(){
        return documents;
    }

    public boolean contains(AbstractFile file){
        // todo
        return true;
    }

    public void addDocument(Document document){
        // todo
    }

    public boolean removeDocument(){
        // todo
        return false;
    }







}
