package uno.Urgentisimo;

import java.util.Map;

public class actividadExtra extends actividad {

    private actividadExtra(DataBaseHelper db, String id) {
        this.id = id;
        this.db = db;
    }

    private Map<String, Map<String,String>> alumnos;

    public Map<String, Map<String,String>> getAlumnos() {
        return this.alumnos;
    }
}
