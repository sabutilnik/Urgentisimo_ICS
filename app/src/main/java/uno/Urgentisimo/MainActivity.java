package uno.Urgentisimo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;





public class MainActivity extends Activity {
  
private static final String TAG = "URGENTISIMO : Main Activity ";

/** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_fragments);
    ActionBar actionbar = getActionBar();
    actionbar.show();  
    
  } 
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      case R.id.backup_database:
          // Handle activity menu item
    	  Log.v(TAG,"click en backup database");
          return true;
      default:
          // Handle fragment menu items
          return super.onOptionsItemSelected(item);
      }
  }
  
  
}  