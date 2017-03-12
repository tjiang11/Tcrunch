package com.toniebalonie.tjiang11.tcrunch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeacherLoginFragment.OnTeacherLoginListener} interface
 * to handle interaction events.
 * Use the {@link TeacherLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherLoginFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button teacherLoginButton;
    private Button teacherRegisterButton;
    private TextView forgotPassword;

    private EditText teacherEmail;
    private EditText teacherPassword;

    private OnTeacherLoginListener mListener;

    public TeacherLoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TeacherLoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TeacherLoginFragment newInstance() {
        TeacherLoginFragment fragment = new TeacherLoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_login, container, false);
        teacherEmail = (EditText) view.findViewById(R.id.teacher_email_input_text);
        teacherPassword = (EditText) view.findViewById(R.id.teacher_password_input_text);
        teacherLoginButton = (Button) view.findViewById(R.id.teacherLoginButton);
        teacherRegisterButton = (Button) view.findViewById(R.id.teacherRegisterButton);
        forgotPassword = (TextView) view.findViewById(R.id.forgotPassword);
        teacherLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonPressed(teacherEmail.getText().toString(), teacherPassword.getText().toString());
            }
        });
        teacherRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterButtonPressed(teacherEmail.getText().toString(), teacherPassword.getText().toString());
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForgotPasswordPressed();
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onLoginButtonPressed(String email, String password) {
        if (mListener != null) {
            mListener.onTeacherLoginPressed(email, password);
        }
    }

    public void onRegisterButtonPressed(String email, String password) {
        if (mListener != null) {
            mListener.onTeacherRegisterPressed(email, password);
        }
    }

    public void onForgotPasswordPressed() {
        if (mListener != null) {
            mListener.onForgotPasswordPressed();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTeacherLoginListener) {
            mListener = (OnTeacherLoginListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTeacherLoginListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTeacherLoginListener {
        void onTeacherLoginPressed(String email, String password);
        void onTeacherRegisterPressed(String email, String password);
        void onForgotPasswordPressed();
    }
}
