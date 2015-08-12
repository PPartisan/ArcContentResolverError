package com.werdpressed.partisan.arccontentresolvererror;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    private static final int REQUEST_OPEN = 15;

    private String content, title;
    private Intent mIntent;
    private EditorFragment mEditorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (getFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, EditorFragment.newInstance())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mEditorFragment = (EditorFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
        if (mEditorFragment == null) throw new NullPointerException(getString(R.string.editor_fragment_null));

        int id = item.getItemId();
        switch (id) {
            case R.id.load_file:
                open();
                break;
            case R.id.save_file:
                saveTask();
                break;
            case R.id.share_file:
                shareTask();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void open(){
        mIntent = new Intent().setType("text/plain");
        startActivityForResult(mIntent.setAction(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE), REQUEST_OPEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = null;

            if (resultData != null) {
                uri = resultData.getData();

                if (requestCode == REQUEST_OPEN) {
                    Cursor c = getContentResolver().query(uri, null, null, null, null);

                    if (c != null && c.moveToFirst()) {
                        title = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                        content = readFromFile(uri);

                        if (title == null || title.equals("")) {
                            Log.e(LOG_TAG, getString(R.string.chrome_book_fail_generic));
                            Toast.makeText(this, getString(R.string.chrome_book_fail_generic), Toast.LENGTH_SHORT).show();
                            title = getString(R.string.app_name);
                        }

                        getSupportActionBar().setTitle(title);
                        mEditorFragment.setEditTextContent(content.trim());
                        mEditorFragment.setUri(uri.toString());

                        if (!c.isClosed()) {
                            c.close();
                        }
                    }
                }
            }
        }
    }

    private String readFromFile(Uri uri) {

        String ret = "";

        try {
            InputStream inputStream = this.getContentResolver().openInputStream(uri);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                    stringBuilder.append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(LOG_TAG, getString(R.string.cannot_locate_file) + e.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, getString(R.string.cannot_read_file) + e.toString());
        }

        return ret;
    }

    private void shareTask() {
        mIntent = new Intent(Intent.ACTION_SEND);
        mIntent.putExtra(Intent.EXTRA_TEXT, mEditorFragment.getEditTextContent());
        mIntent.setType("text/plain");
        startActivity(Intent.createChooser(mIntent, getString(R.string.action_share)));
    }

    private void saveTask() {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(Uri.parse(mEditorFragment.getUri()));
            outputStream.write(mEditorFragment.getEditTextContent().getBytes());
            outputStream.close();
            Log.e(LOG_TAG + " saveTask()", getString(R.string.success_save_task));
            Toast.makeText(this, getString(R.string.success_save_task), Toast.LENGTH_SHORT).show();
        } catch (IOException e){
            e.printStackTrace();
            Log.e(LOG_TAG + " saveTask()", getString(R.string.fail_save_task));
            Toast.makeText(this, getString(R.string.fail_save_task), Toast.LENGTH_SHORT).show();
        }
    }
}
