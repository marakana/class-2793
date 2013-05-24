
package net.callmeike.android.sandbox.tagview;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;


public class BaseActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Class<?> klass;
        switch (item.getItemId()) {
            case R.id.menu_one:
                klass = ActivityOne.class;
                break;

            case R.id.menu_two:
                klass = ActivityTwo.class;
                break;

            case R.id.menu_tags:
                klass = TagViewActivity.class;
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        System.out.println("Got item: " + item.getItemId() + ", " + klass);

        Intent i = new Intent(this, klass);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);

        return true;
    }

}
