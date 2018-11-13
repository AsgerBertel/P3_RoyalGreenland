package directory.plant;

import java.util.ArrayList;

public class AccessModifier {
    private ArrayList<Integer> documents = new ArrayList<>();

    public AccessModifier(){
    }

    public ArrayList<Integer> getDocumentIDs(){
        return documents;
    }

    /**
     * Used to see if a fileID is contained within this plant.AccessModifier
     * Used for displaying the correct files in the GUI.
     * @param ID Document.getID
     */
    public boolean contains(Integer ID){
        for(Integer doc : documents){
            if(doc.equals(ID)){
                return true;
            }
        }
        return false;
    }

    /**
     * Add a document ID to the access modifier.
     * @param ID Document.getID
     */
    public void addDocument(Integer ID){
        if(!contains(ID)){
            documents.add(ID);
        }
        PlantManager.getInstance().updateJsonFile();
    }

    /**
     * Removes a document from Access Modifier if it is in it.
     * @param ID Document.getID
     * @return true if successful.
     */
    public boolean removeDocument(Integer ID){
        if(contains(ID)){
            documents.remove(ID);
            PlantManager.getInstance().updateJsonFile();
            return true;
        }
        return false;
    }
}
