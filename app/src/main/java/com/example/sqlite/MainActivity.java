package com.example.sqlite;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText etDni, etNombre, etTelefono;
    Button btnEnviar;
    ListView lvDatos;
    SqlLiteHelper db;
    ArrayList<String> listaUsuarios;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);



        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etDni = findViewById(R.id.etDni);
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        btnEnviar = findViewById(R.id.btnEnviar);
        lvDatos = findViewById(R.id.lvDatos);

        db = new SqlLiteHelper(this);
        listaUsuarios = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaUsuarios);

        lvDatos.setAdapter(adapter);

        cargarDatos();

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarUsuario();
            }
        });

        lvDatos.setOnItemClickListener((parent, view, position, id)->{
            String usuarioSeleccionado = listaUsuarios.get(position);
            String dni = usuarioSeleccionado.split(" - ")[0];

            PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
            popupMenu.getMenuInflater().inflate(R.menu.menu_usuario, popupMenu.getMenu());
            //Codigo para centrar el menu
            int itemWidth = view.getWidth();
            int popupWidth = (int) (itemWidth * 0.5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popupMenu.setGravity(Gravity.CENTER);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                int id_aux = item.getItemId();

                if (id_aux == R.id.op_actualizar) {
                    mostrarDialogoActualizar(dni);
                    return true;
                } else if (id_aux == R.id.op_eliminar) {
                    if (db.eliminarUsuario(dni)) {
                        Toast.makeText(MainActivity.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                        cargarDatos();
                    } else {
                        Toast.makeText(MainActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }

                return false;
            });
            popupMenu.show();
        });
    }
    private void insertarUsuario(){
        String dni = etDni.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        if(dni.isEmpty() || nombre.isEmpty() || telefono.isEmpty()){
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean insertado = db.insertarUsuario(dni, nombre, telefono);
        if(insertado){
            Toast.makeText(this,"Usuario Agregado", Toast.LENGTH_SHORT).show();
            limpiarCampos();
            cargarDatos();
        } else {
            Toast.makeText(this, "Error: DNI duplicado", Toast.LENGTH_SHORT).show();
        }
    }
    private void cargarDatos(){
        listaUsuarios.clear();
        Cursor cursor = db.obtenerUsuarios();
        if(cursor.moveToFirst()){
            do{
                String dni = cursor.getString(0);
                String nombre = cursor.getString(1);
                String telefono = cursor.getString(2);
                listaUsuarios.add(dni + " - "+nombre+" - "+telefono);
            }while(cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void limpiarCampos(){
        etDni.setText("");
        etNombre.setText("");
        etTelefono.setText("");
        etDni.requestFocus();
    }

    private void mostrarDialogoActualizar(String dni){
        Cursor cursor = db.obtenerUsuarios();
        String nombreActual = "", telefonoActual = "";
        while(cursor.moveToNext()){
            if(cursor.getString(0).equals(dni)){
                nombreActual = cursor.getString(1);
                telefonoActual = cursor.getString(2);
                break;
            }
        }
        cursor.close();

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_actualizar, null);
        EditText etNuevoNombre = dialogView.findViewById(R.id.etNuevoNombre);
        EditText etNuevoTelefono = dialogView.findViewById(R.id.etNuevoTelefono);

        etNuevoNombre.setText(nombreActual);
        etNuevoTelefono.setText(telefonoActual);

        new AlertDialog.Builder(this)
                .setTitle("Actualizar Usuario")
                .setView(dialogView)
                .setPositiveButton("Actualizar", (dialog, which)->{
                    String nuevoNombre = etNuevoNombre.getText().toString().trim();
                    String nuevoTelefono = etNuevoTelefono.getText().toString().trim();

                    if(db.actualizarUsuario(dni, nuevoNombre, nuevoTelefono)){
                        Toast.makeText(this, "Usuario Actualizado", Toast.LENGTH_SHORT).show();
                        cargarDatos();
                    } else {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}