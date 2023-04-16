package dam.coso.pfg_ht_serralertas;

import android.content.Context;
import android.content.res.Resources;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import dam.coso.pfg_ht_serralertas.data.DbAlertas;

public class TestDbAlertas {
    private DbAlertas dbAlertas;

    @Before
    public void setUp() throws Exception {

        Context testContext = new TestContext(new Resources(null, null, null));
        dbAlertas = new DbAlertas(testContext);
    }

    @After
    public void tearDown() throws Exception {
        dbAlertas.close();
    }
    @Test
    public void insertarPerfil_isCorrect() {
        // Insertar una alerta en la base de datos
        long id = dbAlertas.insertarPerfil("Perfil prueba");

        // Verificar que su id es mayor que cero
        assertTrue(id > 0);

    }

}
