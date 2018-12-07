package directory.plant;

import directory.FileTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccessModifierTest extends FileTester {
    private AccessModifier am;
    private Plant plant;


    @BeforeEach
    void initEach(){
        am = new AccessModifier();
        plant = new Plant(1000,"Fabrik", am);
        plant.getAccessModifier().addDocument(100);
        plant.getAccessModifier().addDocument(105);
    }

    @Test
    void getDocumentIDs() {
        assertTrue(plant.getAccessModifier().getDocumentIDs().contains(100));
        assertTrue(plant.getAccessModifier().getDocumentIDs().contains(105));
        assertTrue(!plant.getAccessModifier().getDocumentIDs().contains(102));
    }

    @Test
    void contains() {
        assertTrue(plant.getAccessModifier().contains(105));
        assertTrue(!plant.getAccessModifier().contains(102));
    }

    @Test
    void addDocument() {
        plant.getAccessModifier().addDocument(107);
        assertTrue(plant.getAccessModifier().contains(107));
    }

    @Test
    void removeDocument() {
        plant.getAccessModifier().addDocument(107);
        assertTrue(plant.getAccessModifier().contains(107));
        plant.getAccessModifier().removeDocument(107);
        assertTrue(!plant.getAccessModifier().contains(107));
    }
}