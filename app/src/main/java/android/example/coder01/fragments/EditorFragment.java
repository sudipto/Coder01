package android.example.coder01.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.example.coder01.R;
import android.example.coder01.utilities.NetworkUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class EditorFragment extends Fragment {
    private JSONObject mPostData;
    private Boolean serverAvailable = false;
    private EditText mEditorEditText;
    private TextView mFileNameTextView, mStatusTextView;
    private View view;
    private Button mTabButton;
    private OnInputListener callback;
    private String strFileName = "newfile.txt";
    private String program_lang = null;
    private String mCodeInput, haveInput = "false", userInput = "";
    private ProgressBar mIndeterminateProgress;
    private static final int WRITE_REQUEST_CODE = 43;
    private static final int EDIT_REQUEST_CODE = 44;

    public void setOnInputListener(OnInputListener callback) {
        this.callback = callback;
    }

    /**
     * Fragment will use this to communicate with other fragments.
     * Implement this interface in the MainActivity
     */
    public interface OnInputListener {
        // We will send the edittext input
        void onEditorInput(String text);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate fragment_editor layout for this fragment
        view = inflater.inflate(R.layout.fragment_editor, container, false);

        // Find the edittext
        mEditorEditText = view.findViewById(R.id.et_editor);
        mEditorEditText.setSelection(0);
        mEditorEditText.addTextChangedListener(new TextWatcher() {
            char mchar;
            int mstart;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mchar = s.charAt(start);
                mstart = start;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mchar == '\n') {
                    Log.v("textwatcher","inserted space");
                    s.insert(mstart + 1, "    ");
                }
            }
        });

        // Find the filename textview and status textview
        mFileNameTextView = view.findViewById(R.id.tv_filename);
        mStatusTextView = view.findViewById(R.id.tv_status);

        // Find the progress bar
        mIndeterminateProgress = view.findViewById(R.id.indeterminateBar);

        // Set the initial filename
        mFileNameTextView.setText(strFileName);

        // Find the TAB button
        mTabButton = view.findViewById(R.id.bt_tab);
        mTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditorEditText.getText()
                        .insert(mEditorEditText.getSelectionStart(),"    ");
            }
        });

        // return the view created
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_editor, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.run_code:
                // Check if the language is set or not
                if (program_lang == null) {
                    mStatusTextView.setText("Set the programming language.");
                    return true;
                }
                // Check if server is available or not
                if (serverAvailable != true) {
                    checkServerStatus();
                }
                runCode();
                return true;
            case R.id.lang_c:
                mStatusTextView.setText("C programming language selected.");
                program_lang = "C";
                strFileName = "Program.c";
                mFileNameTextView.setText(strFileName);
                setupEditor(program_lang);
                return true;
            case R.id.lang_java:
                mStatusTextView.setText("Java programming language selected.");
                program_lang = "java";
                strFileName = "Test.java";
                mFileNameTextView.setText(strFileName);
                setupEditor(program_lang);
                return true;
            case R.id.lang_cpp:
                mStatusTextView.setText("Cpp programming language selected.");
                program_lang = "cpp";
                strFileName = "Program.cpp";
                mFileNameTextView.setText(strFileName);
                setupEditor(program_lang);
                return true;
            case R.id.lang_p2:
                mStatusTextView.setText("Python 2.7 programming language selected.");
                program_lang = "python";
                strFileName = "Program.py";
                mFileNameTextView.setText(strFileName);
                setupEditor(program_lang);
                return true;
            case R.id.lang_p3:
                mStatusTextView.setText("Python 3.7 programming language selected.");
                program_lang = "python3";
                strFileName = "Program.py";
                mFileNameTextView.setText(strFileName);
                setupEditor(program_lang);
                return true;
            case R.id.open_file:
                editDocument();
                return true;
            case R.id.save_file:
                createFile("text/*",strFileName);
                return true;
            case R.id.user_input:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Give user input: ");
                // Set up the input
                final EditText input = new EditText(getContext());
                // Specify the type of input expected; this, for example, sets the input as a
                // password, and will mask the text
                input.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userInput = input.getText().toString();
                        haveInput = "true";
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        userInput = "";
                        haveInput = "false";
                    }
                });
                builder.show();
                return true;
            case R.id.editor_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mEditorEditText.getText());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Check the server status
    private void checkServerStatus() {
        URL statusCheckUrl = NetworkUtils.buildUrl(1);
        new CheckServerStatusTask().execute(statusCheckUrl);
    }

    // Get the edit text input and call the background thread
    private void runCode() {
        mCodeInput = "";
        // get the input code from the editext
        mCodeInput = mCodeInput.concat(mEditorEditText.getText().toString());

        // check if there is something or not
        if (mCodeInput == null) {
            mStatusTextView.setText("Please enter something.");
            return;
        }

        URL runCodeUrl = NetworkUtils.buildUrl(2);
        new RunCodeTask().execute(runCodeUrl);
    }

    /**
     * Run code in a background thread.
     */
    public class RunCodeTask extends AsyncTask<URL, Void, String> {
        // Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            // Set the text to compiling and running
            mStatusTextView.setText("Compiling and running code.");
            // Make progress bar visible
            mIndeterminateProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String outputResponse = null;
            try {
                mPostData = new JSONObject();
                try {
                    mPostData.put("id", "12213");
                    mPostData.put("haveInput", haveInput);
                    mPostData.put("language", program_lang);
                    mPostData.put("input", userInput);
                    mPostData.put("source", mEditorEditText.getText());
                } catch (JSONException e) {
                    // Auto-generated catch block
                    e.printStackTrace();
                }

                outputResponse = NetworkUtils.getRunCodeResponse(searchUrl, mPostData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return outputResponse;
        }

        @Override
        protected void onPostExecute(String jsonOutputResult) {
            // Set text to output ready
            mStatusTextView.setText("Output ready.");
            // As soon as the loading is complete, hide the loading indicator
            mIndeterminateProgress.setVisibility(View.INVISIBLE);

            String output = "";

            if (jsonOutputResult != null && !jsonOutputResult.equals("")) {
                output = NetworkUtils.extractOutput(jsonOutputResult);
                callback.onEditorInput(output);
                return;
            }
        }
    }

    /**
     * Check the server status. Check for sometime and try again if not working.
     */
    public class CheckServerStatusTask extends AsyncTask<URL, Void, Integer> {
        @Override
        protected void onPreExecute() {
            // Set the text when going to check the server
            mStatusTextView.setText("Checking server.");
            // Make progress bar visible
            mIndeterminateProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(URL... params) {
            URL searchUrl = params[0];
            int status = 0;
            try {
                status = NetworkUtils.getStatusResponse(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return status;
        }

        @Override
        protected void onPostExecute(Integer status) {
            // Make progress bar invisible
            mIndeterminateProgress.setVisibility(View.INVISIBLE);
            if (status == 200) {
                mStatusTextView.setText("Server is ok.");
                serverAvailable = true;
                return;
            } else {
                mStatusTextView.setText("status");
                serverAvailable = false;
                return;
            }
        }
    }

    // Process the results of the document provider
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // The ACTION_CREATE_DOCUMENT intent was sent with the request code
        // WRITE_REQUEST_CODE.
        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                writeToFile(uri);
                strFileName = getFileName(uri);
                mFileNameTextView.setText(strFileName);
            }
        }
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // EDIT_REQUEST_CODE.
        else if (requestCode == EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Pull that URI using resultData.getData().
            Uri uri = null;

            if (data != null) {
                uri = data.getData();

                // load edit text input
                new LoadFileTask().execute(uri);

                // Get the filename
                strFileName = getFileName(uri);
            }
        }
    }

    /**
     *  Create the text file
     */
    private void createFile(String mimeType, String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    /**
     * Open a file for writing and append some text to it.
     */
    private void editDocument() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's
        // file browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only text files.
        intent.setType("text/*");

        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }

    // Write in the text file
    private void writeToFile(Uri uri) {
        try {
            ParcelFileDescriptor pfd = getActivity().getContentResolver().
                    openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());
            fileOutputStream.write((mEditorEditText.getText().toString()).getBytes());
            // closing the stream.
            fileOutputStream.close();
            pfd.close();
            mStatusTextView.setText("File saved.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get the content of the text file
    private String readTextFromFile(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getActivity()
                .getContentResolver().openFileDescriptor(uri, "r");
        InputStream inputStream = getActivity()
                .getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        inputStream.close();
        parcelFileDescriptor.close();
        return stringBuilder.toString();
    }

    /**
     *   Load file using AsyncTask
     */
    private class LoadFileTask extends AsyncTask<Uri,Void,StringBuffer> {
        @Override
        protected void onPreExecute() {
            // Set status to loading text
            mStatusTextView.setText("Loading your file.");
            // Make the progress bar visible
            mIndeterminateProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected StringBuffer doInBackground(Uri... uris) {
            // use stringbuffer to store the text
            StringBuffer text = new StringBuffer();

            // read from file
            try {
                text.append(readTextFromFile(uris[0]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return text;
        }

        @Override
        protected void onPostExecute(StringBuffer text) {
            // Set text to file loaded
            mStatusTextView.setText("File loaded.");
            // Make the progress bar invisible
            mIndeterminateProgress.setVisibility(View.INVISIBLE);
            // set edit text input to text
            mEditorEditText.setText(text);
            // Set the filename
            mFileNameTextView.setText(strFileName);
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity()
                    .getContentResolver()
                    .query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor
                            .getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * Set the editor with some code for the user to work with
     */
    private void setupEditor(String lang) {
        String str;
        switch (lang) {
            case "C" :
                str = "#include <stdio.h>\n\nint main()" +
                        "{\n    // Your program starts here\n    printf(\"" +
                        "Hello World!\");\n    return 0;\n}";
                mEditorEditText.setText(str);
                break;
            case "cpp" :
                str = "#include <iostream.>\n\nusing namespace std;\n\nint main()" +
                        "{\n    // Your program starts here\n    cout<<\"" +
                        "Hello World!\";\n    return 0;\n}";
                mEditorEditText.setText(str);
                break;
            case "java" :
                str = "class Test {\n    public static void main(String args[]) {\n" +
                        "        // Your program starts here\n        System.out.println(\"" +
                        "Hello World!\");\n    }\n}";
                mEditorEditText.setText(str);
                break;
            case "python" :
                str = "print \"Hello World!\"";
                mEditorEditText.setText(str);
                break;
            case "python3" :
                str = "print(\"Hello World!\")";
                mEditorEditText.setText(str);
                break;
            default:
                str = "";
                mEditorEditText.setText(str);
                return;
        }
    }
}
