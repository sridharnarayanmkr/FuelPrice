package kickstart.mastermind.fuelprice;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.paperdb.Paper;

import static android.view.View.VISIBLE;

public class MainActivity extends Activity {
    TextView petrolText, dieselText,dieselName, petrolName;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.saveButton);
        spinner = (Spinner) findViewById(R.id.spinnerText);
        petrolText = (TextView) findViewById(R.id.petrolPrice);
        dieselText = (TextView) findViewById(R.id.dieselPrice);
        dieselName = (TextView) findViewById(R.id.dieselName);
        petrolName = (TextView) findViewById(R.id.petrolName);
        final String[] cities = {"Chennai", "New-Delhi", "Ambala", "Bhubhaneswar", "Ahmedabad", "Hyderabad", "Jaipur",
                "Kohima", "Patna", "Raipur", "Shimla", "Faridabad", "Ghaziabad", "Gangtok", "Kolkata", "Agartala",
                "Bengaluru", "Chandigarh", "Gandhinagar", "vijayawada", "Jammu", "Lucknow", "Pondicherry", "Ranchi",
                "Srinagar", "Gurgaon", "Amaravathi", "Imphal", "Mumbai", "Aizwal", "Bhopal", "Dehradun", "Guwahati",
                "Itanagar", "Jullunder", "Panjim", "Port-Blair", "Shillong", "Trivandrum", "Noida", "Silvasa"};
        Paper.init(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cities);
        spinner.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute(cities[spinner.getSelectedItemPosition()]);
                Toast.makeText(getBaseContext(), cities[spinner.getSelectedItemPosition()], Toast.LENGTH_SHORT).show();

            }
        });


    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            org.jsoup.nodes.Document doc = null;
            try {
                doc = Jsoup.connect("https://www.petroldieselprice.com/petrol-diesel-price-in-" + strings[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            org.jsoup.nodes.Element table = doc.getElementById("order_review");
            StringBuilder str = new StringBuilder();
            Elements rows = table.getElementsByTag("TR");
            for (Element row : rows) {
                Elements tds = row.getElementsByTag("TD");
                for (int i = 0; i < tds.size(); i++) {
                    Log.d("Value from web", tds.get(i).text() + "," + i);
                    str.append(tds.get(i).text() + ",");

                }
            }
            Log.d("Bundled", str.toString());
            return str.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            String[] array = s.split(",");
            petrolText.setText(array[1]);
            dieselText.setText(array[2]);
            Paper.book().write("Petrol",array[1]);
            Paper.book().write("Diesel",array[2]);
        }
    }
}
