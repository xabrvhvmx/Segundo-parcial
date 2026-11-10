package com.example.tsdmh_7s5_p2practica3_lmua;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PrincipalActivity extends AppCompatActivity {

    TextView tvEstado, tvPorcentaje;
    ProgressBar progressBar;
    Button btnIniciar, btnCancelar;

    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Handler handler = new Handler(Looper.getMainLooper());
    Future<?> tarea;

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

        btnIniciar.setOnClickListener(v -> IniciarTarea(100));

        btnCancelar.setOnClickListener(v -> {
            if (tarea != null && !tarea.isDone()) {
                tarea.cancel(true);
            }
        });
    }

    private void IniciarTarea(int pasos) {
        tvEstado.setText(R.string.iniciando_tarea);
        btnIniciar.setEnabled(false);
        btnCancelar.setEnabled(true);
        tvPorcentaje.setText(R.string.porcentaje_cero);

        tarea = executor.submit(() -> {
            for (int i = 0; i <= pasos; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    handler.post(this::AlCancelar);
                    return;
                }
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    handler.post(this::AlCancelar);
                    Thread.currentThread().interrupt();
                    return;
                }

                final int progreso = (i * 100) / pasos;
                final String textoPorcentaje = getString(R.string.porcentaje_formato, progreso);
                handler.post(() -> {
                    tvPorcentaje.setText(textoPorcentaje);
                    progressBar.setProgress(progreso);
                });
            }
            handler.post(() -> alFinalizar(getString(R.string.proceso_completado)));
        });
    }

    private void AlCancelar() {
        tvEstado.setText(R.string.tarea_cancelada);
        tvPorcentaje.setText(R.string.porcentaje_cero);
        progressBar.setProgress(0);
        btnIniciar.setEnabled(true);
        btnCancelar.setEnabled(false);
    }

    private void alFinalizar(String resultado) {
        tvEstado.setText(resultado);
        tvPorcentaje.setText(R.string.porcentaje_cien);
        progressBar.setProgress(100);
        btnIniciar.setEnabled(true);
        btnCancelar.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tarea != null && !tarea.isDone()) {
            tarea.cancel(true);
        }
        handler.removeCallbacksAndMessages(null);
        executor.shutdown();
    }
}
