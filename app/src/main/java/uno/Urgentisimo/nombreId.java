package uno.Urgentisimo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class nombreId implements Parcelable {
	private static final String TAG = "URGENTISIMO : NOMBREID CLASS :";
	private String id;
	private String nombre;
	private String descripcion;
	
	public nombreId() {
		id = "";
		nombre = "";
		descripcion = "";
	}
	public nombreId(String elid, String elnombre, String descripcion) {
		this.id = elid;
		this.nombre = elnombre;
		this.descripcion = descripcion;
	}
	public String getNombre() {
		return nombre;
	}
	public String getId() {
		return id;
	}
	public void setNombre(String elnombre) {
		this.nombre = elnombre;
	}
	public void setId (String elid) {
		this.id = elid;
	}
	public void setDescripcion(String desc) {
		this.descripcion = desc;
	}
	public String getDescripcion() {
		return this.descripcion;
	}
	public void printData() {
		Log.v(TAG, id+":"+nombre+":"+descripcion);
	}
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		Log.v(TAG, "writeToParcel..."+ flags);
	      dest.writeString(nombre);
	      dest.writeString(id);
	      dest.writeString(descripcion);
	}
    public void ParcelData(Parcel source){
        /*
         * Reconstruct from the Parcel
         */
        Log.v(TAG, "ParcelData(Parcel source): time to put back parcel data");
        id = source.readString();
        nombre = source.readString();
        descripcion = source.readString();
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public nombreId createFromParcel(Parcel source) {
            return new nombreId();
      }
      public nombreId[] newArray(int size) {
            return new nombreId[size];
      }
    };
	
}
