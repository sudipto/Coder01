package android.example.coder01.fragments;

import android.app.SearchManager;
import android.content.Intent;
import android.example.coder01.R;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class OutputFragment extends Fragment {
    private TextView mOutputTextView;
    private View view;
    private boolean hasOutput = false;
    private String query = null;

    public void setOutputTextView(String text) {
        query = text;
        mOutputTextView.setText(text);
        hasOutput = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate fragment_output layout for this fragment
        view = inflater.inflate(R.layout.fragment_output, container, false);

        // Find the textview
        mOutputTextView = view.findViewById(R.id.tv_output);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_output, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If there is no output then return early
        if (!hasOutput) {
            Toast.makeText(getContext(),
                    "No output available. Run code first.", Toast.LENGTH_SHORT)
                    .show();
            return true;
        }

        switch (item.getItemId()) {
            case R.id.search_error:
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, query);
                startActivity(intent);
                return true;
            case R.id.share_output:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, query);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
