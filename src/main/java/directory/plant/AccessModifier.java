package directory.plant;

import java.util.ArrayList;

public class AccessModifier {

    public ArrayList<Integer> documents = new ArrayList<>();

    public AccessModifier(){
    }

    public ArrayList<Integer> getDocumentIDs(){
        return documents;
    }

    public boolean contains(Integer ID){
        for(Integer doc : documents){
            if(doc == ID){
                return true;
            }
        }
        return false;
    }

    public void addDocument(Integer ID){
        if(!contains(ID)){
            documents.add(ID);
        }
    }

    public boolean removeDocument(Integer ID){
        if(contains(ID)){
            documents.remove(ID);
            return true;
        }
        return false;
    }
}
