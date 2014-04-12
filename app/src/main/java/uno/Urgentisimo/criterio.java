package uno.Urgentisimo;

import java.util.ArrayList;
import java.util.Map;

import android.provider.ContactsContract;
import android.util.Log;

public class criterio {
    private static final String TAG = "URGENTISIMO : CRITERIO :";
    private String id;
    private Map<String,String> datos;
    private DataBaseHelper db;
    private Map<String, Map<String,String>> actividades;
    private Map<String, Map<String,String>> asignaturas;


    public void criterio(String id, DataBaseHelper db) {
        this.id = id;
        this.db = db;
        this.refresh();
    }

    public void refresh() {
        this.datos = db.getMapSql("select * from criterios where _id = '"+this.id+"'","_id",this.id);
    }

    public String getdato(String dato) {
        return datos.get(dato);
    }

    public Map<String, Map<String,String>> getActividades() {
        this.actividades = db.getMapField("select criterios_actividad.*,actividades.*,criterios.* from criterios_actividad join actividades on criterios_actividad.idactividad = actividades._id join criterios on criterios._id = criterios_actividad.idcriterio where idcriterio = "+this.id+"'","idactividad");
        return this.actividades;
    }
}
