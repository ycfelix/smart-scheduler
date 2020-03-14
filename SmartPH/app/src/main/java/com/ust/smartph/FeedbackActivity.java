package com.ust.smartph;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        final Button button = (Button) findViewById(R.id.feedback_submit_btn);
        final EditText subject = (EditText) findViewById(R.id.feedback_subject_field);
        final EditText content = (EditText) findViewById(R.id.feedback_content_field);
        final RatingBar rating = (RatingBar) findViewById(R.id.feedback_ratingBar);
        rating.setNumStars(5);

        button.setOnClickListener((View view) -> {
            subject.onEditorAction(EditorInfo.IME_ACTION_DONE);
            content.onEditorAction(EditorInfo.IME_ACTION_DONE);
            Toast.makeText(this, "Subject: " + subject.getText() +
                    "\nContent: " + content.getText() + "\nRating: " +
                    rating.getRating(), Toast.LENGTH_SHORT).show();

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String url ="http://13.70.2.33:5000/";

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            Toast.makeText(FeedbackActivity.this,
                                    "It works! Content = " + response, Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(FeedbackActivity.this,
                            "That didn't work!", Toast.LENGTH_SHORT).show();
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        });
    }
}
