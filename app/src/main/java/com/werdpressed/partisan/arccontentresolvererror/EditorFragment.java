package com.werdpressed.partisan.arccontentresolvererror;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

public class EditorFragment extends Fragment {

    private String mUri = null;
    private View rootView;
    private EditText mEditText;
    private ScrollView parent;

    public static EditorFragment newInstance() {
        EditorFragment frag = new EditorFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.editor_fragment, container, false);

        parent = (ScrollView) rootView.findViewById(R.id.ef_parent);
        mEditText = (EditText) rootView.findViewById(R.id.ef_edit_text);

        return rootView;
    }

    public String getUri(){
        return mUri;
    }

    public void setUri(String newUri){
        mUri = newUri;
    }

    public String getEditTextContent() {
        return mEditText.getText().toString();
    }

    public void setEditTextContent(String newContent) {
        mEditText.setText(newContent);
    }
}
