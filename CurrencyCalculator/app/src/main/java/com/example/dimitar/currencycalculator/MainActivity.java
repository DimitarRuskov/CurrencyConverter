package com.example.dimitar.currencycalculator;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnTaskCompleted, AdapterView.OnItemSelectedListener {
    private HashMap<String, Double> currencyMap;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(this);
        progress.setMessage("Retrieving currency rates");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);
        progress.show();
        fetchCurrencyRates();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("BGN");
        setSupportActionBar(toolbar);
        findViewById(R.id.calculate_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });

        findViewById(R.id.reset_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        ((Spinner)findViewById(R.id.currencySpinner)).setVisibility(View.GONE);
    }

    private void fetchCurrencyRates() {
        CurrencyRateClient client = new CurrencyRateClient(this);
        client.execute("http://api.fixer.io/latest?base=BGN");
    }

    private void calculate() {
        EditText inputRate = (EditText) findViewById(R.id.inputRate);
        EditText inputAmount = (EditText) findViewById(R.id.inputAmount);

        if(inputRate.getText().toString().trim().equals("")) {
            inputRate.setError("this field is required");
            return;
        }
        if (inputAmount.getText().toString().trim().equals("")) {
            inputAmount.setError("this field is required");
            return;
        }

        Double rate = Double.parseDouble(inputRate.getText().toString());
        Double amount = Double.parseDouble(inputAmount.getText().toString());

        Double result = amount * rate;
        String formattedResult = String.format("%.3f", result);

        TextView resultTextView = (TextView) findViewById(R.id.resultTextView);
        resultTextView.setText(formattedResult + " " + ((Spinner)findViewById(R.id.currencySpinner)).getSelectedItem().toString());
    }

    private void reset() {
        EditText inputAmount = (EditText) findViewById(R.id.inputAmount);
        TextView resultTextView = (TextView)findViewById(R.id.resultTextView);

        inputAmount.setText("");
        resultTextView.setText("waiting for input...");
    }

    @Override
    public void onTaskCompleted(Map<String, Double> result) {
        currencyMap = (HashMap<String, Double>)result;
        Spinner currencySpinner = (Spinner) findViewById(R.id.currencySpinner);
        String[] keys = result.keySet().toArray(new String[result.keySet().size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, keys);
        currencySpinner.setAdapter(adapter);
        currencySpinner.setOnItemSelectedListener(this);
        currencySpinner.setSelection(adapter.getPosition("USD"));
        ((Spinner)findViewById(R.id.currencySpinner)).setVisibility(View.VISIBLE);
        progress.hide();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String currencyKey = parent.getSelectedItem().toString();
        Double currencyRate = currencyMap.get(currencyKey);
        ((EditText) findViewById(R.id.inputRate)).setText(currencyRate.toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
