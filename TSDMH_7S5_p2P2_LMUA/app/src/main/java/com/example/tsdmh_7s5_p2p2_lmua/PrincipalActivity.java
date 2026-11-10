package com.example.tsdmh_7s5_p2p2_lmua;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.ref.WeakReference;

public class PrincipalActivity extends AppCompatActivity {
    private TextView tvEstado, tvPorcentaje;
    private ProgressBar progressBar;
    private Button btnIniciar, btnCancelar;
    private DescargaTask tarea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvEstado = findViewById(R.id.tvEstado);
        tvPorcentaje = findViewById(R.id.tvPorcentaje);
        progressBar = findViewById(R.id.progressBar);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnCancelar = findViewById(R.id.btnCancelar);

        btnIniciar.setEnabled(true);
        btnCancelar.setEnabled(false);

        btnIniciar.setOnClickListener(v -> {
            if (tarea == null || tarea.getStatus() == AsyncTask.Status.FINISHED) {
                tarea = new DescargaTask(this);
                tarea.execute(100);
            }
        });


        btnCancelar.setOnClickListener(v -> {
            if (tarea != null && tarea.getStatus() == AsyncTask.Status.RUNNING) {
                tarea.cancel(true);
                tarea = null;
            }
        });
    }

    private static class DescargaTask extends AsyncTask<Integer, Integer, String> {
        private final WeakReference<PrincipalActivity> activityRef;

        public DescargaTask(PrincipalActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            PrincipalActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) return;
            activity.tvEstado.setText("Descargando...");
            activity.progressBar.setProgress(0);
            activity.tvPorcentaje.setText("0%");
            activity.btnIniciar.setEnabled(false);
            activity.btnCancelar.setEnabled(true);
        }

        @Override
        protected String doInBackground(Integer... params) {
            int pasos = params[0];
            for (int i = 1; i <= pasos; i++) {
                if (isCancelled()) {
                    return "Tarea cancelada";
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return "Interrupción de la tarea";
                }
                publishProgress(i * 100 / pasos);
            }
            return "Tarea finalizada";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            PrincipalActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) return;
            activity.progressBar.setProgress(values[0]);
            activity.tvPorcentaje.setText(values[0] + "%");
        }

        @Override
        protected void onPostExecute(String s) {
            PrincipalActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) return;
            activity.tvEstado.setText(s);
            activity.btnIniciar.setEnabled(true);
            activity.btnCancelar.setEnabled(false);
            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled(String result) {
            PrincipalActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) return;
            activity.tvEstado.setText(result != null ? result : "Tarea cancelada");
            activity.btnIniciar.setEnabled(true);
            activity.btnCancelar.setEnabled(false);
        }

        @Override
        protected void onCancelled() {
            PrincipalActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) return;
            activity.tvEstado.setText("Tarea cancelada");
            activity.progressBar.setProgress(0);
            activity.tvPorcentaje.setText("0%");
            activity.btnIniciar.setEnabled(true);
            activity.btnCancelar.setEnabled(false);

        }
    }
}
